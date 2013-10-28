package org.cloudsicle.main.entrypoints;

import java.io.IOException;

import org.cloudsicle.communication.DefaultNetworkVariables;
import org.cloudsicle.communication.FTPService;
import org.cloudsicle.communication.IMessageHandler;
import org.cloudsicle.communication.SocketListener;
import org.cloudsicle.master.Monitor;
import org.cloudsicle.master.Scheduler;
import org.cloudsicle.messages.IMessage;
import org.cloudsicle.messages.JobMetaData;
import org.cloudsicle.messages.StatusUpdate;

public class Master implements IMessageHandler {

	private Scheduler scheduler;
	private SocketListener listener;
	private Monitor monitor;

	public Master() throws Exception {
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
			System.out.println("DEBUG: Received JobMetaData from " + ((JobMetaData) message).getSender());
			this.monitor.addjobWaiting((JobMetaData) message);
			this.scheduler.schedule((JobMetaData) message);
		} else if (message instanceof StatusUpdate) {
			((StatusUpdate)message).getSender();
			System.out.println(((StatusUpdate) message).getMessage());
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
