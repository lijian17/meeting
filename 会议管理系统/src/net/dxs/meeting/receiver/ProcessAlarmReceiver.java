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
 * �յ��㲥�Ժ����ӷ����������»������ݿ�
 * 
 * @author lijian
 * 
 */
public class ProcessAlarmReceiver extends BroadcastReceiver {

	private Context context;
	/**
	 * ֪ͨ
	 */
	private NotificationManager myNotiManager;

	@Override
	public void onReceive(Context context, Intent intent) {
		
		this.context = context;
		
		myNotiManager = (NotificationManager) (context
				.getSystemService(Context.NOTIFICATION_SERVICE));
		
		System.out.println("ProcessAlarmReceiver  " + Constants.sdf.format(new Date(System.currentTimeMillis())));

		// ���ӷ��������鿴�Ƿ��и��� // ���и��£������ظ���
		if (isHaveNewMeet()) {
			updateAndSave2DB();
			setNotiType(R.drawable.busy, "����֪ͨ");
		}
	}

	private List<String> meetidList;

	/**
	 * �����µĻ�����Ϣ���������ݿ�
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
	 * ����Ƿ����µĻ���
	 * @return
	 */
	protected boolean isHaveNewMeet() {
		meetidList = null;
		
		// step1:�ڱ��ز������һ��������Ϣ�ķ���ʱ�� //��һ����null
		String localLastDate = dbUtil.getLastPublishDate();
		Constants.Loglj("localLastDate:" + localLastDate);
		if (localLastDate == null) {
			localLastDate = "0";
		}
		// �����ʱ�䷢�����������������շ��ص�������
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
	 *  ����Notification
	 * */
	private void setNotiType(int iconId, String text) {
		/*
		 * �����µ�Intent����Ϊ��ѡNotification������ʱ�� ��ִ�е�Activity
		 */
		Intent notifyIntent = new Intent(context, MeetListActivity.class);
		notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		
		/* ����PendingIntent��Ϊ�趨����ִ�е�Activity */
		PendingIntent appIntent = PendingIntent.getActivity(context, 0, notifyIntent, 0);

		Notification notify = new Notification();
		/* �趨statusbar��ʾ��icon */
		notify.icon = iconId;
		/* �趨statusbar��ʾ������ѶϢ */
		notify.tickerText = text;
		/* �趨notification����ʱͬʱ����Ԥ������ */
		notify.defaults = Notification.DEFAULT_SOUND;
		//���֪ͨ�Ժ��Զ�����֪ͨ
		notify.flags=Notification.FLAG_AUTO_CANCEL;
		/* �趨Notification�������Ĳ��� */
		notify.setLatestEventInfo(context, "���»���֪ͨ����ע����գ�", text, appIntent);
		/* �ͳ�Notification */
		myNotiManager.notify(0, notify);
	}
}
