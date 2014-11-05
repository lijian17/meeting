package net.dxs.meeting.net.meetidlist;

import java.util.ArrayList;
import java.util.List;

import net.dxs.meeting.net.netmodule.NetBean;
import net.dxs.meeting.net.netmodule.XmlParserListener;
import net.dxs.meeting.util.Constants;

import org.xmlpull.v1.XmlPullParser;

public class MeetIdListXmlParser implements XmlParserListener{

	MeetIdListBean bean;
	private List<String> meetids;
	@Override
	public void startTag(XmlPullParser parser) {
		String name = parser.getName();
		int code = 0;
		if (name.equalsIgnoreCase("checkMeetIds")) {
			bean = new MeetIdListBean();
			code = Integer.parseInt(parser.getAttributeValue(null, "code"));
			meetids = new ArrayList<String>();
			
			bean.setReturnCode("" + code);
			bean.setReturnDes(parser.getAttributeValue(null, "desc"));
		}
		if (name.equalsIgnoreCase("meetingid")) {
			String nextText = Constants.getNextText(parser);
			Constants.Logleo("nextText: "+nextText);
			meetids.add(nextText);
		}
	}

	@Override
	public void endTag(XmlPullParser parser) {
		String name = parser.getName();
		if (name.equalsIgnoreCase("checkMeetIds")) {
			bean.setMeetIdList(meetids);
		}
	}

	@Override
	public NetBean getBean() {
		return bean;
	}
}
