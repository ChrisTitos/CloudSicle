package org.cloudsicle.main.jobs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;

import org.cloudsicle.communication.INeedOwnIP;

public class DownloadJob implements IJob, INeedOwnIP {
	
	//Transferred by sender
	private final int port;		//The port the client has open for the file request
	private final int fileid;	//The ID of the file to be serviced
	
	//Filled in at receiver
	private InetAddress ip;
	
	public DownloadJob(int port, int fileid){
		this.port = port;
		this.fileid = fileid;
	}
	
	/**
	 * Set the sender IP
	 */
	@Override
	public void setIP(InetAddress ip) {
		this.ip = ip;
	}
	
	/**
	 * Retrieve the sender IP
	 */
	@Override
	public InetAddress getIP() {
		return ip;
	}
	
	/**
	 * A file id by which the file to be transferred is known by a client
	 * 
	 * @return the file id
	 */
	public int getFileID(){
		return fileid;
	}
	
	private String conjureFilePath() throws IOException{
		String out = ip.getHostAddress();
		out = "downloads" + File.separator + out.replaceAll("[.:]", "") + File.separator + fileid + ".gif";
		File f = new File(out);
		f.createNewFile();
		return out;
	}
	
	/**
	 * Download the file we were instructed to download.
	 * 
	 * @return The file identifier on our system
	 * @throws IOException If something went horribly wrong
	 */
	public String download() throws IOException{
		String path = conjureFilePath();
		FileOutputStream fos = new FileOutputStream(path);
		Socket sock = new Socket(ip,port);
		InputStream is = sock.getInputStream();
		int c;
		while ((c = is.read())!=-1)
			fos.write(c);
		fos.close();
		is.close();
		return path;
	}
}
