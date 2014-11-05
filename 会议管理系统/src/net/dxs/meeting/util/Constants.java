package net.dxs.meeting.util;

import java.io.IOException;
import java.text.SimpleDateFormat;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;

/**
 * 常量类
 * @author leopold
 *
 */
public class Constants {


//	public static String SERVER_IP = "192.168.1.248";
	public static String SERVER_IP = "10.0.2.2";
	public static String SERVER_PORT = "8080";
	
	public static String getUrl(){
		return "http://"+SERVER_IP+":"+SERVER_PORT+"/NetMeetServer/server";
	}
	
	/**
	 * 联网超时的错误码
	 */
	public static final int TIME_OUT = 101;
	public static final int ConnectException = 102;
	public static final int NOT_KNOW_ERROR = 106;
	/**
	 * 请求类型错误
	 */
	public static final int NO_TYPE_ERROR = 103;
	/**
	 * 服务器无返回
	 */
	public static final int NO_ENTRY_ERROR = 104;
	/**
	 * XML文件解析错误
	 */
	public static final int PARSER_ERROR = 105;
	
	/**
	 * 网络错误
	 */
	public static final int NET_ERROR = 400;
	public static SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日  HH：mm");
	
	public static boolean isShow = true;
	public static void Logleo(String str){
		if(isShow){
			Log.i("leo", "-->"+str);
		}
	}
	
	public static void Loglili(String str){
		if(isShow){
			Log.i("lili", "-->"+str);
		}
	}
	
	public static String getNextText(XmlPullParser parser){
		String text = null;
		try {
			text = parser.nextText();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return text;
	}
}
