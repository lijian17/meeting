package net.dxs.meeting.download;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.text.DecimalFormat;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import net.dxs.meeting.util.Constants;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;


/**
 * ����������
 * @author leopold
 *
 */
public class DownloadTask {
	private String url = "";
	private int threadCount = 5;
	private String localPath = "";
	
	private boolean acceptRanges = false;
//	private String ranges = "";
	private long contentLength = 0;
	long receivedCount = 0;
	private Vector<DownloadThread> threads = new Vector<DownloadThread>();
	
	private long lastCount = 0;
	private long beginTime = 0;
	private long endTime = 0;
	private Object object = new Object();
	private long autoCallbackSleep = 1000;
	
	private Vector<DownloadTaskListener> listeners = new Vector<DownloadTaskListener>();
	
	private static boolean DEBUG = false;
	
	/**
	 * ���������������
	 * @param url Ŀ���ַ
	 */
	@Deprecated
	public DownloadTask(String url) {
		this(url,5);
	}
	/**
	 * ���������������
	 * @param url Ŀ���ַ
	 * @param threadCount �߳�����
	 */
	@Deprecated
	public DownloadTask(String url, int threadCount) {
		this(url,"",threadCount);
	}
	/**
	 * ���������������
	 * @param url Ŀ���ַ
	 * @param localPath ���ر���·��
	 */
	public DownloadTask(String url, String localPath) {
		this(url,localPath,5);
	}
	/**
	 * ���������������
	 * @param url Ŀ���ַ
	 * @param localPath ���ر���·��
	 * @param threadCount �߳�����
	 */
	public DownloadTask(String url, String localPath, int threadCount) {
		this.url = url;
		this.threadCount = threadCount;
		this.localPath = localPath;
	}
	
	public void setAutoCallbackSleep(long autoCallbackSleep) {
		this.autoCallbackSleep = autoCallbackSleep;
	}
	public long getAutoCallbackSleep() {
		return this.autoCallbackSleep;
	}
	
	public static void setDebug(boolean debug){
		DEBUG = debug;
	}
	public static boolean getDebug(){
		return DEBUG;
	}
	
	public void setLocalPath(String localPath){
		this.localPath = localPath;
	}
	
	/**
	 * ��ʼ����
	 * @throws Exception
	 */
	public void startDown() throws Exception{
		HttpClient httpClient = getNewHttpClient();
		try {
			//��ȡ�����ļ���Ϣ
			getDownloadFileInfo(httpClient);
			//������������߳�
			startDownloadThread();
			//��ʼ������������
			monitor();
			
			
		} catch (Exception e) {
			throw e;
		} finally {
			httpClient.getConnectionManager().shutdown();
		}
	}

	/**
	 * ��ȡ�����ļ���Ϣ
	 */
	private void getDownloadFileInfo(HttpClient httpClient) throws IOException,
			ClientProtocolException, Exception {
		Constants.Loglj("url:"+url);
		HttpHead httpHead = new HttpHead(url);    //3gwap  3gnet 
		
		HttpResponse response = httpClient.execute(httpHead);
		//��ȡHTTP״̬��
		int statusCode = response.getStatusLine().getStatusCode();

		if(statusCode != 200) throw new Exception("��Դ������!");
		if(getDebug()){
			for(Header header : response.getAllHeaders()){
				Constants.Loglj(header.getName()+":"+header.getValue());
			}
		}
		Header[] headers = response.getHeaders("Content-Length");
		if (headers.length > 0)
			contentLength = Long.valueOf(headers[0].getValue());
		httpHead.abort();
		
		httpHead = new HttpHead(url);
		httpHead.addHeader("Range", "bytes=0-" + contentLength);
		response = httpClient.execute(httpHead);
		if (response.getStatusLine().getStatusCode() == 206) {
			acceptRanges = true;
		}
		httpHead.abort();
	}

