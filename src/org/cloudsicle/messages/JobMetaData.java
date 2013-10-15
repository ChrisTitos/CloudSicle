package org.cloudsicle.messages;

import java.net.InetAddress;

/**
 * The JobMetaData is presented to the master by the client so that the master can allocate
 * VM(s) for the actual job
 */
public class JobMetaData implements IMessage {

	private static final long serialVersionUID = 4966126383441403840L;
	
	private InetAddress client;
	private int totalFileSize;
	private int numFiles;
	
	/**
	 * 
	 * @param c The InitAddress of the client
	 */
	public JobMetaData(InetAddress c){
		
	}
	
	public InetAddress getClient() {
		return client;
	}

	public void setClient(InetAddress client) {
		this.client = client;
	}

	public int getTotalFileSize() {
		return totalFileSize;
	}

	public void setTotalFileSize(int totalFileSize) {
		this.totalFileSize = totalFileSize;
	}

	public int getNumFiles() {
		return numFiles;
	}

	public void setNumFiles(int numFiles) {
		this.numFiles = numFiles;
	}


	

}
