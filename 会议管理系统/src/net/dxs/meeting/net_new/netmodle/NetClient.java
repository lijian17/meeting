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
 * 具体的联网动作
 * 
 * @author lijian
 * 
 */
public class NetClient {

	/**
	 * 异步请求
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
	 * 发送同步请求
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
				helper.dealNetErr(statusCode, "网络错误：" + statusCode);
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
			if (e instanceof SocketTimeoutException || e instanceof ConnectTimeoutException) {// 联网超时（网络层错误）
				helper.dealNetErr(Constants.TIME_OUT, "联网超时,请检查网络！");
			} else if (e instanceof HttpHostConnectException) {// 网络连接失败，请检查网络
				helper.dealNetErr(Constants.TIME_OUT, "网络连接失败，请检查网络！");
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
