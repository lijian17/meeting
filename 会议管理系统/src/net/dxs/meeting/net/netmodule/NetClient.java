package net.dxs.meeting.net.netmodule;

import java.io.IOException;
import java.net.SocketTimeoutException;

import net.dxs.meeting.util.Constants;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;

public class NetClient {
	
	/**
	 * �첽����
	 * @param req
	 * @param listener
	 */
	public static void execute(final BaseRequest req,final ResponseListener listener){
		new Thread(){
			@Override
			public void run() {
				BaseResponse response = sendReqSyn(req);
				if(!response.isHaveError){
					listener.dealResponse(response);
				}
			}
		}.start();
	}
	
	/**
	 * ����ͬ������
	 * @param req
	 */
	public static BaseResponse sendReqSyn(BaseRequest req){
		BaseResponse  baseResponse = req.getResponse();
		try {
			HttpClient client = getClient();
			HttpParams params = client.getParams();
			params.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 6000);
			params.setParameter(CoreConnectionPNames.SO_TIMEOUT, 6000);
			
				HttpPost post = new HttpPost(req.getUrl());
				HttpEntity httpEntiry = new UrlEncodedFormEntity(req.getStr_Params(), HTTP.UTF_8);
				post.setEntity(httpEntiry);
				
			Constants.Loglj("url:"+post.getURI());
			HttpResponse response = client.execute(post);
			
			int statusCode = response.getStatusLine().getStatusCode();
//			System.out.println("return code :" + statusCode);
			if (statusCode == 200) {
				baseResponse.setResponse(response);
				baseResponse.parseResponse();
			}else{
				baseResponse.dealNetErr(statusCode, "�������"+statusCode);
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
			if (e instanceof SocketTimeoutException
					|| e instanceof ConnectTimeoutException) {// ������ʱ����������
				baseResponse.dealNetErr(Constants.TIME_OUT, "������ʱ,�������磡");
			} else if (e instanceof HttpHostConnectException) {// ��������ʧ�ܣ���������
				baseResponse.dealNetErr(Constants.TIME_OUT, "��������ʧ�ܣ��������磡");
			} else {
				baseResponse.dealNetErr(Constants.NOT_KNOW_ERROR, e.getClass().getName());
			}
		}
		return baseResponse;
	}

	
	private static HttpClient getClient() {
		return new DefaultHttpClient();
	}
}
