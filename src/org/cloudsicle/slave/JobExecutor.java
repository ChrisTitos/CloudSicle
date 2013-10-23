package org.cloudsicle.slave;

import org.cloudsicle.main.jobs.IJob;

public class JobExecutor {

	private final IJob job;
	
	public JobExecutor(IJob job){
		this.job = job;
	}
	
	/**
	 * Blocking execution of our job.
	 */
	public void run(){
		
	}
}
