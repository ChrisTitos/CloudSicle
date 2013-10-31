package org.cloudsicle.main.jobs;

import java.net.InetAddress;

import org.cloudsicle.communication.INeedOwnIP;

public abstract class AbstractJob implements IJob, INeedOwnIP {
	
	private int metaJob;
	protected InetAddress ip;
	
	public int getMetaJob(){
		return this.metaJob;
	}
	
	public void setMetaJob(int metaJobId){
		this.metaJob = metaJobId;
	}

	@Override
	public void setIP(InetAddress ip) {
		this.ip = ip;
	}

	@Override
	public InetAddress getIP() {
		return ip;
	}
	

}
