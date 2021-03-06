package org.cloudsicle.master;

import java.util.ArrayDeque;

import org.cloudsicle.main.jobs.JobType;
import org.cloudsicle.master.allocation.IAllocator;
import org.cloudsicle.master.allocation.JobPerVMAllocator;
import org.cloudsicle.master.slaves.ResourcePool;
import org.cloudsicle.master.slaves.SlaveVM;
import org.cloudsicle.messages.JobMetaData;
import org.opennebula.client.ClientConfigurationException;

public class Scheduler implements Runnable {

	private ResourcePool pool;
	private ArrayDeque<JobMetaData> metaJobQueue;
	private Monitor monitor;
	private IAllocator allocator;

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
		this.monitor.setResourcePool(pool);
		this.allocator = new JobPerVMAllocator(this.pool, this.monitor);
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
	
	public void vmFailed(int vmId){
		SlaveVM vm = pool.getVMById(vmId);
		JobMetaData job = this.monitor.jobFailed(vmId);
		vm.initialize(); //redeploy jar
		schedule(job); //reschedule job
	}
	
	public void vmIsDone(int vmId){
		SlaveVM vm = pool.getVMById(vmId);
		this.monitor.moveJobToFinished(vm.getAssignedJob());
		pool.releaseVM(vm);

	}
	
	public void vmStatus(int vmId, JobType jt){
		SlaveVM vm = pool.getVMById(vmId);
		this.monitor.jobStatus(vm.getAssignedJob(), jt);
	}
	
	public void hardExit(){
		pool.exit();
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
							+ metajob.getSender());
					allocator.allocate(metajob);
				}
			}
		}
	}
}
