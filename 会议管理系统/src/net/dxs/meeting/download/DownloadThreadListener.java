package net.dxs.meeting.download;

/**
 * 单个线程的监听
 * @author leopold
 *
 */
public interface DownloadThreadListener {

	/**
	 * 每次下载完一个字节数组后触发
	 * @param event
	 */
	public void afterPerDown(DownloadThreadEvent event);
	
	/**
	 * 下载完成时触发
	 * @param event
	 */
	public void downCompleted(DownloadThreadEvent event);
	
	/**
	 * 发生异常
	 */
	public void getException();
}

