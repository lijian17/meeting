package net.dxs.meeting.receiver;

import java.util.Date;
import java.util.List;

import net.dxs.meeting.MeetListActivity;
import net.dxs.meeting.R;
import net.dxs.meeting.net.meet.MeetBean;
import net.dxs.meeting.net.meet.MeetRequest;
import net.dxs.meeting.net.meetidlist.MeetIdListBean;
import net.dxs.meeting.net.meetidlist.MeetIdListRequest;
import net.dxs.meeting.net.netmodule.BaseResponse;
import net.dxs.meeting.net.netmodule.NetClient;
import net.dxs.meeting.util.Constants;
import net.dxs.meeting.util.MeetDbUtil;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * 收到广播以后，连接服务器，更新会议数据库
 * 
 * @author lijian
 * 
 */
public class ProcessAlarmReceiver extends BroadcastReceiver {

	private Context context;
	/**
	 * 通知
	 */
	private NotificationManager myNotiManager;

	@Override
	public void onReceive(Context context, Intent intent) {
		
		this.context = context;
		
		myNotiManager = (NotificationManager) (context
				.getSystemService(Context.NOTIFICATION_SERVICE));
		
		System.out.println("ProcessAlarmReceiver  " + Constants.sdf.format(new Date(System.currentTimeMillis())));

		// 连接服务器，查看是否有更新 // 若有更新，则下载更新
		if (isHaveNewMeet()) {
			updateAndSave2DB();
			setNotiType(R.drawable.busy, "会议通知");
		}
	}

	private List<String> meetidList;

	/**
	 * 下载新的会议信息并存入数据库
	 */
	protected void updateAndSave2DB() {
		System.out.println(meetidList);
		for (String meetid : meetidList) {	
			MeetRequest meetReq = new MeetRequest(meetid);
			BaseResponse response = NetClient.sendReqSyn(meetReq);
			
			if(!response.isHaveError){
				MeetBean bean = (MeetBean) response.getBean();
				dbUtil.addMeet(bean);
			}
		}
	}
	private MeetDbUtil dbUtil = MeetDbUtil.getDBUtil();
	
	/**
	 * 检查是否有新的会议
	 * @return
	 */
	protected boolean isHaveNewMeet() {
		meetidList = null;
		
		// step1:在本地查找最近一条会议信息的发布时间 //第一次是null
		String localLastDate = dbUtil.getLastPublishDate();
		Constants.Loglj("localLastDate:" + localLastDate);
		if (localLastDate == null) {
			localLastDate = "0";
		}
		// 将这个时间发送至服务器，并接收返回的流数据
		MeetIdListRequest idsReq = new MeetIdListRequest(localLastDate);
		BaseResponse response = NetClient.sendReqSyn(idsReq);
		
		if (!response.isHaveError) {
			MeetIdListBean bean = (MeetIdListBean) response.getBean();
			int code = Integer.parseInt(bean.getReturnCode());
			if (code == 1) {
				meetidList = bean.getMeetIdList();
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	/**
	 *  发出Notification
	 * */
	private void setNotiType(int iconId, String text) {
		/*
		 * 建立新的Intent，作为点选Notification留言条时， 会执行的Activity
		 */
		Intent notifyIntent = new Intent(context, MeetListActivity.class);
		notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		
		/* 建立PendingIntent作为设定递延执行的Activity */
		PendingIntent appIntent = PendingIntent.getActivity(context, 0, notifyIntent, 0);

		Notification notify = new Notification();
		/* 设定statusbar显示的icon */
		notify.icon = iconId;
		/* 设定statusbar显示的文字讯息 */
		notify.tickerText = text;
		/* 设定notification发生时同时发出预设声音 */
		notify.defaults = Notification.DEFAULT_SOUND;
		//点击通知以后，自动消除通知
		notify.flags=Notification.FLAG_AUTO_CANCEL;
		/* 设定Notification留言条的参数 */
		notify.setLatestEventInfo(context, "有新会议通知，请注意查收！", text, appIntent);
		/* 送出Notification */
		myNotiManager.notify(0, notify);
	}
}
