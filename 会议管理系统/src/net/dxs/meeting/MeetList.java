package net.dxs.meeting;

import java.util.List;

import net.dxs.meeting.net.meet.MeetBean;
import net.dxs.meeting.net.meet.MeetRequest;
import net.dxs.meeting.net.meetidlist.MeetIdListBean;
import net.dxs.meeting.net.meetidlist.MeetIdListRequest;
import net.dxs.meeting.net.netmodule.BaseResponse;
import net.dxs.meeting.net.netmodule.ResponseListener;
import net.dxs.meeting.net_new.netmodle.NetClient;
import net.dxs.meeting.service.SystemService;
import net.dxs.meeting.util.Constants;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

/**
 * 会议列表
 * @author leopold
 *
 */
public class MeetList extends BaseActivity {
	private GridView gridView;
	/**
	 * 有新会议信息
	 */
	private final int NEW_MEETING = 1;
	/**
	 * 没有新会议
	 */
	private final int NO_NEW_MEETING = 2;
	/**
	 * 完成更新一条会议记录
	 */
	private final int UPDATE_ONE = 3;
	

	@Override
	public View setBodyView() {
		return inflater.inflate(R.layout.meetlist, null);
	}
	
	@Override
	public void dealTitle(Button btn_left, TextView title, Button btn_right) {
		btn_left.setVisibility(View.INVISIBLE);
		title.setText("会议列表");
		btn_right.setText("更新");
		btn_right.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				updateMess();
			}
		});
	}

	@Override
	public void init() {
		gridView = (GridView) findViewById(R.id.gridview);
		
		/*
		 * 轮询的服务
		 */
		Intent intent = new Intent(this,SystemService.class);
		startService(intent);
		
		/*
		 * 开启推送服务
		 */
//        ServiceManager serviceManager = new ServiceManager(this);
//        serviceManager.startService();
        
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		showMeetInfo();
	}

	/**
	 * 显示会议列表
	 */
	public void showMeetInfo(){
		gridView.setAdapter(new MyGridViewAdapter(this));
	}
	
	Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case NEW_MEETING:
				showPD("有新会议了，正在查收，请稍后....");
				updateMeetMessages();
				break;
			case UPDATE_ONE:
				meetIdCont++;
				Constants.Logleo("update_one :"+meetIdCont);
				
				if(meetIdCont == meetidList.size()){
					dismissPD();
					showMeetInfo();
				}
			case NO_NEW_MEETING:
				dismissPD();
				showToast("暂时没有会议通知");
				break;
			default:
				break;
			}
		}
	};
	
	private List<String> meetidList;
	/**
	 * 保存过的会议ID数目
	 */
	private int meetIdCont = 0;
	
	/**
	 * 手动更新会议信息
	 */
	private void updateMess() {
		showPD("正在检查更新，请稍后....");
		
		/*
		 * 在本地查找最近一条会议信息的发布时间	，第一次是null
		 */
		String localLastDate = dbUtil.getLastPublishDate();
		Constants.Logleo("localLastDate:"+localLastDate);
		if(localLastDate==null){
			localLastDate="0";
		}
		//将这个时间发送至服务器，并接收返回的流数据
		MeetIdListRequest idsReq = new MeetIdListRequest(localLastDate);
		NetClient.execute(idsReq, new ResponseListener() {
			@Override
			public void dealResponse(BaseResponse response) {
				MeetIdListBean bean = (MeetIdListBean) response.getBean();
				int code = Integer.parseInt(bean.getReturnCode());
				switch (code) {
				case 1://有新会议
					meetIdCont = 0;
					meetidList = bean.getMeetIdList();
					handler.sendEmptyMessage(NEW_MEETING);
					break;
				case 0://无新会议
					handler.sendEmptyMessage(NO_NEW_MEETING);
					break;
				default:
					break;
				}
			}
		});
	}

	/**
	 * 更新会议数据
	 */
	protected void updateMeetMessages() {
		Constants.Logleo(""+meetidList);
		
		for(String meetid:meetidList){	
			MeetRequest meetReq = new MeetRequest(meetid);
			NetClient.execute(meetReq, new ResponseListener() {
				@Override
				public void dealResponse(BaseResponse response) {
					MeetBean bean = (MeetBean) response.getBean();
					dbUtil.addMeet(bean);
					handler.sendEmptyMessage(UPDATE_ONE);
				}
			});
		}
	}
}