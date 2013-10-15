package org.cloudsicle.master;

import java.util.concurrent.PriorityBlockingQueue;

import org.cloudsicle.master.slaves.ResourcePool;
import org.cloudsicle.messages.JobMetaData;
import org.opennebula.client.ClientConfigurationException;

public class Scheduler implements Runnable {
	
	private ResourcePool pool;
	private PriorityBlockingQueue<JobMetaData> metaJobQueue;
	
	/**
	 * Instantiate a new Scheduler.
	 * @throws ClientConfigurationException
	 */
	public Scheduler() throws ClientConfigurationException{
		this.pool = new ResourcePool();
		this.metaJobQueue = new PriorityBlockingQueue<JobMetaData>();
		
		new Thread(this).start();
	}
	
	/**
	 * Add JobMetaData that has to be scheduled.
	 * @param metajob
	 */
	public void schedule(JobMetaData metajob){
		this.metaJobQueue.put(metajob);
	}

	@Override
	public void run() {
		/**
		 * Scheduling (first veeery rough version):
		 * - find VM that is not in use
		 * 	 |-> if none available, add new VM to pool and use that one
		 * 		|-> if pool full, wait (FCFS)
		 * - set the VM status to unavailable
		 * - send the VM ID/IP to the client 
		 */
		while(true){
			if(!this.metaJobQueue.isEmpty()){
				JobMetaData metajob = this.metaJobQueue.poll();
			}
		}		
	}
}
