package net.dxs.meeting.net_new.netmodle;

public class BaseBean {

	private String returnCode;
	private String returnDes;
	
	public String getReturnCode() {
		return returnCode;
	}
	public void setReturnCode(String returnCode) {
		this.returnCode = returnCode;
	}
	public String getReturnDes() {
		return returnDes;
	}
	public void setReturnDes(String returnDes) {
		this.returnDes = returnDes;
	}
	public BaseBean(String returnCode, String returnDes) {
		super();
		this.returnCode = returnCode;
		this.returnDes = returnDes;
	}
	public BaseBean(){}
	
}
