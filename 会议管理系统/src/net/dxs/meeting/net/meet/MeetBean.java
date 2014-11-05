package net.dxs.meeting.net.meet;

import java.util.Date;
import java.util.List;

import net.dxs.meeting.net.netmodule.NetBean;


/**
 * 会议bean
 * @author leopold
 *
 */
public class MeetBean extends NetBean{
	
	private String meetid;
	private String meetname;
	/**
	 * 开会的时间
	 */
	private Date meetdate;
	private String meetmanager;
	private String meetplace;
	private String meetpeoples;
	private List<String> filepath;
	
	/**
	 * 发布的时间
	 */
	private Date publishDate;

	
	public MeetBean() {
		super();
	}

	public MeetBean(String meetid, String meetname, Date meetdate,
			String meetmanager, String meetplace, String meetpeoples,
			Date publishDate,List<String> filepath) {
		this.meetid = meetid;
		this.meetname = meetname;
		this.meetdate = meetdate;
		this.meetmanager = meetmanager;
		this.meetplace = meetplace;
		this.meetpeoples = meetpeoples;
		this.filepath = filepath;
		this.publishDate = publishDate;
	}
	
	public String getMeetid() {
		return meetid;
	}

	public void setMeetid(String meetid) {
		this.meetid = meetid;
	}



	public String getMeetpeoples() {
		return meetpeoples;
	}



	public void setMeetpeoples(String meetpeoples) {
		this.meetpeoples = meetpeoples;
	}



	public List<String> getFilepath() {
		return filepath;
	}



	public void setFilepath(List<String> filepath) {
		this.filepath = filepath;
	}

	
	
	
	public String getMeetname() {
		return meetname;
	}
	public void setMeetname(String meetname) {
		this.meetname = meetname;
	}
	public Date getMeetdate() {
		return meetdate;
	}
	public void setMeetdate(Date meetdate) {
		this.meetdate = meetdate;
	}
	public String getMeetmanager() {
		return meetmanager;
	}
	public void setMeetmanager(String meetmanager) {
		this.meetmanager = meetmanager;
	}
	public String getMeetplace() {
		return meetplace;
	}
	public void setMeetplace(String meetplace) {
		this.meetplace = meetplace;
	}
	public void setPublishDate(Date publishDate) {
		this.publishDate = publishDate;
	}
	public Date getPublishDate() {
		return publishDate;
	}
}
