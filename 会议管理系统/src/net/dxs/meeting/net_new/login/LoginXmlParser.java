package net.dxs.meeting.net_new.login;

import net.dxs.meeting.net_new.netmodle.BaseBean;
import net.dxs.meeting.net_new.netmodle.XmlParserListener;

import org.xmlpull.v1.XmlPullParser;

public class LoginXmlParser implements XmlParserListener{

	LoginBean bean;

	@Override
	public BaseBean getBean() {
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
