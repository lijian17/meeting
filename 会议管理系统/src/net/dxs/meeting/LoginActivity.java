package net.dxs.meeting;

import java.util.HashMap;

import net.dxs.meeting.app.NetMeetApp;
import net.dxs.meeting.net_new.login.LoginBean;
import net.dxs.meeting.net_new.login.LoginHelper;
import net.dxs.meeting.net_new.netmodle.NetClient;
import net.dxs.meeting.util.Constants;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;

/**
 * 登录
 * 
 * @author lijian
 * 
 */
public class LoginActivity extends BaseActivity {
	private EditText username;
	/*
	 * 
	 */
	private EditText password;

	private Button btn_login;

	private CheckBox checkbox_isSave;
	private CheckBox checkbox_isAutoLogin;

	@Override
	public View setBodyView() {
		return inflater.inflate(R.layout.dxs_activity_login, null);
	}

	@Override
	public void dealTitle(Button btn_left, TextView title, Button btn_right) {
		btn_left.setText("设置");
		btn_left.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				system_set();
			}
		});
		btn_right.setText("退出");
		btn_right.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				existApp();
			}
		});
		title.setText("会议管理系统");
	}

	@Override
	public void init() {
		sysInfo = ((NetMeetApp) this.getApplication()).setInfo;
		findView();
		setListener();
		initView();
	}

	HashMap<String, String> sysInfo;

	/**
	 * 初始化界面
	 */
	private void initView() {
		String isSave = sysInfo.get("isSave");
		checkbox_isSave.setChecked(("1".equals(isSave)) ? true : false);
		if ("1".equals(isSave)) {

			String name = sysInfo.get("username");
			username.setText((name == null) ? "" : name);

			String pwd = sysInfo.get("password");
			password.setText((pwd == null) ? "" : pwd);
		}

		String isAuto = sysInfo.get("isAutoLogin");
		if ("1".equals(isAuto)) {
			checkbox_isAutoLogin.setChecked(true);
		}
		//TODO:自动登录
	}

	private void findView() {
		username = (EditText) findViewById(R.id.username);
		password = (EditText) findViewById(R.id.password);

		btn_login = (Button) findViewById(R.id.btn_login);

		checkbox_isSave = (CheckBox) findViewById(R.id.checkbox_isSave);
		checkbox_isAutoLogin = (CheckBox) findViewById(R.id.checkbox_isAutoLogin);
	}

	/**
	 * 设置对象监听
	 */
	private void setListener() {

		checkbox_isSave.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				String str = isChecked ? "1" : "0";

				dbUtil.saveSettingToDb("isSave", str);
				Constants.Loglj(NetMeetApp.app.setInfo.get("isSave"));
			}
		});
		checkbox_isAutoLogin.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				String str = isChecked ? "1" : "0";

				dbUtil.saveSettingToDb("isAutoLogin", str);
				Constants.Loglj(NetMeetApp.app.setInfo.get("isAutoLogin"));
			}
		});
		btn_login.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				doLogin();
			}
		});
	}

	/**
	 * 响应登录按钮
	 */
	protected void doLogin() {
		if (parseData()) {
			//显示progressDialog对话框
			showPD();
			//检查设置，将用户名和密码存入数据库
			if (checkbox_isSave.isChecked()) {

				dbUtil.saveSettingToDb("username", str_name);
				dbUtil.saveSettingToDb("password", str_pwd);
			}

			final LoginHelper login = new LoginHelper(str_name, str_pwd);

			new Thread() {
				public void run() {

					LoginBean loginBean = (LoginBean) NetClient.sendReqSyn(login);

					int returnCole = Integer.parseInt(loginBean.getReturnCode());

					switch (returnCole) {
					case 1:
						handler.sendEmptyMessage(1);
						break;
					case 0:
						handler.sendEmptyMessage(0);
						break;

					default:
						break;
					}

				};
			}.start();

			//			NetClient.execute(login, new BaseBeanListener() {
			//
			//				@Override
			//				public void dealResult(BaseBean bean) {
			//					LoginBean loginBean = (LoginBean) bean;
			//					int returnCole = Integer.parseInt(loginBean.getReturnCode());
			//					switch (returnCole) {
			//					case 1:
			//						handler.sendEmptyMessage(1);
			//						break;
			//					case 0:
			//						handler.sendEmptyMessage(0);
			//						break;
			//
			//					default:
			//						break;
			//					}
			//				}
			//			});

			//			//联网获得结果
			//			LoginRequest request = new LoginRequest(str_name, str_pwd);
			//			NetClient.execute(request, new ResponseListener() {
			//				@Override
			//				public void dealResponse(BaseResponse response) {
			//
			//					LoginBean bean = (LoginBean) response.getBean();
			//					int returnCole = Integer.parseInt(bean.getReturnCode());
			//					switch (returnCole) {
			//					case 1:
			//						handler.sendEmptyMessage(1);
			//						break;
			//					case 0:
			//						handler.sendEmptyMessage(0);
			//						break;
			//
			//					default:
			//						break;
			//					}
			//				}
			//			});
		}
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			dismissPD();//关闭
			switch (msg.what) {
			case 0://登录失败。
				alert("用户名或密码不正确");
				break;
			case 1://登录成功,址接跳转至下一页面。
				showToast("登录成功");
				Intent intent = new Intent(LoginActivity.this, MeetListActivity.class);
				startActivity(intent);
				finish();
				break;

			default:

				break;
			}
		}

	};
	/**
	 * 文本框中的用户名
	 */
	private String str_name;
	/**
	 * 文本框中的密码
	 */
	private String str_pwd;

	/**
	 * 验证输入数据是否有效
	 * 
	 * @return
	 */
	private boolean parseData() {
		str_name = username.getText().toString();
		if (str_name.equals("")) {
			showToast("用户名不能为空！");
			return false;
		}
		str_pwd = password.getText().toString();
		if (str_pwd.equals("")) {
			showToast("请输入密码！");
			return false;
		}
		return true;
	}

	/**
	 * 显示IP及端口设置对话框
	 */
	public void system_set() {
		View dialogView = inflater.inflate(R.layout.main_dialog_view, null);
		final EditText editTextIp = (EditText) dialogView.findViewById(R.id.dialog_serverIP);
		editTextIp.setText(Constants.SERVER_IP);

		final EditText editTextPort = (EditText) dialogView.findViewById(R.id.dialog_serverPort);
		editTextPort.setText(Constants.SERVER_PORT);

		OnClickListener listener = new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// 打开下载页面
				Constants.SERVER_IP = editTextIp.getText().toString();
				Constants.SERVER_PORT = editTextPort.getText().toString();
				dialog.dismiss();
			}
		};

		alert("网络设置", null, dialogView, null, listener);
	}

}