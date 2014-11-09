package net.dxs.meeting.app;

import java.util.HashMap;

import net.dxs.meeting.net.meet.MeetBean;
import net.dxs.meeting.util.MeetDbUtil;
import android.app.Application;

/**
 * Ӧ�ó�����
 * 
 * @author lijian
 * 
 */
public class NetMeetApp extends Application {

	public static NetMeetApp app;

	private MeetDbUtil dbUtil;

	/**
	 * ��Ӧ�õ�������Ϣ
	 */
	public HashMap<String, String> setInfo = new HashMap<String, String>();

	/**
	 * ��ʱ����meetBean;
	 */
	public MeetBean netMeetCatche;

	@Override
	public void onCreate() {
		app = this;
		dbUtil = MeetDbUtil.getDBUtil();

		setInfo = dbUtil.getSetInfo();

	}

}
