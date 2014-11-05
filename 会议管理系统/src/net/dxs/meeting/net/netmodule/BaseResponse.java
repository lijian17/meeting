package net.dxs.meeting.net.netmodule;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import net.dxs.meeting.BaseActivity;
import net.dxs.meeting.util.Constants;

import org.apache.http.HttpResponse;
import org.xmlpull.v1.XmlPullParser;

import android.os.Bundle;
import android.os.Message;
import android.util.Xml;

/**
 * 
 * @author leopold
 *
 */
public abstract class BaseResponse {

	private BaseActivity activity;
	
	public BaseResponse(){
		setListener(setXmlParser());
		activity = BaseActivity.activity;
	}
	
	private NetBean bean;
	public NetBean getBean() {
		return bean;
	}
	public void setBean(NetBean bean) {
		this.bean = bean;
	}
	
	
	private HttpResponse response;

	public HttpResponse getResponse() {
		return response;
	}

	public void setResponse(HttpResponse response) {
		this.response = response;
	}
	
	public boolean isHaveError = false;
	
	private XmlParserListener listener;
	
	public XmlParserListener getListener() {
		return listener;
	}
	
	public void setListener(XmlParserListener listener) {
		this.listener =listener;
	}
	
	/**
	 * 子类必须为response设置xml解析器
	 * leo 2012-12-5
	 * @return
	 */
	public abstract XmlParserListener setXmlParser();
	
	/**
	 * 统一处理错误
	 */
	public void dealNetErr(int errorCode, String errorDes) {
		isHaveError = true;
		Constants.Logleo("========"+errorDes+"=====");

		Message msg = new Message();
		msg.what = Constants.NET_ERROR;
		Bundle data = new Bundle();
		data.putString("errorDes", errorDes);
		msg.setData(data );
		activity.handler.sendMessage(msg);
		
		switch (errorCode) {
		case Constants.TIME_OUT://联网超时
			break;
		case Constants.NO_TYPE_ERROR://类型错误
			break;
		default:
			break;
		}
	}

	/**
	 * 解析服务器返回的XML文件，并做相应的处理
	 */
	public void parseResponse() {
		try {
			HttpResponse httpRes = this.getResponse();
			InputStream ins = httpRes.getEntity().getContent();
			String source = convertStream(ins);
			if("".equals(source)){
				dealNetErr(Constants.NO_ENTRY_ERROR, "服务器没有返回任何数据，请稍后重试！");
				return;
			}
			
//			JSONObject jobj = new JSONObject(source);
//			setBean(listener.getBean(jobj));
			
			
			InputStream inputStream = new ByteArrayInputStream(source.getBytes());
			
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(inputStream, "UTF-8");
			
			int type = parser.getEventType();
			while(type!= XmlPullParser.END_DOCUMENT){
				if("error".equals(parser.getName())){
					//处理服务器返回的错误信息
					int errorCode = Integer.parseInt(parser.getAttributeValue(null, "code"));
					String errorDes = parser.getAttributeValue(null, "desc");
					dealNetErr(errorCode, errorDes);
					return;
				}
				switch (type) {
				case XmlPullParser.START_TAG:
					listener.startTag(parser);
					break;
				case XmlPullParser.END_TAG:
					listener.endTag(parser);
					break;
				default:
					break;
				}
				type = parser.next();
			}
			//将分析结果得到的bean放入response
			if(listener!=null){
				setBean(listener.getBean());
			}
		} catch (Exception e) {
			e.printStackTrace();
			dealNetErr(Constants.PARSER_ERROR, "json文件解析失败");
		}
	}

	private String convertStream(InputStream input){
		ByteArrayOutputStream bais = new ByteArrayOutputStream();
		try {
			
			byte [] buf = new byte[512];
			int c = input.read(buf);
			while(c!=-1){
				bais.write(buf, 0, c);
				c = input.read(buf);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new String(bais.toByteArray());
	}
}
