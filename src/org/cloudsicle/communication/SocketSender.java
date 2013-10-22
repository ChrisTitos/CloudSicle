package org.cloudsicle.communication;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

import org.cloudsicle.messages.IMessage;

/**
 * Class to facilitate the sending of objects to a specific target
 */
public class SocketSender {
	
	private InetAddress receiver;
	private int port;
	
	/**
	 * Initialize the SocketSender for a specific target, with default port
	 * 
	 * @param receiver The target's InetAddress
	 */
	public SocketSender(InetAddress receiver){
		this.receiver = receiver;
		this.port = 21007;
	}
	
	/**
	 * Initialize the SocketSender for a specific target
	 * 
	 * @param receiver The target's InetAddress
	 * @param port The target's port
	 */
	public SocketSender(InetAddress receiver, int port){
		this.receiver = receiver;
		this.port = port;
	}
	
	/**
	 * Send a message to our target.
	 * 
	 * @param message The message to be sent
	 * @throws IOException If something went wrong sending
	 */
	public void send(IMessage message) throws IOException{
		Socket s = new Socket(receiver, port);
		ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
		oos.writeObject(message);
		oos.close();
		s.close();
	}

}
