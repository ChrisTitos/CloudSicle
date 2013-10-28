package org.cloudsicle.messages;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.List;

import org.cloudsicle.communication.INeedOwnIP;

/**
 * The JobMetaData is presented to the master by the client so that the master can allocate
 * VM(s) for the actual job
 */
public class JobMetaData extends AbstractMessage implements INeedOwnIP {

	private static final long serialVersionUID = 4966126383441403840L;
	
	private InetAddress client;

	private int totalFileSize;
	private HashMap<Integer, String> files;
	
	private long starttime;
	private long endtime;
	

	public int getTotalFileSize() {
		return totalFileSize;
	}

	public void setTotalFileSize(int totalFileSize) {
		this.totalFileSize = totalFileSize;
	}

	public int getNumFiles() {
		return this.files.size();
	}
	
	public HashMap<Integer, String> getFiles(){
		return this.files;
	}
	
	/**
	 * Pass a HashMap of file id's to file names.
	 * @param files
	 */
	public void setFiles(HashMap<Integer, String> files){
		this.files = files;
	}

	@Override
	public void setIP(InetAddress ip) {
		this.client = ip;
	}

	@Override
	public InetAddress getIP() {
		return this.client;
	}

	public long getStarttime() {
		return starttime;
	}

	public void setStarttime(long starttime) {
		this.starttime = starttime;
	}

	public long getEndtime() {
		return endtime;
	}

	public void setEndtime(long endtime) {
		this.endtime = endtime;
	}


}
