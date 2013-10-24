package org.cloudsicle.communication;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Offer a file for download,
 * Exit when we have finished transferring the file
 * or if we timeout.
 */
public class SingleDownload {

	private final File file;
	private int timeout;
	
	private SingleDownload(String file, int timeout){
		this.file = new File(file);
		this.timeout = timeout;
	}
	
	/**
	 * Block until our file is downloaded
	 * 
	 * @return True if the file was transferred, False if a timeout occurred
	 * @throws IOException 
	 */
	public boolean offer(){
		try{
			ServerSocket serverSocket = new ServerSocket(DefaultNetworkVariables.DEFAULT_FTP_PORT);
			serverSocket.setSoTimeout(timeout);
			Socket s = serverSocket.accept();
			OutputStream os = s.getOutputStream();
			FileInputStream fis = new FileInputStream(file);
			
			int data = 0;
			while ((data = fis.read()) != -1)
				os.write(data);
			
			os.close();
			fis.close();
			return true;
		} catch (IOException e){
			return false;
		}
	}
}
