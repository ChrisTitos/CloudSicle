package org.cloudsicle.main.entrypoints;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Random;

import org.cloudsicle.client.Session;
import org.cloudsicle.client.gui.Frontend;
import org.cloudsicle.communication.DefaultNetworkVariables;
import org.cloudsicle.communication.FTPService;
import org.cloudsicle.communication.IMessageHandler;
import org.cloudsicle.messages.Allocation;
import org.cloudsicle.messages.IMessage;

public class Client implements IMessageHandler {

	private Session s;

	public static void main(String[] args) throws IOException {
		DefaultNetworkVariables.loadDAS4InfoFromConfig();
		try {
			Client client = new Client(args);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public Client(String[] args) throws IOException {
		FTPService.start();

		s = new Session();
		if (args.length > 0 && args[0].equals("test")) {
			testMakespan(args[1]);
		} else {
			Frontend.launch(s);
		}
	}

	@Override
	public void finalize() throws Throwable {
		FTPService.stop();
		super.finalize();
	}

	@Override
	public void process(IMessage message) {
		if (message instanceof Allocation) {
			System.out.println("DEBUG: Received Allocation");
		}

	}

	private void testMakespan(String set) throws UnknownHostException {
		File testset = new File("testsets/" + set + "/");
		File[] filelist = testset.listFiles();
		ArrayList<String> files = new ArrayList<String>();
		for (File f : filelist) {
			files.add(f.getAbsolutePath());
		}

		s.requestCloudSicle(files, InetAddress.getByName("130.161.7.3"));
	}

	private void testScaling(String set) throws UnknownHostException {
		File testset = new File("testsets/" + set + "/");
		File[] filelist = testset.listFiles();
		ArrayList<String> files = new ArrayList<String>();
		for (File f : filelist) {
			files.add(f.getAbsolutePath());
		}

		for (int i = 0; i < 20; i++) {
			s.requestCloudSicle(files, InetAddress.getByName("130.161.7.3"));
			Random r = new Random();
			int sleep = r.nextInt(30000 - 0 + 1) + 0; //wait somewhere between 0 and 30 seconds between submitting jobs
			try {
				Thread.sleep(sleep);
			} catch (InterruptedException e) {}
		}
	}
}
