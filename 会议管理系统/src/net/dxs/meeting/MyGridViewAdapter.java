package net.dxs.meeting;

import java.text.SimpleDateFormat;
import java.util.List;

import net.dxs.meeting.app.NetMeetApp;
import net.dxs.meeting.net.meet.MeetBean;
import net.dxs.meeting.util.Constants;
import net.dxs.meeting.util.MeetDbUtil;
import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MyGridViewAdapter extends BaseAdapter {

	private Activity activity;
	/**
	 * 会议bean列表
	 */
	private List<MeetBean> meets;
	
	private SimpleDateFormat sdf = Constants.sdf;
	/**
	 * 数据库操作工具类
	 */
	private MeetDbUtil dbUtil = MeetDbUtil.getDBUtil();
	
	
	public MyGridViewAdapter(Activity act){
		this.activity = act;
		this.meets = dbUtil.getAllMeetBaseInfo();
		
	}
	

	@Override
	public int getCount() {
		return meets.size();
	}

	@Override
	public Object getItem(int position) {
		return meets.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final MeetBean meet = meets.get(position);
		ItemTag itemTag = null;
		if(convertView != null){
			itemTag = (ItemTag) convertView.getTag();
		}else{
			itemTag = new ItemTag();
			LayoutInflater inflater = LayoutInflater.from(activity);
			convertView = inflater.inflate(R.layout.gridviewitem, null);
			
			itemTag.meetdate = (TextView) convertView.findViewById(R.id.griditem_meetdate);
			
			itemTag.meetplace =(TextView) convertView.findViewById(R.id.griditem_meetplace);
			
			itemTag.meetmanager = (TextView) convertView.findViewById(R.id.griditem_meetmanager);
			
			itemTag.meetname = (TextView) convertView.findViewById(R.id.griditem_meetname);
			
			convertView.setTag(itemTag);
			
			convertView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					((NetMeetApp)activity.getApplication()).netMeetCatche = meet;//将当前meet缓存
					
					Intent intent = new Intent(activity,MeetDetail.class);
					activity.startActivity(intent);
					activity.finish();
				}
			});
		}
		
		itemTag.meetdate.setText(sdf.format(meet.getMeetdate()));
		itemTag.meetname.setText(meet.getMeetname());
		itemTag.meetplace.setText(meet.getMeetplace());
		itemTag.meetmanager.setText(meet.getMeetmanager());
		
		return convertView;
	}

	class ItemTag {
		public TextView meetdate;
		public TextView meetplace;
		public TextView meetmanager;
		public TextView meetname;
	}

}
