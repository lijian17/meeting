package net.dxs.meeting.net.meetidlist;

import net.dxs.meeting.net.netmodule.BaseRequest;
import net.dxs.meeting.net.netmodule.BaseResponse;

/**
 * 查询是否有新会议发布的请求。
 * @author leopold
 *
 */
public class MeetIdListRequest extends BaseRequest {

	public MeetIdListRequest(String publishDate){
		this.addParameter("type", "checkMeetIds");
		this.addParameter("publishDate", publishDate);
	}
	
	@Override
	public BaseResponse setResponse() {
		return new MeetIdListResponse();
	}

}
