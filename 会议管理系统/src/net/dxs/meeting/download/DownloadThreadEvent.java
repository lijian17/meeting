package net.dxs.meeting.download;


/**
 * �����̵߳��¼�����
 * @author leopold
 */
public class DownloadThreadEvent {
	private DownloadThread target = null;
	private long count = 0;
	public DownloadThreadEvent(DownloadThread target, long count){
		this.target = target;
		this.count = count;
	}
	
	
	
	/**
	 * ��ȡ�����¼�Դ
	 * @return
	 */
	public DownloadThread getTarget() {
		return target;
	}
	
	/**
	 * ��ȡ�������ص��ֽڴ�С
	 * @return
	 */
	public long getCount() {
		return count;
	}
}
