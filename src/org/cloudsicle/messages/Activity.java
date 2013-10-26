package org.cloudsicle.messages;

import java.net.InetAddress;
import java.util.ArrayList;

import org.cloudsicle.communication.INeedOwnIP;
import org.cloudsicle.main.jobs.IJob;

/**
 * An Activiy Message contains an array of Jobs to be executed by the receiver.
 */
public class Activity extends AbstractMessage implements INeedOwnIP {
	
	private static final long serialVersionUID = 5915331727737049076L;
	private ArrayList<IJob> jobs;
	private InetAddress senderIp;
	
	public Activity(ArrayList<IJob> j){
		jobs = j;
	}
	
	public ArrayList<IJob> getJobs(){
		return jobs;
	}
	
	@Override
	public void setIP(InetAddress ip) {
		this.senderIp = ip;
		
	}

	@Override
	public InetAddress getIP() {
		return this.senderIp;
	}
	
}
