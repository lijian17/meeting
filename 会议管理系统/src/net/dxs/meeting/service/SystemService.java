package net.dxs.meeting.service;

import net.dxs.meeting.util.Constants;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

/**
 * ��̨����
 * 
 * @author lijian
 * 
 */
public class SystemService extends Service {
	/**
	 * ����ʱ�ӹ�����
	 */
	private AlarmManager alarmManager;
	/**
	 * ����PendingIntent
	 */
	private PendingIntent pendIntent;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
	}

	@Override
	public void onStart(Intent intent, int startId) {
		Constants.Loglj("SystemService-onStartCommand");

		long currentTime = System.currentTimeMillis();

		long interValue = 1000 * 60 * 1; // ÿ1����
		Intent intent1 = new Intent("net.dxs.action.PROCESS_ALARM");

		pendIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent1, 0); // ��ȡһ����ִ�й㲥�¼���PendingIntent

		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, currentTime, interValue, pendIntent); // �����趨��ʱ�������ϵķ��͹㲥�¼�
	}

	@Override
	public void onDestroy() {
		Constants.Loglj("SystemService :: onDestroy()");
		super.onDestroy();
		if (alarmManager != null) {
			alarmManager.cancel(pendIntent); // ֹͣ�㲥�¼�
		}
	}
}
