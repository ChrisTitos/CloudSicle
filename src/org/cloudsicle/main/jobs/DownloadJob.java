package org.cloudsicle.main.jobs;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.ArrayList;

import org.cloudsicle.communication.FTPService;

public class DownloadJob extends AbstractJob implements Serializable {
	
	private static final long serialVersionUID = -8173332718020415882L;
	
	private String uploaderSession;
	private ArrayList<Integer> fileIds;
	private InetAddress uploaderIP;
	
	public DownloadJob(ArrayList<Integer> files, InetAddress uploader){
		this.uploaderSession = FTPService.sessionFromFiles(files);
		this.fileIds = files;
		this.uploaderIP = uploader;
		
	}

	public String getSession() {
		return uploaderSession;
	}

	public void setSession(String uploaderSession) {
		this.uploaderSession = uploaderSession;
	}

	public ArrayList<Integer> getFileIds() {
		return fileIds;
	}

	public void setFileIds(ArrayList<Integer> fileIds) {
		this.fileIds = fileIds;
	}

	public InetAddress getUploaderIP() {
		return uploaderIP;
	}

	public void setUploaderIP(InetAddress uploaderIP) {
		this.uploaderIP = uploaderIP;
	}
}
