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
 * �������ݿ⹤����
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
	
//	public static SimpleDateFormat sdf = new SimpleDateFormat("MM��dd��  HH��mm");
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
	 *  ��ñ��صĻ��������
	 * @return
	 */
	public List<MeetBean> getAllMeetBaseInfo() {
		List<MeetBean> meets = new ArrayList<MeetBean>();

		Cursor cursor = dBadapter.query(DBAdapter.MEET_INFO,"*");
		while (cursor.moveToNext()) {
			MeetBean am = new MeetBean();
			String dateString = cursor.getString(cursor.getColumnIndex("meetdate"));
			am.setMeetdate(new Date(Long.parseLong(dateString))); // ����ʱ��
			am.setMeetname(cursor.getString(cursor.getColumnIndex("meetname"))); // ��������

			String meetid = cursor.getString(cursor.getColumnIndex("meetid"));
			am.setMeetid(meetid); // ����ID
			am.setMeetmanager(cursor.getString(cursor.getColumnIndex("meetmanager"))); // ���������
			am.setMeetplace(cursor.getString(cursor.getColumnIndex("meetplace"))); // ����ص�
			am.setMeetpeoples(cursor.getString(cursor.getColumnIndex("meetpeoples"))); // �����Ա
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
	 *  �õ�����Ļ��鷢��ʱ��
	 * @return
	 */
	public String getLastPublishDate() {
		
		String dateString=null;
		// �õ�������Ļ���ķ�������
		Cursor cursor = dBadapter.query(DBAdapter.MEET_INFO, "publishdate" ,null, "publishdate desc limit 1");
		
		if (cursor.moveToNext()) {
			dateString = cursor.getString(0);
//			LPDate = new Date(Long.parseLong(dateString));
		}
		cursor.close();
		
		return dateString;
	}
	
	/**
	 * ��������Ϣ�������ݿ�
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
	 * �������õ����ݿ⣬��֮ǰû�У�����ӣ����У������
	 * @param mkey 		mkey�е�ֵ
	 * @param mvalue 	mvalue�е�ֵ
	 */
	public void saveSettingToDb( String mkey, String mvalue) {

		ContentValues values = new ContentValues();
		values.put(DBAdapter.MKEY, mkey);
		values.put(DBAdapter.MVALUE, mvalue);
		if (NetMeetApp.app.setInfo.containsKey(mkey)) { // ����Ƿ��Ѿ��д�ѡ����У����£����ޣ����
			dBadapter.update(DBAdapter.TABLE_SYS_INFO, values, "mkey = ?", new String[] { mkey });
		} else{
			dBadapter.insert(DBAdapter.TABLE_SYS_INFO, values);
		}
		//���»���
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
	 * ��ȡ���ݿ��е�������Ϣ
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
