package net.dxs.meeting.download;

/**
 * �����̵߳ļ���
 * @author leopold
 *
 */
public interface DownloadThreadListener {

	/**
	 * ÿ��������һ���ֽ�����󴥷�
	 * @param event
	 */
	public void afterPerDown(DownloadThreadEvent event);
	
	/**
	 * �������ʱ����
	 * @param event
	 */
	public void downCompleted(DownloadThreadEvent event);
	
	/**
	 * �����쳣
	 */
	public void getException();
}

