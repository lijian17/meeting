package net.dxs.meeting.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * ���ݿ������
 * @author leopold
 *
 */
public class DBAdapter {

	/**
	 * ����Ļ�����Ϣ
	 */
	public static String MEET_INFO = "meetcontent";
	/**
	 * ��������ļ��б�
	 */
	public static String MEET_FILE_LIST = "meetfilelist";	
	/**
	 * ��setting���У���key ,value����ʽ��Ÿ�����Ϣ�磺
	 * mkey
	 * mvalue
	 */
	public static String TABLE_SYS_INFO = "setting";
	public static String MKEY = "mkey";
	public static String MVALUE = "mvalue";
	
	private static DBAdapter instance;

	public static DBAdapter getInstance() {
		if(instance==null){
			instance = new DBAdapter();
		}
		return instance;
	}
	private DBAdapter(){}
	
	private NetMeetDBHelper dbHelper;
	private SQLiteDatabase db;
	public void createDB(Context context, String name,
			CursorFactory factory, int version){
		dbHelper = new NetMeetDBHelper(context, name, factory, version);
	}
	class NetMeetDBHelper extends SQLiteOpenHelper{
		public NetMeetDBHelper(Context context, String name,
				CursorFactory factory, int version) {
			super(context, name, factory, version);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("create table if not exists meetcontent"
					+ "(_id Integer primary key autoincrement," + "meetid integer,"
					+ "meetname varchar(20)," + "meetdate varchar(20),"
					+ "meetmanager varchar(20)," + "meetplace varchar(30),"
					+ "publishdate varchar(20)," + "meetpeoples varchar(30));");

			db.execSQL("create table if not exists meetfilelist"
					+ "(_id Integer primary key autoincrement," + "meetid integer,"
					+ "filepath varchar(20));");

			db.execSQL("create table if not exists setting(_id integer primary key autoincrement," +
					"mkey varchar(20),mvalue varchar(20));");	
			//�����ʼ����
//			db.execSQL("insert into setting(mkey,mvalue) values('isSave','0');");
//			db.execSQL("insert into setting(mkey,mvalue) values('isAutoLogin','0');");
//			db.execSQL("insert into setting(mkey) values('username');");
//			db.execSQL("insert into setting(mkey) values('password');");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			if(oldVersion>=1 && oldVersion<=2){
				//TODO;,,,,
			}else if (oldVersion<=3){
				//,,,
			}
		}
	}
	
	/**���ĳ�ű��ܼ�¼����
     * @param tableName ������
     * @return
     */
    public int getTotalCount(String tableName){
    	db = dbHelper.getReadableDatabase();
		String sql= "SELECT count(*) FROM " + tableName ;
		Cursor cursor=db.rawQuery(sql, null);
		cursor.moveToFirst();
		int count=cursor.getInt(0);
		return count;
    }
	

	public Cursor query(String tableName, String strCols) {
		Cursor cursor = query(tableName,strCols, null);
		return cursor;
	}
	public Cursor query(String tableName, String strCols, String filter) {
		db = dbHelper.getReadableDatabase();
		String sql;
		if (filter == null) {
			sql = "SELECT " + strCols + " FROM " + tableName;
		} else {
			sql = "SELECT " + strCols + " FROM " + tableName + " where " + filter+ " order by _id desc ";
		}
		Cursor cursor = db.rawQuery(sql, null);
		return cursor;
	}
	
	public Cursor query(String tableName, String strCols, String filter,String order) {
		db = dbHelper.getReadableDatabase();
		String sql;
		if (filter == null) {
			sql = "SELECT " + strCols + " FROM " + tableName + " order by "+order;
		} else {
			sql = "SELECT " + strCols + " FROM " + tableName + " where " + filter+ " order by "+order;
		}
		Cursor cursor = db.rawQuery(sql, null);
		return cursor;
	}
	/**
	 * ����һ����¼
	 * @param tableName����
	 * @param args����
	 * @return
	 */
	public long insert(String tableName,ContentValues args) {
		db = dbHelper.getWritableDatabase();
		long n=db.insert(tableName, null, args);
		return n;
	}
	/**
	 * ����һ����¼
	 * @param tableName����
	 * @param args����
	 * @return
	 */
	public long insert(String tableName,String noNullCol,ContentValues args) {
		db = dbHelper.getWritableDatabase();
		long n=db.insert(tableName, noNullCol, args);
		return n;
	}

	
	/**ɾ����¼
	 * @param tableName  ����
	 * @param whereClause  ����
	 * @param whereArgs  ������Ӧ��ֵ
	 * @return
	 */
	public boolean delete(String table, String whereClause, String[] whereArgs)
	{
		db = dbHelper.getWritableDatabase();
		return db.delete(table, whereClause, whereArgs) > 0;
	}

	
	/**�޸ļ�¼ 
	 * @param table ����
	 * @param values  �޸ĵ�ֵ
	 * @param whereClause �޸�����
	 * @param whereArgs �޸�������Ӧ��ֵ
	 * @return
	 */
	public boolean update(String table, ContentValues values, String whereClause, String[] whereArgs){
    	db = dbHelper.getWritableDatabase();
		return db.update(table, values, whereClause, whereArgs) > 0;
    }
	
}
