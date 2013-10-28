package org.cloudsicle.communication;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

/**
 * Convienience class for sharing files.
 * 
 * Offers functionality for sending files (offer())
 * and receiving files (download()).
 * 
 * To communicate the resources to be shared a session
 * identifier is needed (String).
 * 
 * Internal communication protocol is as follows:
 * (D being downloader, U being uploader)
 * 
 * D > String   [SerializedObject : Session descriptor]
 * U > byte     [0 : Unable to service, 1 : Proceeding to upload files]
 * [ if unable to service, end of stream
 * [ for each file in session
 *   U > int    [byte[4] : file id]
 *   U > long   [byte[8] : file size]
 *   U > byte[] [byte[file size] : file contents]
 * [ end for
 * [ end of stream
 *  
 */
public class FTPService {
	
	private static final HashMap<String, HashMap<Integer, String>> openDownloadables = new HashMap<String, HashMap<Integer, String>>();
	private static final OfferingThread downloadServer = new OfferingThread();
	
	private static final int THREAD_HEARTBEAT = 5000; //Check if we are still alive every 5 seconds 
	
	public static void start(){
		downloadServer.start();
	}
	
	public static void stop(){
		downloadServer.quit();
	}
	
	/**
	 * Transform a list of fileids to a unique session
	 * identifier for just these files.
	 * 
	 * @param files A list of file ids
	 * @return A unique session id
	 */
	public static String sessionFromFiles(List<Integer> files){
		String out = "";
		for (Integer i : files)
			out += "," + i;
		return out;
	}
	
	/**
	 * Transform a mapping of fileids to file locations to a unique session
	 * identifier for just these files.
	 * 
	 * @param mapping The file identifier to location mapping
	 * @return A unique session id
	 */
	public static String sessionFromFiles(HashMap<Integer,String> mapping){
		String out = "";
		for (Integer i : mapping.keySet())
			out += "," + i;
		return out;
	}
	
	/**
	 * Block until our file is downloaded, utilized the default inferred session.
	 * 
	 * @param filemapping The mapping for fileids to actual files
	 * @param timeout The timeout in milliseconds to wait for the file to start downloading
	 * @return True if the file was transferred, False if a timeout occurred, the server has not started or the server has an error
	 */
	public static boolean offer(HashMap<Integer, String> filemapping, int timeout){
		return offer(sessionFromFiles(filemapping), filemapping, timeout);
	}
	
	/**
	 * Block until our file is downloaded
	 * 
	 * @param descriptor The session descriptor
	 * @param filemapping The mapping for fileids to actual files
	 * @param timeout The timeout in milliseconds to wait for the file to start downloading
	 * @return True if the file was transferred, False if a timeout occurred, the server has not started or the server has an error
	 */
	public static boolean offer(String descriptor, HashMap<Integer, String> filemapping, int timeout){
		if (downloadServer.hasError() || !downloadServer.hasStarted())
			return false;
		//Offer the download
		synchronized (openDownloadables){
			openDownloadables.put(descriptor, filemapping);
		}
		//Wait until our file is done or the timeout has expired
		long startTime = System.currentTimeMillis();
		while (System.currentTimeMillis() < startTime + timeout){
			boolean downloaded = false;
			synchronized (openDownloadables){
				downloaded = !openDownloadables.containsKey(descriptor);
			}
			//Quit if our download is no longer available/has been downloaded
			if (downloaded)
				return true;
			try { Thread.sleep(10); } catch (InterruptedException e) {}
		}
		return false;
	}
	
	/**
	 * Download a file from a certain peer using normal Sockets.
	 * 
	 * @param ip The peer to connect to
	 * @param outputFolder The output folder to write to WITH FINAL PATH SEPARATOR
	 * @return True if the download succeeded, False if it failed
	 */
	public static boolean downloadSock(InetAddress ip, String sessionid, String outputFolder){
		try {
			Socket s = new Socket(ip,DefaultNetworkVariables.DEFAULT_FTP_PORT);
			InputStream is = s.getInputStream();
			OutputStream os = s.getOutputStream();
			boolean success = download(is,os,sessionid,outputFolder);
			s.close();
			return success;
		} catch (IOException e) {
			return false;
		}
	}
	
