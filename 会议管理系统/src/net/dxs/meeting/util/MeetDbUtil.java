package net.dxs.meeting.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import net.dxs.meeting.app.NetMeetApp;
import net.dxs.meeting.db.DBAdapter;
import net.dxs.meeting.net.meet.MeetBean;
import android.content.ContentValues;
import android.database.Cursor;

/**
 * 访问数据库工具类
 * @author leopold
 *
 */
public class MeetDbUtil {

	private DBAdapter dBadapter;
	
	private static MeetDbUtil dbUtil;
	public static synchronized MeetDbUtil getDBUtil(){
		if(dbUtil==null){
			dbUtil = new MeetDbUtil();
		}
		return dbUtil;
	}
	private MeetDbUtil(){
		dBadapter = DBAdapter.getInstance();
		dBadapter.createDB(NetMeetApp.app, "net_meet", null, 1);
	}
	
//	public static SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日  HH：mm");
//	
//	public static SQLiteDatabase db = null;
//	
//	public static void openDb(Context context){
//		if(db == null || !MeetDbUtil.db.isOpen())
//		db = new MeetDb(context).getWritableDatabase();
//	}
//	
//	public static void closeDb(){
//		if(db!=null && db.isOpen())
//		db.close();
//	}
//	
//	public static int getCount() {
//		Cursor cursor = db.query(MeetDb.MEET_INFO, new String[] { "count(*)" }, null,
//				null, null, null, null);
//		cursor.moveToFirst();
//		int int1 = cursor.getInt(0);
//		cursor.close();
//		
//		return int1;
//	}
//
	/**
	 *  获得本地的会议的属性
	 * @return
	 */
	public List<MeetBean> getAllMeetBaseInfo() {
		List<MeetBean> meets = new ArrayList<MeetBean>();

		Cursor cursor = dBadapter.query(DBAdapter.MEET_INFO,"*");
		while (cursor.moveToNext()) {
			MeetBean am = new MeetBean();
			String dateString = cursor.getString(cursor.getColumnIndex("meetdate"));
			am.setMeetdate(new Date(Long.parseLong(dateString))); // 开会时间
			am.setMeetname(cursor.getString(cursor.getColumnIndex("meetname"))); // 会议名称

			String meetid = cursor.getString(cursor.getColumnIndex("meetid"));
			am.setMeetid(meetid); // 会议ID
			am.setMeetmanager(cursor.getString(cursor.getColumnIndex("meetmanager"))); // 会议管理者
			am.setMeetplace(cursor.getString(cursor.getColumnIndex("meetplace"))); // 会议地点
			am.setMeetpeoples(cursor.getString(cursor.getColumnIndex("meetpeoples"))); // 与会人员
			am.setFilepath(getFileListByMeetId(meetid));
			meets.add(am);
		}
		cursor.close();
		
		return meets;
	}

	public List<String> getFileListByMeetId(String meetid ) {
		List<String> fileList = null;
		if (meetid != null) {
			Cursor cursor = dBadapter.query(DBAdapter.MEET_FILE_LIST,"filepath","meetid="+meetid);
			while (cursor.moveToNext()) {
				if (fileList == null)
					fileList = new ArrayList<String>();
				fileList.add(cursor.getString(cursor.getColumnIndex("filepath")));
			}
			cursor.close();
		}
		return fileList;
	}

	/**
	 *  得到最近的会议发布时间
	 * @return
	 */
	public String getLastPublishDate() {
		
		String dateString=null;
		// 得到最后加入的会议的发布日期
		Cursor cursor = dBadapter.query(DBAdapter.MEET_INFO, "publishdate" ,null, "publishdate desc limit 1");
		
		if (cursor.moveToNext()) {
			dateString = cursor.getString(0);
//			LPDate = new Date(Long.parseLong(dateString));
		}
		cursor.close();
		
		return dateString;
	}
	
	/**
	 * 将会议信息存入数据库
	 * @param meet
	 * @param context
	 */
	public void addMeet(MeetBean meet ) {
		ContentValues values = new ContentValues();
		values.put("meetid", meet.getMeetid());
		values.put("meetname", meet.getMeetname());
		values.put("meetdate", meet.getMeetdate().getTime() + "");
		values.put("meetmanager", meet.getMeetmanager());
		values.put("meetplace", meet.getMeetplace());
		values.put("meetpeoples", meet.getMeetpeoples());
		values.put("publishdate", meet.getPublishDate().getTime() + "");
		dBadapter.insert(DBAdapter.MEET_INFO, "meetid",values);

		ContentValues valuesFileList = new ContentValues();
		valuesFileList.put("meetid", meet.getMeetid());
		List<String> fileList = meet.getFilepath();
		for (String path : fileList) {
			valuesFileList.put("filepath", path);
			dBadapter.insert(DBAdapter.MEET_FILE_LIST, "meetid", valuesFileList);
		}
		
	}
//
	// /////////////////////////
	/**
	 * 保存设置到数据库，若之前没有，则添加，若有，则更新
	 * @param mkey 		mkey列的值
	 * @param mvalue 	mvalue列的值
	 */
	public void saveSettingToDb( String mkey, String mvalue) {

		ContentValues values = new ContentValues();
		values.put(DBAdapter.MKEY, mkey);
		values.put(DBAdapter.MVALUE, mvalue);
		if (NetMeetApp.app.setInfo.containsKey(mkey)) { // 检查是否已经有此选项，若有，更新，若无，添加
			dBadapter.update(DBAdapter.TABLE_SYS_INFO, values, "mkey = ?", new String[] { mkey });
		} else{
			dBadapter.insert(DBAdapter.TABLE_SYS_INFO, values);
		}
		//更新缓存
		NetMeetApp.app.setInfo.put(mkey, mvalue);
	}
//
//	public static Map<String, String> getSysMap() {
//		Map<String, String> sysMap = new HashMap<String, String>();
//
//		Cursor cursor = db.query(MeetDb.SYS_SETTING, null, null, null, null, null,
//				null);
//		while (cursor.moveToNext()) {
//			sysMap.put(cursor.getString(cursor.getColumnIndex(MeetDb.MKEY)),
//					cursor.getString(cursor.getColumnIndex(MeetDb.MVALUE)));
//		}
//		cursor.close();
//		
//		return sysMap;
//	}
//	
	/**
	 * 读取数据库中的设置信息
	 * @return	HashMap<String, String>
	 */
	public HashMap<String, String> getSetInfo() {
		HashMap<String,String> setInfo  = new HashMap<String,String>();
		Cursor cursor = dBadapter.query(DBAdapter.TABLE_SYS_INFO, "*");
		while(cursor.moveToNext()){
			setInfo.put(cursor.getString(cursor.getColumnIndex("mkey")), 
					cursor.getString(cursor.getColumnIndex("mvalue")));
		}
		cursor.close();
		return setInfo;
	}

}
