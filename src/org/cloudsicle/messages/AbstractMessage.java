package org.cloudsicle.messages;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;

abstract class AbstractMessage implements IMessage {

	private static final long serialVersionUID = -4293709280862367915L;
	private InetAddress sender;	
	
	public void setSender(){
		
		URL whatismyip;
		try {
			whatismyip = new URL("http://icanhazip.com");
		
		BufferedReader in = new BufferedReader(new InputStreamReader(
		                whatismyip.openStream()));

		String ip = in.readLine(); //you get the IP as a String
		this.sender = InetAddress.getByName(ip);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public InetAddress getSender(){
		return this.sender;
	}


}
