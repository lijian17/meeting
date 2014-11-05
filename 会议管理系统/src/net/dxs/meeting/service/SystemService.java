package net.dxs.meeting.service;

import net.dxs.meeting.util.Constants;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

/**
 * 后台服务
 * @author leopold
 *
 */
public class SystemService extends Service{
	/**
	 * 声明时钟管理器
	 */
	private AlarmManager alarmManager; 
	/**
	 *  声明PendingIntent
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
		Constants.Logleo("SystemService-onStartCommand");

		long currentTime = System.currentTimeMillis();

		long interValue = 1000 * 60 * 1; // 每1分钟
		Intent intent1 = new Intent("com.heima.action.PROCESS_ALARM"); 

		pendIntent = PendingIntent.getBroadcast(getApplicationContext(), 0,
				intent1, 0); // 获取一个将执行广播事件的PendingIntent

		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, currentTime,
				interValue, pendIntent); // 根据设定的时间间隔不断的发送广播事件
	}

	@Override
	public void onDestroy() {
		Constants.Logleo("SystemService :: onDestroy()");
		super.onDestroy();
		if (alarmManager != null) {
			alarmManager.cancel(pendIntent); // 停止广播事件
		}
	}
}
