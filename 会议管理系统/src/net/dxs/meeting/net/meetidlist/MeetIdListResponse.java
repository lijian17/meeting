package net.dxs.meeting.net.meetidlist;

import net.dxs.meeting.net.netmodule.BaseResponse;
import net.dxs.meeting.net.netmodule.XmlParserListener;

public class MeetIdListResponse extends BaseResponse {

	@Override
	public XmlParserListener setXmlParser() {
		return new MeetIdListXmlParser();
	}

}
