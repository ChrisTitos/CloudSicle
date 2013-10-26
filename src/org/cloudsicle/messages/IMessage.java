package org.cloudsicle.messages;

import java.io.Serializable;
import java.net.InetAddress;

public interface IMessage extends Serializable {

	public void setSender();
	
	public InetAddress getSender();

}
