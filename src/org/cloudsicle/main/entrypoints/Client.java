package org.cloudsicle.main.entrypoints;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.cloudsicle.client.Session;
import org.cloudsicle.client.gui.Frontend;
import org.cloudsicle.communication.DefaultNetworkVariables;
import org.cloudsicle.communication.FTPService;
import org.cloudsicle.communication.IMessageHandler;
import org.cloudsicle.communication.SocketListener;
import org.cloudsicle.communication.SocketSender;
import org.cloudsicle.main.jobs.CombineJob;
import org.cloudsicle.main.jobs.CompressJob;
import org.cloudsicle.main.jobs.DownloadJob;
import org.cloudsicle.main.jobs.ForwardJob;
import org.cloudsicle.main.jobs.IJob;
import org.cloudsicle.messages.Activity;
import org.cloudsicle.messages.Allocation;
import org.cloudsicle.messages.IMessage;

import com.jcraft.jsch.JSchException;

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
