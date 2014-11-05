package net.dxs.meeting.net.meetidlist;

import java.util.List;

import net.dxs.meeting.net.netmodule.NetBean;


/**
 * 新发布的会议ID列表bean
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
