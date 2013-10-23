package org.cloudsicle.main.entrypoints;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;

import org.cloudsicle.client.Session;
import org.cloudsicle.client.gui.Frontend;
import org.cloudsicle.communication.IMessageHandler;
import org.cloudsicle.communication.SocketListener;
import org.cloudsicle.communication.SocketSender;
import org.cloudsicle.main.jobs.IJob;
import org.cloudsicle.main.jobs.ProduceJob;
import org.cloudsicle.messages.Activity;
import org.cloudsicle.messages.Allocation;
import org.cloudsicle.messages.IMessage;

import com.jcraft.jsch.JSchException;

public class Client implements IMessageHandler {

	private SocketListener listener;

	public static void main(String[] args) {
		try {
			Client client = new Client();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Session s = new Session();
		Frontend.launch(s);
		
	}

	public Client() throws IOException {
		this.listener = new SocketListener(this);
		this.listener.start();
	}

	@Override
	public void process(IMessage message) {
		if (message instanceof Allocation) {
			System.out.println("DEBUG: Received Allocation");
			for (InetAddress vm : ((Allocation) message).getAllocations()
					.keySet()) {
				ProduceJob pj = new ProduceJob();
				pj.setDelay(10);
				pj.setLoop(true);
				for (String filename : ((Allocation) message).getAllocations()
						.get(vm)) {
					//pj.addFile(new File(filename)); @TODO
				}
				SocketSender sender = new SocketSender(false, vm);
				ArrayList<IJob> list = new ArrayList<IJob>();
				list.add(pj);
				Activity am = new Activity(list);
				try {
					System.out.println("Sending Activity to " + vm.getHostAddress());
					sender.send(am);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JSchException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}
}
