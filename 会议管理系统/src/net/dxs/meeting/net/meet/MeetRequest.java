package net.dxs.meeting.net.meet;

import net.dxs.meeting.net.netmodule.BaseRequest;
import net.dxs.meeting.net.netmodule.BaseResponse;

/**
 * 根据meetid获得meet的请求
 * @author leopold
 *
 */
public class MeetRequest extends BaseRequest {

	public MeetRequest(String meetid){
		this.addParameter("type", "getMeetById");
		this.addParameter("meetid", meetid);
	}
	
	@Override
	public BaseResponse setResponse() {
		return new MeetResponse();
	}
}
