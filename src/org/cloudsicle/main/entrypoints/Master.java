package org.cloudsicle.main.entrypoints;

import java.io.IOException;

import org.cloudsicle.communication.IMessageHandler;
import org.cloudsicle.communication.SocketListener;
import org.cloudsicle.master.Scheduler;
import org.cloudsicle.messages.IMessage;
import org.cloudsicle.messages.JobMetaData;

public class Master implements IMessageHandler {

	private Scheduler scheduler;
	private SocketListener listener;
	
	private final static int port = 1;

	public Master() throws Exception {
		this.scheduler = new Scheduler();
		this.listener = new SocketListener(this, port);
	}
	
	@Override
	public void process(IMessage message) {		
		if(message instanceof JobMetaData){
			this.scheduler.schedule((JobMetaData) message);
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Master master = new Master();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
