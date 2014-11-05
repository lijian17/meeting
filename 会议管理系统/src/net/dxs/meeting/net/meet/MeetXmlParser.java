package net.dxs.meeting.net.meet;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.dxs.meeting.net.netmodule.NetBean;
import net.dxs.meeting.net.netmodule.XmlParserListener;
import net.dxs.meeting.util.Constants;

import org.xmlpull.v1.XmlPullParser;

/**
 * 根据meetid获得meet的xml解析
 * @author leopold
 *
 */
public class MeetXmlParser implements XmlParserListener{

	private MeetBean bean;
	private List<String> filepaths;

	@Override
	public NetBean getBean() {
		return bean;
	}

	@Override
	public void startTag(XmlPullParser parser) {
		String name = parser.getName();
		if(name.equalsIgnoreCase("meeting")){
			bean = new MeetBean();
			filepaths = new ArrayList<String>();
			bean.setFilepath(filepaths);
			
			bean.setReturnCode(parser.getAttributeValue(null,"code"));
			bean.setReturnDes(parser.getAttributeValue(null,"desc"));
		}
		
		if (name.equalsIgnoreCase("mtID")) {
			bean.setMeetid(Constants.getNextText(parser));
		} 
		if (name.equalsIgnoreCase("mtName")) {
			bean.setMeetname(Constants.getNextText(parser));
		} 
		if (name.equalsIgnoreCase("mtDate")) {
			String dateString = Constants.getNextText(parser);
			bean.setMeetdate(new Date(Long.parseLong(dateString)));
		} 
		if (name.equalsIgnoreCase("mtPublishDate")) {
			String dateString = Constants.getNextText(parser);
			bean.setPublishDate(new Date(Long.parseLong(dateString)));
		} 
		if (name.equalsIgnoreCase("mtManger")) {
			bean.setMeetmanager(Constants.getNextText(parser));
		} 
		if (name.equalsIgnoreCase("mtPlace")) {
			bean.setMeetplace(Constants.getNextText(parser));
		} 
		if (name.equalsIgnoreCase("mtPeoples")) {
			bean.setMeetpeoples(Constants.getNextText(parser));
		} 
		if (name.equalsIgnoreCase("file")) {
			filepaths.add(Constants.getNextText(parser));
		}
		
	}

	@Override
	public void endTag(XmlPullParser parser) {}

}
