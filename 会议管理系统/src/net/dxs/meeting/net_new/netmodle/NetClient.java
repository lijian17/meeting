package net.dxs.meeting.net_new.netmodle;

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

/**
 * �������������
 * 
 * @author lijian
 * 
 */
public class NetClient {

	/**
	 * �첽����
	 * 
	 * @param helper
	 * @param listener
	 */
	public static void execute(final NetHelper helper, final BaseBeanListener listener) {
		new Thread() {
			@Override
			public void run() {
				BaseBean bean = sendReqSyn(helper);
				if (!helper.isHaveError) {
					listener.dealResult(bean);
				}
			}
		}.start();
	}

	/**
	 * ����ͬ������
	 * 
	 * @param helper
	 */
	public static BaseBean sendReqSyn(NetHelper helper) {
		try {
			HttpClient client = getClient();
			HttpParams params = client.getParams();
			params.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 6000);
			params.setParameter(CoreConnectionPNames.SO_TIMEOUT, 6000);

			HttpPost post = new HttpPost(helper.getUrl());
			HttpEntity httpEntiry = new UrlEncodedFormEntity(helper.getNameValuePairList(), HTTP.UTF_8);
			post.setEntity(httpEntiry);

			Constants.Loglj("url:" + post.getURI());
			HttpResponse response = client.execute(post);

			int statusCode = response.getStatusLine().getStatusCode();
			//			System.out.println("return code :" + statusCode);
			if (statusCode == 200) {
				helper.parseResult(response);
			} else {
				helper.dealNetErr(statusCode, "�������" + statusCode);
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
			if (e instanceof SocketTimeoutException || e instanceof ConnectTimeoutException) {// ������ʱ����������
				helper.dealNetErr(Constants.TIME_OUT, "������ʱ,�������磡");
			} else if (e instanceof HttpHostConnectException) {// ��������ʧ�ܣ���������
				helper.dealNetErr(Constants.TIME_OUT, "��������ʧ�ܣ��������磡");
			} else {
				helper.dealNetErr(Constants.NOT_KNOW_ERROR, e.getClass().getName());
			}
		}
		return helper.getBean();
	}

	private static HttpClient getClient() {
		///
		return new DefaultHttpClient();
	}
}
