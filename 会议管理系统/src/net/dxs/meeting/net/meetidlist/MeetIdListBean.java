package net.dxs.meeting.net.meetidlist;

import java.util.List;

import net.dxs.meeting.net.netmodule.NetBean;


/**
 * �·����Ļ���ID�б�bean
 * @author leopold
 *
 */
public class MeetIdListBean extends NetBean {

	private List<String> meetIdList;

	public List<String> getMeetIdList() {
		return meetIdList;
	}

	public void setMeetIdList(List<String> meetIdList) {
		this.meetIdList = meetIdList;
	}
}
