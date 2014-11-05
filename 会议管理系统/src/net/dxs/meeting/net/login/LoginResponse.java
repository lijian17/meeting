package net.dxs.meeting.net.login;

import net.dxs.meeting.net.netmodule.BaseResponse;
import net.dxs.meeting.net.netmodule.XmlParserListener;

public class LoginResponse extends BaseResponse {

	@Override
	public XmlParserListener setXmlParser() {
		return new LoginXmlParser();
	}
	
}
