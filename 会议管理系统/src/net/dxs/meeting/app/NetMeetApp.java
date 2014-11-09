package net.dxs.meeting.app;

import java.util.HashMap;

import net.dxs.meeting.net.meet.MeetBean;
import net.dxs.meeting.util.MeetDbUtil;
import android.app.Application;

/**
 * 应用程序类
 * 
 * @author lijian
 * 
 */
public class NetMeetApp extends Application {

	public static NetMeetApp app;

	private MeetDbUtil dbUtil;

	/**
	 * 本应用的设置信息
	 */
	public HashMap<String, String> setInfo = new HashMap<String, String>();

	/**
	 * 临时缓存meetBean;
	 */
	public MeetBean netMeetCatche;

	@Override
	public void onCreate() {
		app = this;
		dbUtil = MeetDbUtil.getDBUtil();

		setInfo = dbUtil.getSetInfo();

	}

}
