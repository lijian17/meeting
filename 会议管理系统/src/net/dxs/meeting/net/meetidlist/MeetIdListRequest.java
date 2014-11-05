package net.dxs.meeting.net.meetidlist;

import net.dxs.meeting.net.netmodule.BaseRequest;
import net.dxs.meeting.net.netmodule.BaseResponse;

/**
 * ��ѯ�Ƿ����»��鷢��������
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
