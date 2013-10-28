package org.cloudsicle.main.entrypoints;

import java.io.IOException;

import org.cloudsicle.communication.DefaultNetworkVariables;
import org.cloudsicle.communication.FTPService;
import org.cloudsicle.communication.IMessageHandler;
import org.cloudsicle.communication.SocketListener;
import org.cloudsicle.main.VMState;
import org.cloudsicle.master.Monitor;
import org.cloudsicle.master.Scheduler;
import org.cloudsicle.messages.IMessage;
import org.cloudsicle.messages.JobMetaData;
import org.cloudsicle.messages.StatusUpdate;

public class Master implements IMessageHandler {

	private Scheduler scheduler;
	private SocketListener listener;
	private Monitor monitor;
	private int jobcounter;

	public Master() throws Exception {
		this.jobcounter = 1;
		this.monitor = new Monitor();
		this.scheduler = new Scheduler(this.monitor);
		this.listener = new SocketListener(this);
		listener.start();
		FTPService.start();
	}
	
	@Override
	public void finalize() throws Throwable{
		FTPService.stop();
		super.finalize();
	}

	@Override
	public void process(IMessage message) {
		if (message instanceof JobMetaData) {
			JobMetaData meta = (JobMetaData) message;
			meta.setId(this.jobcounter);
			this.jobcounter++;
			
			System.out.println("DEBUG: Received JobMetaData" + meta.getId() + " from " + meta.getSender());
			this.monitor.addjobWaiting(meta);
			this.scheduler.schedule(meta);
		} else if (message instanceof StatusUpdate) {
			StatusUpdate update = (StatusUpdate) message;
			System.out.println((update).getMessage());
			
			if(update.getState() == VMState.DONE){
				System.out.println(update.getVmId() + " saying he is done");
				this.scheduler.vmIsDone(update.getVmId());
			}
		}
	}

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		DefaultNetworkVariables.loadDAS4InfoFromConfig();
		try {
			Master master = new Master();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
