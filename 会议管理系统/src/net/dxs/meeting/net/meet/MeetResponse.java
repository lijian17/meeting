package net.dxs.meeting.net.meet;

import net.dxs.meeting.net.netmodule.BaseResponse;
import net.dxs.meeting.net.netmodule.XmlParserListener;

/**
 * 根据meetid请求meet的回复
 * @author leopold
 *
 */
public class MeetResponse extends BaseResponse {

	@Override
	public XmlParserListener setXmlParser() {
		return new MeetXmlParser();
	}
	
}
