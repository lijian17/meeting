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
 * ��¼
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
		btn_left.setText("����");
		btn_left.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				system_set();
			}
		});
		btn_right.setText("�˳�");
		btn_right.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				existApp();
			}
		});
		title.setText("�������ϵͳ");
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
	 * ��ʼ������
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
		//TODO:�Զ���¼
	}

	private void findView() {
		username = (EditText) findViewById(R.id.username);
		password = (EditText) findViewById(R.id.password);

		btn_login = (Button) findViewById(R.id.btn_login);

		checkbox_isSave = (CheckBox) findViewById(R.id.checkbox_isSave);
		checkbox_isAutoLogin = (CheckBox) findViewById(R.id.checkbox_isAutoLogin);
	}

	/**
	 * ���ö������
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
	 * ��Ӧ��¼��ť
	 */
	protected void doLogin() {
		if (parseData()) {
			//��ʾprogressDialog�Ի���
			showPD();
			//������ã����û���������������ݿ�
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

			//			//������ý��
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
			dismissPD();//�ر�
			switch (msg.what) {
			case 0://��¼ʧ�ܡ�
				alert("�û��������벻��ȷ");
				break;
			case 1://��¼�ɹ�,ַ����ת����һҳ�档
				showToast("��¼�ɹ�");
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
	 * �ı����е��û���
	 */
	private String str_name;
	/**
	 * �ı����е�����
	 */
	private String str_pwd;

	/**
	 * ��֤���������Ƿ���Ч
	 * 
	 * @return
	 */
	private boolean parseData() {
		str_name = username.getText().toString();
		if (str_name.equals("")) {
			showToast("�û�������Ϊ�գ�");
			return false;
		}
		str_pwd = password.getText().toString();
		if (str_pwd.equals("")) {
			showToast("���������룡");
			return false;
		}
		return true;
	}

	/**
	 * ��ʾIP���˿����öԻ���
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
				// ������ҳ��
				Constants.SERVER_IP = editTextIp.getText().toString();
				Constants.SERVER_PORT = editTextPort.getText().toString();
				dialog.dismiss();
			}
		};

		alert("��������", null, dialogView, null, listener);
	}

}