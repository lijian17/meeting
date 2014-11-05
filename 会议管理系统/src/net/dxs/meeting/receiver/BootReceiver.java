package net.dxs.meeting.receiver;

import net.dxs.meeting.app.NetMeetApp;
import net.dxs.meeting.service.SystemService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		
		if ("1".equals(NetMeetApp.app.setInfo.get("isAutoLogin"))) {
			Intent service = new Intent(context, SystemService.class);
			context.startService(service); // Æô¶¯¼àÌý·þÎñ
		}
	}
}
