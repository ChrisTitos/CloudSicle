package org.cloudsicle.client;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.List;

import org.cloudsicle.communication.SocketSender;
import org.cloudsicle.messages.JobMetaData;

import com.jcraft.jsch.JSchException;

public class Session {
	
	private HashMap<Integer, String> files;
	
	public Session(){
		this.files = new HashMap<Integer, String>();
	}
	
	public HashMap<Integer, String> getFileList(){
		return this.files;
	}
	
	/**
	 * Get the total file size for a list of string describing files
	 * 
	 * @param files the files in question
	 * @return the total file size of all the file descriptors
	 */
	private int totalFileSize(List<String> files){
		int size = 0;
		
		for (String s: files){
			File file = new File(s);
			size += file.getTotalSpace();
		}
		
		return size;
	}
	
	/**
	 * Send a JobMetaData request to the server
	 * 
	 * @param files The file descriptors
	 * @param server The server to send to
	 * 
	 * @return 
	 */
	public boolean requestCloudSicle(List<String> files, InetAddress server){
		for(String filename : files){
			this.files.put(filename.hashCode(), filename);
		}
		
		JobMetaData request = new JobMetaData();
		
		request.setFiles(files);
		request.setTotalFileSize(totalFileSize(files));
		
		try {
			SocketSender sender = new SocketSender(true, server);
			sender.send(request);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (JSchException e) {
			e.printStackTrace();
			return false;
		}
	}
	
}