	/**
	 * ������������߳�
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	private void startDownloadThread() throws IOException,
			FileNotFoundException {
		//���������ļ�
		File file = new File(localPath);
		if(file.exists()){
			file.delete();
		}
		file.createNewFile();
		
		RandomAccessFile raf = new RandomAccessFile(file, "rw");
		raf.setLength(contentLength);
		raf.close();
		
		//���������߳��¼�ʵ����
		DownloadThreadListener listener = new DownloadThreadListener() {
			public void afterPerDown(DownloadThreadEvent event) {
				//������һ��Ƭ�κ�׷���������ֽ���
				synchronized (object) {
					DownloadTask.this.receivedCount += event.getCount();
				}
			}

			public void downCompleted(DownloadThreadEvent event) {
				//�����߳�ִ����Ϻ�����������Ƴ�
				try{
					stopThread(event.getTarget());
					//threads.remove(event.getTarget());
				}catch(Exception e){
					//Constants.LogLeo("remove==e"+e.getMessage());
				}
				if(getDebug()){
					//Constants.LogLeo("ʣ���߳�����"+threads.size());
				}
			}

			
			public void getException() {
				stopAllThread();
				if(getDebug()){
					//Constants.LogLeo("�쳣-ʣ���߳�����"+threads.size());
				}
				fireAutoCallback(new DownloadTaskEvent(true));
				stopAllThread();
			}
		};
		
		//if(threads!=null){
			stopAllThread();
		//}
		//��֧�ֶ��߳�����ʱ
		if (!acceptRanges) {
			if(getDebug()){
				//Constants.LogLeo("�õ�ַ��֧�ֶ��߳�����");
			}
			//������ͨ����
			DownloadThread thread = new DownloadThread(url, 0, contentLength, file, false);
			thread.addDownloadListener(listener);
			thread.start();
			threads.add(thread);
			return;
		}
		
		//ÿ������Ĵ�С
		long perThreadLength = contentLength / threadCount + 1;
		long startPosition = 0;
		long endPosition = perThreadLength;
		//ѭ��������������߳�
		do{
			if(endPosition > contentLength)
				endPosition = contentLength;

			DownloadThread thread = new DownloadThread(url, startPosition, endPosition, file);
			thread.addDownloadListener(listener);
			thread.start();
			threads.add(thread);

			startPosition = endPosition + 1;//�˴��� 1,�ӽ���λ�õ���һ���ط���ʼ����
			endPosition += perThreadLength;
		} while (startPosition < contentLength);
	}
	
	/**
	 * �����������ع���
	 */
	private void monitor() {
		new Thread() {
			public void run() {
				beginTime = System.currentTimeMillis();
				//���������ֽ���>=�����ֽ��� ���� �����̶߳��ѹرյ�ʱ�����ѭ��
				while(receivedCount < contentLength && !threads.isEmpty()) {
					showInfo(false);
					
					try {
						Thread.sleep(autoCallbackSleep);
					} catch (InterruptedException e) { }
				}
				if(receivedCount >= contentLength)
				{
					showInfo(true);
					return;
				}
			}
		}.start();
	}

	/**
	 * ���������Ϣ�������¼�
	 */
	private void showInfo(boolean complete) {
		long currentTime = System.currentTimeMillis();
		double realTimeSpeed = (receivedCount - lastCount) * 1.0 / ((currentTime - endTime) / 1000.0);
		double globalSpeed = receivedCount * 1.0 / ((currentTime - beginTime) / 1000.0);
		lastCount = receivedCount;
		endTime = currentTime;
		//�������ؽ��Ȼص��¼�
		//if(complete)
			//Constants.LogLeo("showInfo:true XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXx");
		fireAutoCallback(new DownloadTaskEvent(receivedCount, contentLength, formatSpeed(realTimeSpeed), formatSpeed(globalSpeed),complete));
	};
	
