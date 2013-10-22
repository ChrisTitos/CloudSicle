package org.cloudsicle.main.entrypoints;

import org.cloudsicle.communication.IMessageHandler;
import org.cloudsicle.communication.SocketListener;
import org.cloudsicle.master.Scheduler;
import org.cloudsicle.messages.IMessage;
import org.cloudsicle.messages.JobMetaData;
import org.cloudsicle.messages.StatusUpdate;

public class Master implements IMessageHandler {

	private Scheduler scheduler;
	private SocketListener listener;
	
	public Master() throws Exception {
		this.scheduler = new Scheduler();
		this.listener = new SocketListener(this);
	}
	
	@Override
	public void process(IMessage message) {		
		if(message instanceof JobMetaData){
			System.out.println("DEBUG: Received message!");
			this.scheduler.schedule((JobMetaData) message);
		} else if(message instanceof StatusUpdate){
			System.out.println(((StatusUpdate) message).getMessage());
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
