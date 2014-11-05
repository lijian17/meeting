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
	//===== 下载
	
	private DownloadTask dt;
	private String dlUrl;
	/**
	 * 升级操作
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
	 * 显示进度对话框
	 */
	private final static int SHOWDOWNLOADPROGRESS = 4;
	/**
	 * 提示没有插入SD卡
	 */
	private final static int ALERTNOSDCARD = 5;
	/**
	 * 下载完成
	 */
	private final static int DOWNLOAD_COMPLATE = 2;
	/**
	 * 网络错误
	 */
	protected final static int NETWORK_ERROR = -1;
	/**
	 * 下载进度发生变化
	 */
	private final static int PROGRESS_CHANGE = 1;
	/**
	 * 下载中出现异常
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
				// 加入一个异常的处理，如果发送下载失败的消息
				Message msg = new Message();
				msg.what = NETWORK_ERROR;
				handler_update.sendMessage(msg);
			}
	}
	/**
	 * //进度条对话框ID
	 */
	final int PROGRESS_DIALOG = 0;
	ProgressBar progressBar;
	/**
	 * 下载文件总的字节数
	 */
	private int maxLength;
	long secondProgress;
	TextView tv2;

	/**
	 * 进度条对话框引用
	 */
	private AlertDialog progressDialog_download;

	/**
	 * 是否有显示 是否重试的按钮
	 */
	public boolean isShowLoadRetryDialog;
	
//	private int tmpnum;

	// 更新UI
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
			case DOWNLOAD_COMPLATE:// 下载完成
				progressDialog_download.dismiss();
				Toast.makeText(ctx, "下载已经完成", 0).show();
				break;
			case NETWORK_ERROR:
			case DOWNLOAD_EXCEPTION:
				if (dt != null) {
					dt.stopAllThread();
				}
				break;
			case ALERTNOSDCARD:
				String showStr = "文件创建失败";
				AlertDialog errorDia = new AlertDialog.Builder(ctx).create();
				errorDia.setTitle("提示");
				errorDia.setMessage(showStr);
				errorDia.setButton("重试",
						new Dialog.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								startDownload(dlUrl);
							}
						});
				errorDia.setButton2("取消",new Dialog.OnClickListener() {
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
	 * 显示下载进度对话框
	 */
	protected void showDialog() {
		
		AlertDialog.Builder adb = new AlertDialog.Builder(ctx);
		LayoutInflater factory = LayoutInflater.from(ctx);
		// 加载progress_dialog为对话框的布局xml
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
		progressBar.setMax(maxLength); // 进度最大值
		
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
			// 异常处理-------
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
