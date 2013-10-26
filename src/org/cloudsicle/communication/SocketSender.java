package org.cloudsicle.communication;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.InetAddress;
import java.net.Socket;

import org.cloudsicle.messages.IMessage;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

/**
 * Class to facilitate the sending of objects to a specific target
 */
public class SocketSender {
	
	private InetAddress receiver;
	private int port;
	private boolean useSSH;
	
	/**
	 * Initialize the SocketSender for a specific target, with default port
	 * 
	 * @param receiver The target's InetAddress
	 */
	public SocketSender(boolean useSSH, InetAddress receiver){
		this(useSSH, receiver, DefaultNetworkVariables.DEFAULT_PORT);
	}
	
	/**
	 * Initialize the SocketSender for a specific target
	 * 
	 * @param receiver The target's InetAddress
	 * @param port The target's port
	 */
	public SocketSender(boolean useSSH, InetAddress receiver, int port){
		this.receiver = receiver;
		this.port = port;
		this.useSSH = useSSH;
	}
	
	/**
	 * Send a message to our target.
	 * 
	 * @param message The message to be sent
	 * @throws IOException If something went wrong sending
	 */
	public void send(IMessage message) throws IOException, JSchException{
		message.setSender();
		if (useSSH)
			sendSSH(message);
		else
			sendSock(message);
	}
	
	/**
	 * Send a message to our target.
	 * 
	 * @param message The message to be sent
	 * @throws IOException If something went wrong sending
	 * @throws JSchException 
	 */
	public void sendSSH(IMessage message) throws IOException, JSchException{
		JSch jsch = new JSch();
		Session session=jsch.getSession("in439204", receiver.getHostAddress(), 22);

		session.setPassword("Pkk6gE5g");
		session.setConfig("StrictHostKeyChecking", "no");
		session.connect();
		
		PipedInputStream pis = new PipedInputStream();
		PipedOutputStream pos = new PipedOutputStream(pis);
		ObjectOutputStream oos = new ObjectOutputStream(pos);
		
		Channel channel = session.getStreamForwarder(receiver.getHostAddress(), port);
		channel.setInputStream(pis);
		channel.connect(1000);
		
		oos.writeObject(message);

		oos.close();

		while (!channel.isClosed())
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
				break;
			}
		
		channel.disconnect();
	}
	
	/**
	 * Send a message to our target.
	 * 
	 * @param message The message to be sent
	 * @throws IOException If something went wrong sending
	 */
	public void sendSock(IMessage message) throws IOException{
		Socket s = new Socket(receiver, port);
		ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
		oos.writeObject(message);
		oos.close();
		s.close();
	}

}
