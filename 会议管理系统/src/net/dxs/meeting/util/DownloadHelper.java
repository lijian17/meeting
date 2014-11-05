package net.dxs.meeting.util;

import java.io.IOException;

import net.dxs.meeting.R;
import net.dxs.meeting.download.DownloadTask;
import net.dxs.meeting.download.DownloadTaskEvent;
import net.dxs.meeting.download.DownloadTaskListener;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class DownloadHelper {

	private Context ctx;
	private String filepath;
	
	public DownloadHelper(Context ctx,String savePath){
		this.ctx = ctx;
		this.filepath = savePath;
		
	}
	//===== ����
	
	private DownloadTask dt;
	private String dlUrl;
	/**
	 * ��������
	 * 
	 * @param dlUrl
	 */
	public void startDownload(final String dlUrl) {
		this.dlUrl = dlUrl;
		if (dt != null) {
			dt.stopAllThread();
		}
		Thread t = new Thread() {
			public void run() {
				startDown(dlUrl);
			}
		};
		t.start();
	}

	/**
	 * ��ʾ���ȶԻ���
	 */
	private final static int SHOWDOWNLOADPROGRESS = 4;
	/**
	 * ��ʾû�в���SD��
	 */
	private final static int ALERTNOSDCARD = 5;
	/**
	 * �������
	 */
	private final static int DOWNLOAD_COMPLATE = 2;
	/**
	 * �������
	 */
	protected final static int NETWORK_ERROR = -1;
	/**
	 * ���ؽ��ȷ����仯
	 */
	private final static int PROGRESS_CHANGE = 1;
	/**
	 * �����г����쳣
	 */
	private final static int DOWNLOAD_EXCEPTION = 3;
	protected final static int MSG_CANCEL_DOWNLOAD = 9;
	
	private void startDown(String dlUrl) {
			dt = new DownloadTask(dlUrl, getFilepath());
			dt.addTaskListener(new DownloadTaskListener(){
				@Override
				public void autoCallback(DownloadTaskEvent event) {
					autoCallback2(event);
				}});
			
			DownloadTask.setDebug(true);
			
			try {
				Message msg = new Message();
				msg.what = SHOWDOWNLOADPROGRESS;
				handler_update.sendMessage(msg);
				dt.startDown();
			} catch (Exception e) {
				e.printStackTrace();
				// ����һ���쳣�Ĵ��������������ʧ�ܵ���Ϣ
				Message msg = new Message();
				msg.what = NETWORK_ERROR;
				handler_update.sendMessage(msg);
			}
	}
	/**
	 * //�������Ի���ID
	 */
	final int PROGRESS_DIALOG = 0;
	ProgressBar progressBar;
	/**
	 * �����ļ��ܵ��ֽ���
	 */
	private int maxLength;
	long secondProgress;
	TextView tv2;

	/**
	 * �������Ի�������
	 */
	private AlertDialog progressDialog_download;

	/**
	 * �Ƿ�����ʾ �Ƿ����Եİ�ť
	 */
	public boolean isShowLoadRetryDialog;
	
//	private int tmpnum;

	// ����UI
	public Handler handler_update = new Handler(Looper.getMainLooper()) {
		// @Override
		public void handleMessage(Message msg) {
//			closeDialog();
			switch (msg.what) {
			case SHOWDOWNLOADPROGRESS:
				showDialog();
				progressBar.setProgress(0);
				maxLength = 100;
				progressBar.setMax(maxLength);

				break;
			case PROGRESS_CHANGE:
				 maxLength = (int) dt.getContentLength();
				 secondProgress = msg.getData().getLong("progress");
				 tv2.setText(secondProgress + "/" + maxLength + "");
				 if (secondProgress > 0) {
				 progressBar.setMax(maxLength);
				 progressBar.setProgress((int) secondProgress);
				 }
				break;
			case DOWNLOAD_COMPLATE:// �������
				progressDialog_download.dismiss();
				Toast.makeText(ctx, "�����Ѿ����", 0).show();
				break;
			case NETWORK_ERROR:
			case DOWNLOAD_EXCEPTION:
				if (dt != null) {
					dt.stopAllThread();
				}
				break;
			case ALERTNOSDCARD:
				String showStr = "�ļ�����ʧ��";
				AlertDialog errorDia = new AlertDialog.Builder(ctx).create();
				errorDia.setTitle("��ʾ");
				errorDia.setMessage(showStr);
				errorDia.setButton("����",
						new Dialog.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								startDownload(dlUrl);
							}
						});
				errorDia.setButton2("ȡ��",new Dialog.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
					}
				});
				errorDia.show();
				break;
			case MSG_CANCEL_DOWNLOAD:
				if (dt != null) {
					dt.stopAllThread();
				}
				progressDialog_download.dismiss();
				break;

			}
		}
	};
	public void chmod(String permission, String path) {
		try {
			String command = "chmod " + permission + " " + path;
			Runtime runtime = Runtime.getRuntime();
			runtime.exec(command);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * ��ʾ���ؽ��ȶԻ���
	 */
	protected void showDialog() {
		
		AlertDialog.Builder adb = new AlertDialog.Builder(ctx);
		LayoutInflater factory = LayoutInflater.from(ctx);
		// ����progress_dialogΪ�Ի���Ĳ���xml
		View view = factory.inflate(R.layout.down_dialog, null);
		view.setPadding(15, 15, 15, 15);
		TextView tv1 = (TextView) view.findViewById(R.id.textView1);
		tv1.setText(ctx.getResources().getString(R.string.loadprogress));
		ImageView iv = (ImageView) view.findViewById(R.id.imageView1);
		iv.setBackgroundResource(R.drawable.icon);
		tv2 = (TextView) view.findViewById(R.id.textView2);
		if (maxLength <= 0)
			maxLength = 100;
		tv2.setText(secondProgress + "/" + maxLength + "");
		progressBar = (ProgressBar) view.findViewById(R.id.progressBar1);

		maxLength = (int) dt.getContentLength();
		if (maxLength <= 0)
			maxLength = 100;
		progressBar.setProgress(0);
		progressBar.setMax(maxLength); // �������ֵ
		
		adb.setView(view);
		adb.setPositiveButton(ctx.getResources().getString(R.string.cancel), new Dialog.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				handler_update.sendEmptyMessage(MSG_CANCEL_DOWNLOAD);
			}
		});
		progressDialog_download = adb.create();
		
		progressDialog_download.show();
	}

	public void autoCallback2(DownloadTaskEvent event) {
		if (event.hasError()) {
			// �쳣����-------
			Message msg = new Message();
			msg.what = DOWNLOAD_EXCEPTION;
			handler_update.sendMessage(msg);
			return;
		} else {
//			int progess = (int) (event.getReceivedCount() * 100.0 / event.getTotalCount());
			Message msg = new Message();
			msg.what = PROGRESS_CHANGE;
			Bundle data = new Bundle();
			data.putLong("progress", event.getReceivedCount());
			data.putLong("max", event.getTotalCount());
			msg.setData(data);
			handler_update.sendMessage(msg);
			if (event.isComplete()) {
				synchronized (this) {
					Message msgComplate = new Message();
					msgComplate.what = DOWNLOAD_COMPLATE;
					handler_update.sendMessage(msgComplate);
				}
			}
		}
	}

	public String getFilepath() {
		return filepath;
	}
	public void setFilepath(String filepath){
		this.filepath = filepath;
	}
}
