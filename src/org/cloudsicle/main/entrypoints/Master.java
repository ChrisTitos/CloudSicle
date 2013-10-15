package org.cloudsicle.main.entrypoints;

import org.cloudsicle.communication.IMessageHandler;
import org.cloudsicle.master.Scheduler;
import org.cloudsicle.messages.IMessage;

public class Master implements IMessageHandler {

	private Scheduler scheduler;

	public Master() {
		this.scheduler = new Scheduler();
	}

	@Override
	public void process(IMessage message) {
		// TODO Auto-generated method stub

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}

}