	private void fireAutoCallback(DownloadTaskEvent event){
//		Constants.LogLeo(">>>>>>>>>>>>>>>>>>>");
		if(listeners.isEmpty()) return;
		
		for(DownloadTaskListener listener:listeners){
			listener.autoCallback(event);
		}
	}
	public void addTaskListener(DownloadTaskListener listener) {
		listeners.add(listener);
	}
	
//	public static void killall(){
//		int size=DownloadTask.listeners.size();
//	    for(int i=0;i<size;i++){
//	    	if(DownloadTask.listeners.get(i)!=null){
//	    		DownloadTask.listeners.remove(i);
//	    	}
//	    }
//	    
//	}
	/**
	 * ���Ա������ļ�������
	 */
	public String guessFileName() throws Exception{
		HttpClient httpClient = new DefaultHttpClient();
		try {
			HttpHead httpHead = new HttpHead(url);
			HttpResponse response = httpClient.execute(httpHead);
			String contentDisposition = null;
			if(response.getStatusLine().getStatusCode() == 200){
				//Content-Disposition
				Header[] headers = response.getHeaders("Content-Disposition");
				if(headers.length > 0)
					contentDisposition = headers[0].getValue();
			}
			httpHead.abort();
			
			if (contentDisposition!=null && contentDisposition.startsWith("attachment")) {
				return contentDisposition.substring(contentDisposition.indexOf("=")+1);
			} else if (Pattern.compile("(/|=)([^/&?]+\\.[a-zA-Z]+)").matcher(url).find()) {
				Matcher matcher = Pattern.compile("(/|=)([^/&?]+\\.[a-zA-Z]+)").matcher(url);
				String s = "";
				while(matcher.find())
					//�����һ��URL�ϵĿ����ļ�����Ϊ���β²�Ľ��
					s = matcher.group(2);
				return s;
			}
		} catch (Exception e) {
			throw e;
		}finally{
			httpClient.getConnectionManager().shutdown();
		}
		return "UnknowName.temp";
	}
	
	/**
	 * ��ʽ�����ؽ��� Ϊ B/s,K/s,M/s,G/s,T/s
	 */
	private String formatSpeed(double speed){
		DecimalFormat format = new DecimalFormat("#,##0.##");
		if(speed<1024){
			return format.format(speed)+" B/s";
		}
		
		speed /= 1024;
		if(speed<1024){
			return format.format(speed)+" K/s";
		}
		
		speed /= 1024;
		if(speed<1024){
			return format.format(speed)+" M/s";
		}
		
		speed /= 1024;
		if(speed<1024){
			return format.format(speed)+" G/s";
		}
		
		speed /= 1024;
		if(speed<1024){
			return format.format(speed)+" T/s";
		}
		
		return format.format(speed) + "B/s";
	}
	public static  HttpClient getNewHttpClient() {
	    try {
	        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
	        trustStore.load(null, null);

	        SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
	        sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

	        HttpParams params = new BasicHttpParams();
	        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
	       
	        HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

	        SchemeRegistry registry = new SchemeRegistry();
	        registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
	        registry.register(new Scheme("https", sf, 443));

	        ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

	        return new DefaultHttpClient(ccm, params);
	    } catch (Exception e) {
	        return new DefaultHttpClient();
	    }
	}
	
	
	public static class MySSLSocketFactory extends SSLSocketFactory {
	    SSLContext sslContext = SSLContext.getInstance("TLS");

	    public MySSLSocketFactory(KeyStore truststore) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
	        super(truststore);

	        TrustManager tm = new X509TrustManager() {

	        	@Override
	            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
	                return null;
	            }

				@Override
				public void checkClientTrusted(
						java.security.cert.X509Certificate[] chain,
						String authType) throws CertificateException {
					
				}

				@Override
				public void checkServerTrusted(
						java.security.cert.X509Certificate[] chain,
						String authType) throws CertificateException {
					
				}
	        };

	        sslContext.init(null, new TrustManager[] { tm }, null);
	    }

	    @Override
	    public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException {
	        return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
	    }

	    @Override
	    public Socket createSocket() throws IOException {
	        return sslContext.getSocketFactory().createSocket();
	    }
	}
	
	public void stopThread(DownloadThread thread){
		synchronized(threads){
			thread.cancel();
			threads.remove(thread);
		}
	}
	public void stopAllThread(){
		synchronized(threads){
			for(DownloadThread thread :threads){
				thread.cancel();
			}
			threads.removeAllElements();
		}
	}
	public long getContentLength() {
		return contentLength;
	}
	
	
}
