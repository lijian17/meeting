package net.dxs.meeting.net.login;

import net.dxs.meeting.net.netmodule.NetBean;
import net.dxs.meeting.net.netmodule.XmlParserListener;

import org.xmlpull.v1.XmlPullParser;


public class LoginXmlParser implements XmlParserListener{

	LoginBean bean;

	@Override
	public NetBean getBean() {
		return bean;
	}

	@Override
	public void startTag(XmlPullParser parser) {
		if(parser.getName().equals("login")){
			bean = new LoginBean();
			bean.setReturnCode(parser.getAttributeValue(null,"code"));
			bean.setReturnDes(parser.getAttributeValue(null,"desc"));
		}
	}

	@Override
	public void endTag(XmlPullParser parser) {}

}
