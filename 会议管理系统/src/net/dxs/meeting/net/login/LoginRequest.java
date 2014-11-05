package net.dxs.meeting.net.login;

import net.dxs.meeting.net.netmodule.BaseRequest;
import net.dxs.meeting.net.netmodule.BaseResponse;

public class LoginRequest extends BaseRequest {

	public LoginRequest(String name, String password){
		this.addParameter("type", "login");
		this.addParameter("name", name);
		this.addParameter("password", password);
		
//		String requestStr = "";
	}
	
	@Override
	public BaseResponse setResponse() {
		return new LoginResponse();
	}
}
