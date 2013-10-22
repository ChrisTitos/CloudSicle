package org.cloudsicle.communication;

import java.net.InetAddress;

public interface INeedOwnIP {

	public void setIP(InetAddress ip);
	public InetAddress getIP();
	
}
