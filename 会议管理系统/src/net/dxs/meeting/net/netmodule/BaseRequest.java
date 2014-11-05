package net.dxs.meeting.net.netmodule;

import java.util.ArrayList;
import java.util.List;

import net.dxs.meeting.util.Constants;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

/**
 * 基础的request，提供访问网络所需要的元素。
 * @author Leopold
 *
 */
public abstract class BaseRequest {

	public static String HTTP_GET = "GET";
	public static String HTTP_POST = "POST";
	public static String HTTP_HEAD = "HEAD";

	private String method = "POST";

	private String url ;

	public BaseResponse response;
	
	private List<NameValuePair> nameValuePair;

	public void addParameter(String name, String value) {
		if (nameValuePair == null) {
			nameValuePair = new ArrayList<NameValuePair>();
		}
		nameValuePair.add(new BasicNameValuePair(name, value));
	};

	public BaseRequest() {
		this.url = Constants.getUrl();
		this.response = setResponse();
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public BaseResponse getResponse() {
		return response;
	}

	/**
	 * 子类必须为request设置一个response
	 * leo 2012-12-5
	 * @return
	 */
	public abstract BaseResponse setResponse();

	public List<? extends NameValuePair> getStr_Params() {
		return nameValuePair;
	}

	

}
