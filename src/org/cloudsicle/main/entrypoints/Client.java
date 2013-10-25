package org.cloudsicle.main.entrypoints;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.cloudsicle.client.Session;
import org.cloudsicle.client.gui.Frontend;
import org.cloudsicle.communication.DefaultNetworkVariables;
import org.cloudsicle.communication.IMessageHandler;
import org.cloudsicle.communication.SocketListener;
import org.cloudsicle.communication.SocketSender;
import org.cloudsicle.main.jobs.CombineJob;
import org.cloudsicle.main.jobs.CompressJob;
import org.cloudsicle.main.jobs.DownloadJob;
import org.cloudsicle.main.jobs.ForwardJob;
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
			createActivity((Allocation) message);
		}

	}

	private void createActivity(Allocation alloc) {

		ArrayList<IJob> list = new ArrayList<IJob>();
		HashMap<InetAddress, List<String>> allocs = alloc.getAllocations();

		for (InetAddress vm : allocs.keySet()) {
			SocketSender sender = new SocketSender(false, vm);

			ArrayList<String> files = (ArrayList<String>) allocs.get(vm);
			int[] filelist = new int[files.size()];
			for (String filename : files) {
				DownloadJob d = new DownloadJob(
						DefaultNetworkVariables.DEFAULT_FTP_PORT,
						filename.hashCode());
				list.add(d);
				filelist[files.indexOf(filename)] = filename.hashCode();
			}
			CombineJob c = new CombineJob(filelist, "mygif.gif");
			CompressJob comp = new CompressJob("myresult");
			ForwardJob f = new ForwardJob();
			list.add(c);
			list.add(comp);
			list.add(f);
			
			Activity activity = new Activity(list);

			try {
				System.out.println("Sending Activity to "
						+ vm.getHostAddress());
				sender.send(activity);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSchException e) {
				e.printStackTrace();
			}
		}
	}
}
