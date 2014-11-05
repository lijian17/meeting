package net.dxs.meeting.download;

/**
 * 下载任务的监听
 * @author leopold
 *
 */
public interface DownloadTaskListener {
	
	public void autoCallback(DownloadTaskEvent event);
}
