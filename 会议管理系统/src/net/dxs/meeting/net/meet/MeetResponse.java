package net.dxs.meeting.net.meet;

import net.dxs.meeting.net.netmodule.BaseResponse;
import net.dxs.meeting.net.netmodule.XmlParserListener;

/**
 * ����meetid����meet�Ļظ�
 * @author leopold
 *
 */
public class MeetResponse extends BaseResponse {

	@Override
	public XmlParserListener setXmlParser() {
		return new MeetXmlParser();
	}
	
}
