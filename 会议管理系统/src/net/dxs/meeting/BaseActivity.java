package net.dxs.meeting;

import net.dxs.meeting.util.Constants;
import net.dxs.meeting.util.MeetDbUtil;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * ��ܲ�Activity
 * ��������������ҳ�涼����һЩԪ�ء�
 * @author leopold
 *
 */
public abstract class BaseActivity extends Activity {

	/**
	 * ProgressDialog���ȶԻ���
	 */
	protected ProgressDialog pd;
	
	/**
	 * ���öԻ���
	 */
	private AlertDialog.Builder adb;
	
	public static BaseActivity activity;

	
	
	/**
	 * ���ݿ��������
	 */
	public MeetDbUtil dbUtil = MeetDbUtil.getDBUtil();
	/**
	 * ����ת����
	 */
	public LayoutInflater inflater;
	/**
	 * ������ߵİ�ť
	 */
	private Button btn_left;
	/**
	 * �����ұߵİ�ť
	 */
	private Button btn_right;
	/**
	 * ҳ�����
	 */
	private TextView title;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = this;
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		inflater = LayoutInflater.from(this);
		
		View titleView = inflater.inflate(R.layout.title, null);
		LinearLayout bodyLayout = (LinearLayout) titleView.findViewById(R.id.frame_body);
		btn_left = (Button) titleView.findViewById(R.id.btn_left);
		btn_right = (Button) titleView.findViewById(R.id.btn_right);
		title = (TextView) titleView.findViewById(R.id.title_text);
		
		bodyLayout.addView(setBodyView());
		dealTitle(btn_left,title,btn_right);
		
		setContentView(titleView);
		createPD();
		init();
	}
	
	/**
	 * ����ҳ������View
	 * @return
	 */
	public abstract View setBodyView();
	/**
	 * ����ҳ�����
	 * @param btn_left	��ߵİ�ť
	 * @param title		����
	 * @param btn_right	�ұߵİ�ť
	 */
	public abstract void dealTitle(Button btn_left, TextView title, Button btn_right);
	/**
	 * �����ʼ��
	 */
	public abstract void init();

	/**
	 * �������ý��ȶԻ���
	 */
	private void createPD(){
		pd = new ProgressDialog(this);
		pd.setTitle("��ʾ");
		
	}
	/**
	 * ��ʾĬ�Ͻ��ȶԻ���
	 */
	public void showPD(){
		pd.setMessage("���ڴ������Ժ�...");
		if(!pd.isShowing()){
			pd.show();
		}
	}
	/**
	 * ��ʾ���ȶԻ���
	 * @param message Ҫ��ʾ������
	 */
	public void showPD(String message){
		pd.setMessage(message);
		if(!pd.isShowing()){
			pd.show();
		}
	}
	/**
	 * �رս��ȶԻ���
	 */
	public void dismissPD() {
		if(pd.isShowing()){
			pd.dismiss();
		}
	}
	
	public Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			dismissPD();
			switch (msg.what) {
			case Constants.NET_ERROR:
				alert((String) msg.getData().get("errorDes"));
				break;

			default:
				break;
			}
		}
		
	};
	/**
	 * ��ʾ����Ի���
	 * @param message
	 */
	public void alert(String message) {
		
		alert(null,message,null,null,null,null,null,false);
	}

	/**
	 * 
	 * @param title
	 * @param message
	 * @param view
	 * @param btn1Text
	 * @param listener1
	 */
	public void alert(String title,String message,View view,
			String btn1Text,DialogInterface.OnClickListener listener1) {
		alert(title,message,view,btn1Text,listener1,null,null,true);
	}
	/**
	 * �Ի���
	 * @param title		����
	 * @param message	��ʾ����Ϣ
	 * @param view		��ʾ��View
	 * @param btn1Text	��Ť1������
	 * @param listener1	��ť1�ļ���
	 * @param btn1Text	��Ť2������
	 * @param listener1	��ť2�ļ���
	 * @param b			�Ƿ���ʾȡ����ť
	 */
	public void alert(String title,String message,View view,
			String btn1Text,DialogInterface.OnClickListener listener1,
			String btn2Text,DialogInterface.OnClickListener listener2,boolean b){
		
		if(adb!=null && adb.create().isShowing()){
			return ;
		}
		
		adb = new AlertDialog.Builder(this);
		if(title==null){
			title = "��ʾ";
		}
		adb.setTitle(title);
		
		if(message!=null){
			adb.setMessage(message);
		}
		if(view!=null){
			adb.setView(view);
		}
		if(btn1Text ==null){
			btn1Text="ȷ��";
		}
		if (listener1 != null) {
			adb.setPositiveButton(btn1Text, listener1);
		}else{
			adb.setPositiveButton(btn1Text, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
		}
		if(btn2Text ==null){
			btn2Text="ȡ��";
		}
		if (listener2 != null) {
			adb.setPositiveButton(btn2Text, listener1);
		}
		if(b){
			adb.setNegativeButton(btn2Text, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
		}
		adb.create();
		adb.show();
		
	}
	
	/**
	 * ��ʾTosat
	 * @param string
	 */
	public void showToast(String string) {
		Toast.makeText(this, string, 0).show();
	}
	public void existApp(){
		finish();
		android.os.Process.killProcess(android.os.Process.myPid());
	}
	
}
