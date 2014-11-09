package net.dxs.meeting.net_new.login;

import net.dxs.meeting.net_new.netmodle.NetHelper;
import net.dxs.meeting.net_new.netmodle.XmlParserListener;

/**
 * µÇÂ½ÖúÊÖ
 * 
 * @author lijian
 */
public class LoginHelper extends NetHelper {

	public LoginHelper(String name, String password) {
		this.addParameter("type", "login");
		this.addParameter("name", name);
		this.addParameter("password", password);
	}

	@Override
	public XmlParserListener setXmlListener() {
		return new LoginXmlParser();
	}

}
