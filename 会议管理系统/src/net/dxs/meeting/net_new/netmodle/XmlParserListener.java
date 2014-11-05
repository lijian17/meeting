package net.dxs.meeting.net_new.netmodle;

import org.xmlpull.v1.XmlPullParser;

public interface XmlParserListener {

	public abstract void startTag(XmlPullParser parser);

	public abstract void endTag(XmlPullParser parser);

	public abstract BaseBean getBean();
}
