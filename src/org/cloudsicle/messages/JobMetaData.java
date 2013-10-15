package org.cloudsicle.messages;

import java.net.InetAddress;
import java.util.ArrayList;

/**
 * The JobMetaData is presented to the master by the client so that the master can allocate
 * VM(s) for the actual job
 */
public class JobMetaData implements IMessage {

	private static final long serialVersionUID = 4966126383441403840L;
	
	private InetAddress client;
	private int totalFileSize;
	private int numFiles;
	private ArrayList<String> files;
	
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
		return this.files.size();
	}
	
	public ArrayList<String> getFiles(){
		return this.files;
	}
	
	/**
	 * Pass an ArrayList of <i>filenames</i>, <b>not actual files!</b>
	 * @param files
	 */
	public void setFiles(ArrayList<String> files){
		
	}


	

}
