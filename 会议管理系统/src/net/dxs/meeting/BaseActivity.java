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
 * 框架层Activity
 * 常用来定义所有页面都共有一些元素。
 * @author leopold
 *
 */
public abstract class BaseActivity extends Activity {

	/**
	 * ProgressDialog进度对话框
	 */
	protected ProgressDialog pd;
	
	/**
	 * 共用对话框
	 */
	private AlertDialog.Builder adb;
	
	public static BaseActivity activity;

	
	
	/**
	 * 数据库操作工具
	 */
	public MeetDbUtil dbUtil = MeetDbUtil.getDBUtil();
	/**
	 * 布局转换器
	 */
	public LayoutInflater inflater;
	/**
	 * 标题左边的按钮
	 */
	private Button btn_left;
	/**
	 * 标题右边的按钮
	 */
	private Button btn_right;
	/**
	 * 页面标题
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
	 * 设置页面身体View
	 * @return
	 */
	public abstract View setBodyView();
	/**
	 * 处理页面标题
	 * @param btn_left	左边的按钮
	 * @param title		标题
	 * @param btn_right	右边的按钮
	 */
	public abstract void dealTitle(Button btn_left, TextView title, Button btn_right);
	/**
	 * 子类初始化
	 */
	public abstract void init();

	/**
	 * 创建公用进度对话框
	 */
	private void createPD(){
		pd = new ProgressDialog(this);
		pd.setTitle("提示");
		
	}
	/**
	 * 显示默认进度对话框
	 */
	public void showPD(){
		pd.setMessage("正在处理，请稍候...");
		if(!pd.isShowing()){
			pd.show();
		}
	}
	/**
	 * 显示进度对话框
	 * @param message 要显示的文字
	 */
	public void showPD(String message){
		pd.setMessage(message);
		if(!pd.isShowing()){
			pd.show();
		}
	}
	/**
	 * 关闭进度对话框
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
	 * 显示警告对话框
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
	 * 对话框
	 * @param title		标题
	 * @param message	显示的信息
	 * @param view		显示的View
	 * @param btn1Text	按扭1的文字
	 * @param listener1	按钮1的监听
	 * @param btn1Text	按扭2的文字
	 * @param listener1	按钮2的监听
	 * @param b			是否显示取消按钮
	 */
	public void alert(String title,String message,View view,
			String btn1Text,DialogInterface.OnClickListener listener1,
			String btn2Text,DialogInterface.OnClickListener listener2,boolean b){
		
		if(adb!=null && adb.create().isShowing()){
			return ;
		}
		
		adb = new AlertDialog.Builder(this);
		if(title==null){
			title = "提示";
		}
		adb.setTitle(title);
		
		if(message!=null){
			adb.setMessage(message);
		}
		if(view!=null){
			adb.setView(view);
		}
		if(btn1Text ==null){
			btn1Text="确定";
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
			btn2Text="取消";
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
	 * 显示Tosat
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
