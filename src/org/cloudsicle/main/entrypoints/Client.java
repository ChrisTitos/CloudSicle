package org.cloudsicle.main.entrypoints;

import java.io.IOException;
import java.net.UnknownHostException;
import org.cloudsicle.client.Session;
import org.cloudsicle.client.gui.Frontend;
import org.cloudsicle.communication.FTPService;
import org.cloudsicle.communication.IMessageHandler;
import org.cloudsicle.communication.SocketListener;
import org.cloudsicle.messages.Allocation;
import org.cloudsicle.messages.IMessage;

public class Client implements IMessageHandler {

	private SocketListener listener;

	public static void main(String[] args) throws UnknownHostException {
		try {
			Client client = new Client();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Session s = new Session();
		Frontend.launch(s); 
	}

	public Client() throws IOException {
		FTPService.start();
	}
	
	@Override
	public void finalize() throws Throwable{
		FTPService.stop();
		super.finalize();
	}

	@Override
	public void process(IMessage message) {
		if (message instanceof Allocation) {
			System.out.println("DEBUG: Received Allocation");			
		}

	}
}
