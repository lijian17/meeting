package net.dxs.meeting;

import java.io.File;
import java.util.List;

import net.dxs.meeting.app.NetMeetApp;
import net.dxs.meeting.net.meet.MeetBean;
import net.dxs.meeting.util.Constants;
import net.dxs.meeting.util.DownloadHelper;
import net.dxs.meeting.util.FileType;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
/**
 * 会议详情页面
 * @author leopold
 *
 */
public class MeetDetail extends BaseActivity {

	private ListView listView;
	private TextView mc_meetname;
	private TextView mc_meetdate;
	private TextView mc_meetplace;
	private TextView mc_meetmanager;
	private TextView mc_meetpeople;

	private MeetBean meet;
	private MyListAdapter myListAdapter;
	
	@Override
	public View setBodyView() {
		return inflater.inflate(R.layout.meet_detail, null);
	}

	@Override
	public void dealTitle(Button btn_left, TextView title, Button btn_right) {
		btn_left.setText("后退");
		btn_left.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MeetDetail.this,MeetListActivity.class);
				startActivity(intent);
				finish();
			}
		});
		btn_right.setText("退出");
		btn_right.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				existApp();
			}
		});
		title.setText("会议内容");
	}

	@Override
	public void init() {
//		btn_fallback = (Button) findViewById(R.id.btn_fallback);
//		btn_exit = (Button) findViewById(R.id.btn_exit);
		
		meet = ((NetMeetApp)getApplication()).netMeetCatche;
		((NetMeetApp)getApplication()).netMeetCatche=null;
		
		mc_meetname = (TextView) findViewById(R.id.mc_meetname);
		mc_meetname.setText("会议名称：" + meet.getMeetname());

		mc_meetdate = (TextView) findViewById(R.id.mc_meetdate);
		mc_meetdate.setText("会议日期：" + Constants.sdf.format(meet.getMeetdate()));

		mc_meetplace = (TextView) findViewById(R.id.mc_meetplace);
		mc_meetplace.setText("会议地点：" + meet.getMeetplace());

		mc_meetmanager = (TextView) findViewById(R.id.mc_meetmanager);
		mc_meetmanager.setText("会议主管：" + meet.getMeetmanager());

		mc_meetpeople = (TextView) findViewById(R.id.mc_meetpeople);
		mc_meetpeople.setText("与会人员：" + meet.getMeetpeoples());

		listView = (ListView) findViewById(R.id.mc_file_list);

		myListAdapter = new MyListAdapter(this);
		listView.setAdapter(myListAdapter);
		listView.setDivider(null);
		listView.setOnItemClickListener(new MyItemClickListener());
	}

	private final class MyItemClickListener implements OnItemClickListener {
		
		private String urlPath;
		private String saveDir;

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			
			String filename = (String) myListAdapter.getItem(position); // 该文件在网络中的地址 // 数据库中只存放文件名
			//类似于此样式  /meet_files/201106202318
			urlPath =  "http://"+Constants.SERVER_IP+":"+Constants.SERVER_PORT+"/NetMeetServer/meet_files/"+meet.getMeetid()+"/"+filename;	
			saveDir = Environment.getExternalStorageDirectory().getAbsolutePath()+"/meet_files/"+meet.getMeetid();		
			
			File dir = new File(saveDir);
			if(!dir.exists()){
				dir.mkdirs();
			}
			final File file = new File(saveDir,filename); 
			
			System.out.println("saveDir: "+saveDir+"   "+"filename : "+filename);
			System.out.println("urlPath"+urlPath);
			
			if (file.exists() && file.canRead()) {
				showToast("打开文件");
				openFile(file);
			}else{
				OnClickListener listener1 = new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// 打开下载页面
						new DownloadHelper(MeetDetail.this,file.getAbsolutePath()).startDownload(urlPath);
						dialog.cancel();
					}
				};
				alert("下载文件","文件不存在，是否立刻下载！",null,"下载",listener1,null,null,true);
			}
		}
		private void openFile(File file) {
			Intent intent = new Intent();
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setAction(android.content.Intent.ACTION_VIEW);
			// 根据文件取得类型
			String type = FileType.getMIMEType(file);
			intent.setDataAndType(Uri.fromFile(file), type);
			
			startActivity(intent);
		}
	}
	
	public final class MyItem {
		public ImageView imageView;
		public TextView textView;
	}
	
	private final class MyListAdapter extends BaseAdapter {
		private LayoutInflater inflater;
		// private Context context;
		private List<String> fileList;

		public MyListAdapter(Context context) {
			// this.context = context;
			fileList = meet.getFilepath(); 
			inflater = LayoutInflater.from(MeetDetail.this.getBaseContext());
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View itemView = null;
			MyItem item = null;

			if (convertView == null) {
				item = new MyItem();
				itemView = inflater.inflate(R.layout.meet_detail_list_item, null);
				item.imageView = (ImageView) itemView.findViewById(R.id.mc_file_icon);

				item.textView = (TextView) itemView
						.findViewById(R.id.mc_filelist_filename);
				itemView.setTag(item);
			} else {
				itemView = convertView;
				item = (MyItem) itemView.getTag();
			}

			// 图标先不设置
			item.imageView.setImageResource(R.drawable.icon);
			item.textView.setText(fileList.get(position));
			return itemView;
		}

		@Override
		public int getCount() {
			return fileList.size();
		}

		@Override
		public Object getItem(int position) {
			return fileList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}
	}

}
