package org.cloudsicle.master;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.cloudsicle.communication.DefaultNetworkVariables;
import org.cloudsicle.communication.SocketSender;
import org.cloudsicle.main.jobs.CombineJob;
import org.cloudsicle.main.jobs.CompressJob;
import org.cloudsicle.main.jobs.DownloadJob;
import org.cloudsicle.main.jobs.ForwardJob;
import org.cloudsicle.main.jobs.IJob;
import org.cloudsicle.master.slaves.ResourcePool;
import org.cloudsicle.master.slaves.SlaveVM;
import org.cloudsicle.messages.Activity;
import org.cloudsicle.messages.Allocation;
import org.cloudsicle.messages.JobMetaData;
import org.opennebula.client.ClientConfigurationException;

import com.jcraft.jsch.JSchException;

public class Scheduler implements Runnable {

	private ResourcePool pool;
	private ArrayDeque<JobMetaData> metaJobQueue;
	private Monitor monitor;

	/**
	 * Instantiate a new Scheduler.
	 * @param monitor 
	 * 
	 * @throws ClientConfigurationException
	 */
	public Scheduler(Monitor monitor) throws ClientConfigurationException {
		this.pool = new ResourcePool();
		this.metaJobQueue = new ArrayDeque<JobMetaData>();
		this.monitor = monitor;
		new Thread(this).start();
	}

	/**
	 * Add JobMetaData that has to be scheduled.
	 * 
	 * @param metajob
	 */
	public void schedule(JobMetaData metajob) {
		this.metaJobQueue.push(metajob);
	}
	
	public void vmFinished(SlaveVM vm){
		pool.releaseVM(vm);
	}

	@Override
	public void run() {
		/**
		 * Scheduling (first veeery rough version): - find VM that is not in use
		 * |-> if none available, add new VM to pool and use that one |-> if
		 * pool full, wait (FCFS) - set the VM status to unavailable - send the
		 * VM ID/IP to the client
		 */
		while (true) {
			synchronized (this.metaJobQueue) {
				if (!this.metaJobQueue.isEmpty()) {
					JobMetaData metajob = this.metaJobQueue.pop();
					System.out.println("DEBUG: Sheduling job of "
							+ metajob.getIP());

					SlaveVM vm = this.pool.requestVM();
					if (vm != null) {
						Allocation alloc = new Allocation();
						alloc.allocate(vm, metajob.getFiles()); // for now just
																// give
																// everything to
																// one vm
						createActivity(alloc, metajob.getIP());

					}
				}
			}
		}
	}
	
	private void createActivity(Allocation alloc, InetAddress client) {

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
			CombineJob c = new CombineJob(filelist, client.getHostAddress().replace(".", ""));
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
