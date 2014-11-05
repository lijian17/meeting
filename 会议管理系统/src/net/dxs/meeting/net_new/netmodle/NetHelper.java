package net.dxs.meeting.net_new.netmodle;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import net.dxs.meeting.util.Constants;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.xmlpull.v1.XmlPullParser;

import android.util.Xml;

/**
 * 连网助手
 * leo 2013-1-4
 */
public abstract class NetHelper {

	private String url ;

	public String getUrl() {
		return url;
	}	

	public NetHelper() {
		this.url = Constants.getUrl();
		this.xmlListener = setXmlListener();
	}

	private List<NameValuePair> nameValuePair;
	
	public List<? extends NameValuePair> getNameValuePairList() {
		return nameValuePair;
	}
	
	public void addParameter(String name, String value) {
		if (nameValuePair == null) {
			nameValuePair = new ArrayList<NameValuePair>();
		}
		nameValuePair.add(new BasicNameValuePair(name, value));
	};
	
	private XmlParserListener xmlListener;
	public abstract XmlParserListener setXmlListener();
	
	/**
	 * 对结果进行处理
	 * leo 2013-1-4
	 * @param response2
	 */
	public void parseResult(HttpResponse response) {
		try {
			InputStream ins = response.getEntity().getContent();
			
			String source = convertStream(ins);
			if("".equals(source)){
				dealNetErr(Constants.NO_ENTRY_ERROR, "服务器没有返回任何数据，请稍后重试！");
				return;
			}
			
			InputStream inputStream = new ByteArrayInputStream(source.getBytes());
			
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(inputStream, "UTF-8");
			int type = parser.getEventType();
			while(type!= XmlPullParser.END_DOCUMENT){
				
				switch (type) {
				case XmlPullParser.START_TAG:
					xmlListener.startTag(parser);
					break;
				case XmlPullParser.END_TAG:
					xmlListener.endTag(parser);
					break;
				default:
					break;
				}
				type = parser.next();
			}
			setBean(xmlListener.getBean());
		} catch (Exception e) {
			e.printStackTrace();
			dealNetErr(Constants.PARSER_ERROR, "xml文件解析失败");
		}
	}

	private BaseBean bean;
	
	private void setBean(BaseBean bean) {
		this.bean = bean;
		
	}
	public BaseBean getBean() {
		return bean;
	}
	
	protected boolean isHaveError = false;
	public void dealNetErr(int statusCode, String string) {
		isHaveError = true;
		// TODO Auto-generated method stub
		
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
