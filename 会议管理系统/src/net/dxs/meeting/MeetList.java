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
 * �����б�
 * @author leopold
 *
 */
public class MeetList extends BaseActivity {
	private GridView gridView;
	/**
	 * ���»�����Ϣ
	 */
	private final int NEW_MEETING = 1;
	/**
	 * û���»���
	 */
	private final int NO_NEW_MEETING = 2;
	/**
	 * ��ɸ���һ�������¼
	 */
	private final int UPDATE_ONE = 3;
	

	@Override
	public View setBodyView() {
		return inflater.inflate(R.layout.meetlist, null);
	}
	
	@Override
	public void dealTitle(Button btn_left, TextView title, Button btn_right) {
		btn_left.setVisibility(View.INVISIBLE);
		title.setText("�����б�");
		btn_right.setText("����");
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
		 * ��ѯ�ķ���
		 */
		Intent intent = new Intent(this,SystemService.class);
		startService(intent);
		
		/*
		 * �������ͷ���
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
	 * ��ʾ�����б�
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
				showPD("���»����ˣ����ڲ��գ����Ժ�....");
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
				showToast("��ʱû�л���֪ͨ");
				break;
			default:
				break;
			}
		}
	};
	
	private List<String> meetidList;
	/**
	 * ������Ļ���ID��Ŀ
	 */
	private int meetIdCont = 0;
	
	/**
	 * �ֶ����»�����Ϣ
	 */
	private void updateMess() {
		showPD("���ڼ����£����Ժ�....");
		
		/*
		 * �ڱ��ز������һ��������Ϣ�ķ���ʱ��	����һ����null
		 */
		String localLastDate = dbUtil.getLastPublishDate();
		Constants.Logleo("localLastDate:"+localLastDate);
		if(localLastDate==null){
			localLastDate="0";
		}
		//�����ʱ�䷢�����������������շ��ص�������
		MeetIdListRequest idsReq = new MeetIdListRequest(localLastDate);
		NetClient.execute(idsReq, new ResponseListener() {
			@Override
			public void dealResponse(BaseResponse response) {
				MeetIdListBean bean = (MeetIdListBean) response.getBean();
				int code = Integer.parseInt(bean.getReturnCode());
				switch (code) {
				case 1://���»���
					meetIdCont = 0;
					meetidList = bean.getMeetIdList();
					handler.sendEmptyMessage(NEW_MEETING);
					break;
				case 0://���»���
					handler.sendEmptyMessage(NO_NEW_MEETING);
					break;
				default:
					break;
				}
			}
		});
	}

	/**
	 * ���»�������
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