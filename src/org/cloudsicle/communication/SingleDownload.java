package org.cloudsicle.communication;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Offer a file for download,
 * Exit when we have finished transferring the file
 * or if we timeout.
 */
public class SingleDownload {

	private final File file;
	private final String descriptor;
	private final int timeout;
	
	private SingleDownload(String file, String descriptor, int timeout){
		this.file = new File(file);
		this.descriptor = descriptor;
		this.timeout = timeout;
	}
	
	/**
	 * Block until our file is downloaded
	 * 
	 * @return True if the file was transferred, False if a timeout occurred
	 * @throws IOException 
	 */
	public synchronized boolean offer(){
		try{
			long startTime = System.currentTimeMillis();
			while (System.currentTimeMillis() < startTime + timeout){
				ServerSocket serverSocket = new ServerSocket(DefaultNetworkVariables.DEFAULT_FTP_PORT);
				serverSocket.setSoTimeout(timeout);
				Socket s = serverSocket.accept();
				
				ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
				
				if (!descriptor.equals(ois.readObject())){
					serverSocket.close();
					continue;
				}
				
				OutputStream os = s.getOutputStream();
				FileInputStream fis = new FileInputStream(file);
				
				int data = 0;
				while ((data = fis.read()) != -1)
					os.write(data);
				
				os.close();
				fis.close();
				
				serverSocket.close();
			}
			return true;
		} catch (IOException e){
			return false;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Download a file from a certain peer.
	 * 
	 * @param ip The peer to connect to
	 * @return True if the download succeeded, False if it failed
	 */
	public boolean download(InetAddress ip){
		try{
			Socket s = new Socket(ip,DefaultNetworkVariables.DEFAULT_FTP_PORT);
			InputStream is = s.getInputStream();
			ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
			FileOutputStream fos = new FileOutputStream(file);
			
			oos.writeObject(descriptor);
			oos.close();
			
			int data = 0;
			while ((data = is.read()) != -1)
				fos.write(data); 
			
			is.close();
			fos.close();
			
			s.close();
			return true;
		} catch (IOException e){
			return false;
		}
	}
	
	/**
	 * Try to download a file from a peer several times.
	 * Note that this method is preferred over download(ip)
	 * considering the way offer() is implemented.
	 * 
	 * @param ip The peer to connect to
	 * @param tries The amount of tries to download the file
	 * @return Whether we were successful in downloading the file
	 */
	public boolean download(InetAddress ip, int tries){
		int times = tries;
		while (times > 0){
			if (download(ip))
				break;
			times--;
		}
		return times != 0;
	}
}
