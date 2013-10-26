package org.cloudsicle.messages;

import java.net.InetAddress;

import org.cloudsicle.communication.INeedOwnIP;

/**
 * Allow current state chain to finish then shut down.
 */
public class SoftExit extends AbstractMessage implements INeedOwnIP{

	private static final long serialVersionUID = 1L;
	
	private InetAddress ip;
	
	@Override
	public void setIP(InetAddress ip) {
		this.ip = ip;
	}

	@Override
	public InetAddress getIP() {
		return ip;
	}

}
