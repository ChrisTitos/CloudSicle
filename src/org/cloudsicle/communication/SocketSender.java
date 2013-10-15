package org.cloudsicle.communication;

import java.net.InetAddress;

import org.cloudsicle.messages.IMessage;

public class SocketSender {
	
	private InetAddress receiver;
	private int port;
	
	public SocketSender(InetAddress receiver, int port){
		this.receiver = receiver;
		this.port = port;
	}
	
	public void send(IMessage message){
		
	}
	

}