	/**
	 * Download a file from a certain peer using normal ssh.
	 * 
	 * @param ip The peer to connect to
	 * @param outputFolder The output folder to write to WITH FINAL PATH SEPARATOR
	 * @return True if the download succeeded, False if it failed
	 */
	public static boolean downloadSSH(InetAddress ip, boolean toVM, String sessionid, String outputFolder){
		try {
			JSch jsch = new JSch();
			Session session;
			if (toVM) {
				session = jsch.getSession("root", ip.getHostAddress(), 22);
			} else {
				session = jsch
						.getSession("in439204", ip.getHostAddress(), 22);
				session.setPassword("Pkk6gE5g");
			}

			jsch.addIdentity("~/.ssh/id_dsa");
			session.setConfig("StrictHostKeyChecking", "no");
			session.connect();

			PipedInputStream pis = new PipedInputStream();
			PipedOutputStream fwdOutput = new PipedOutputStream(pis);
			
			PipedOutputStream pos = new PipedOutputStream();
			PipedInputStream fwdInput = new PipedInputStream(pos);

			Channel channel = session.getStreamForwarder(ip.getHostAddress(), DefaultNetworkVariables.DEFAULT_FTP_PORT);
			channel.setInputStream(pis);
			channel.setOutputStream(pos);
			channel.connect(1000);

			boolean success = download(fwdInput,fwdOutput,sessionid,outputFolder);
			
			while (!channel.isClosed())
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
					break;
				}

			channel.disconnect();
			
			return success;
		} catch (IOException e) {
			return false;
		} catch (JSchException e1) {
			return false;
		}
	}
	
	/**
	 * Download a file from a certain peer.
	 * 
	 * @param ip The peer to connect to
	 * @param outputFolder The output folder to write to WITH FINAL PATH SEPARATOR
	 * @return True if the download succeeded, False if it failed
	 */
	private static boolean download(InputStream inStream, OutputStream outStream, String session, String outputFolder){
		try{
			//Write our session identifier
			System.out.println("DEBUG: Attempting to download session \"" + session + "\"");
			ObjectOutputStream oos = new ObjectOutputStream(outStream);
			oos.writeObject(session);
			oos.flush();
			//Read the O.K. signal
			System.out.println("DEBUG: Waiting for uploader session O.K.");
			int ok = inStream.read();
			int timeout = 100;
			while (ok == -1 && timeout > 0){
				ok = inStream.read();
				try { Thread.sleep(50);} catch (InterruptedException e) {}
				timeout--;
			}
			if (timeout == 0){
				System.out.println("DEBUG: Did not receive uploader O.K, exiting");
				inStream.close();
				outStream.close();
				return false;
			}
			if (ok == 0){
				//Uploader signaled our session does not exist
				System.out.println("DEBUG: Uploader could not service download request, exiting");
				inStream.close();
				outStream.close();
				return false;
			}
			System.out.println("DEBUG: Download session O.K., starting download");
			
			BufferedInputStream is = new BufferedInputStream(inStream);
			//We are now going to receive a lot of data
			while (true){
				byte[] b_fileid = new byte[4];
				byte[] b_filesize = new byte[8];
				if (is.read(b_fileid) == -1)
					break; // EOF
				is.read(b_filesize);
				
				int fileid = ByteBuffer.wrap(b_fileid).getInt();
				long filesize = ByteBuffer.wrap(b_filesize).getLong();
				
				System.out.print("DEBUG: Downloading fileid " + fileid + " (" + filesize + " bytes).. ");
				
				//Write the specified amount of bytes to the output file associated with this fileid
				FileOutputStream fos = new FileOutputStream(outputFolder + fileid + ".gif");
				for (long i = 0; i < filesize; i++)
					fos.write(is.read());
				fos.close();
				
				System.out.println("DONE!");
				System.out.println("DEBUG: Stored in " + outputFolder + fileid + ".gif");
			}
			oos.close();
			is.close();
			
			System.out.println("DEBUG: Finished session \"" + session + "\"");
			return true;
		} catch (IOException e){
			e.printStackTrace();
			return false;
		}
	}
	
	private static class OfferingThread implements Runnable{
		
		private boolean alive = true;
		private boolean error = false;
		
		public void start(){
			new Thread(this).start();
		}
		
		public void quit(){
			alive = false;
		}
		
		public boolean hasStarted(){
			return alive;
		}
		
		public boolean hasError(){
			return error;
		}
		
		@Override
		public void run() {
			try{
				ServerSocket serverSocket = new ServerSocket(DefaultNetworkVariables.DEFAULT_FTP_PORT);
				serverSocket.setSoTimeout(THREAD_HEARTBEAT);
				while (alive){
					try {
						Socket s = serverSocket.accept();
						OutputStream os = new BufferedOutputStream(s.getOutputStream());
						
						System.out.println("DEBUG: Accepted new FTP request from " + s.getRemoteSocketAddress());
						
						//Wait for the downloader to put his session description on the stream
						int timeout = 100;				
						while(s.getInputStream().available() < 10 && timeout > 0){
							try { Thread.sleep(50);} catch (InterruptedException e) {}
							timeout--;
						}
						if (timeout == 0){
							System.out.println("DEBUG: Did not receive FTP session identifier, dropping connection");
							continue;
						}
						
						ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
						String session = (String) ois.readObject();
						
						System.out.println("DEBUG: Starting upload session \"" + session + "\"");
						
						//Receive session identifier
						boolean knownsession = false;
						synchronized(openDownloadables){
							knownsession = openDownloadables.containsKey(session);
						}
						if (!knownsession){
							//If an unknown session is being requested send a 0 (false)
							System.out.println("DEBUG: Unknown download session requested, exiting");
							os.write(0);
							os.flush();
							s.close();
							continue;
						}
						//If an known session is being requested send a 1 (true)
						os.write(1);
						os.flush();
						
						HashMap<Integer, String> filemapping = null;
						synchronized(openDownloadables){
							filemapping = openDownloadables.remove(session);
						}
						for (Integer map : filemapping.keySet()){
							String fpath = filemapping.get(map);
							FileInputStream fis = new FileInputStream(fpath);
						
							//Write our file identifier before the stream, so
							//the receiver knows what is coming in.
							byte[] identifier = ByteBuffer.allocate(4).putInt(map.intValue()).array();
							os.write(identifier);
							
							//Put the file size on the stream
							long filesize = new File(fpath).length();
							byte[] filesizeheader = ByteBuffer.allocate(8).putLong(filesize).array();
							os.write(filesizeheader);
							os.flush();
							
							System.out.println("DEBUG: Sending fileid " + map.intValue() + " (" + filesize + " bytes)");
							
							//Write the raw file to stream
							int data = 0;
							while ((data = fis.read()) != -1)
								os.write(data);
							fis.close();
							System.out.println("DEBUG: Done sending file");
							os.flush();
						}
						
						os.close();
						s.close();
						
						System.out.println("DEBUG: Finished session \"" + session + "\"");
	
					} catch (ClassNotFoundException e) {
						System.err.println("Failed to read session descriptor from stream!");
					} catch (SocketTimeoutException e) {
						//Heartbeat, do nothing
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				serverSocket.close();
			} catch (IOException e){
				error = true;
				e.printStackTrace();
			}
		}
	}
	
	/*
	 * Quinten's tests
	 */
	/*
	//Uploader
	public static void main(String[] args){
		FTPService.start();
		HashMap<Integer, String> filemapping = new HashMap<Integer, String>();
		filemapping.put(0, "/home/quinten/Mario.gif");
		filemapping.put(1, "/home/quinten/Mario2.gif");
		FTPService.offer("test", filemapping, 30000);
		FTPService.stop();
	}
	//
	//
	//Downloader
	public static void main(String[] args) throws UnknownHostException{
		InetAddress localhost = InetAddress.getByName("192.168.178.21");
		FTPService.downloadSock(localhost, "test", "in4392test" + File.separator);
	}
	*/
}
