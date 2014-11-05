package net.dxs.meeting.download;

/**
 * ����������¼���
 * @author leopold
 *
 */
public class DownloadTaskEvent {
	private long receivedCount = 0;
	private long totalCount = 0;
	
	private String realTimeSpeed = "";
	private String globalSpeed = "";
	
	private boolean complete = false;
	private boolean hasError;
	public DownloadTaskEvent(boolean hasError){
		this.hasError=true;
	}
	
	public boolean hasError()
	{
		return hasError;
	}
	
	public DownloadTaskEvent(long ReceivedCount, long totalCount,
			String realTimeSpeed, String globalSpeed) {
		this(totalCount, totalCount, globalSpeed, globalSpeed, false);
	}
	
	public DownloadTaskEvent(long ReceivedCount, long totalCount,
			String realTimeSpeed, String globalSpeed, boolean complete) {
		this.receivedCount = ReceivedCount;
		this.totalCount = totalCount;
		this.realTimeSpeed = realTimeSpeed;
		this.globalSpeed = globalSpeed;
		this.complete = complete;
	}

	/**
	 * ��ȡ�ļ��ѽ��մ�С(�ֽ���)
	 * @return
	 */
	public long getReceivedCount() {
		return receivedCount;
	}

	/**
	 * ��ȡ�ļ��ܴ�С(�ֽ���)
	 * @return
	 */
	public long getTotalCount() {
		return totalCount;
	}

	/**
	 * ��ȡʵʱ�ٶ�
	 * @return
	 */
	public String getRealTimeSpeed() {
		return realTimeSpeed;
	}

	/**
	 * ��ȡȫ���ٶ�
	 * @return
	 */
	public String getGlobalSpeed() {
		return globalSpeed;
	}
	
	public boolean isComplete() {
		return complete;
	}
}
