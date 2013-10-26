package org.cloudsicle.master;

import java.io.IOException;
import java.util.ArrayDeque;

import org.cloudsicle.communication.DefaultNetworkVariables;
import org.cloudsicle.communication.SocketSender;
import org.cloudsicle.master.slaves.ResourcePool;
import org.cloudsicle.master.slaves.SlaveVM;
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
						SocketSender sender = new SocketSender(false,
								metajob.getSender());
						Allocation alloc = new Allocation();
						alloc.allocate(vm, metajob.getFiles()); // for now just
																// give
																// everything to
																// one vm
						alloc.setSender();

						try {
							System.out.println("DEBUG: Sending job to "
									+ metajob.getIP());
							sender.send(alloc);
							monitor.removejobWaiting(metajob);
							monitor.addjobRunning(metajob);
						} catch (IOException e) {
							e.printStackTrace();
						} catch (JSchException e) {
							e.printStackTrace();
						}

					}
				}
			}
		}
	}
}
