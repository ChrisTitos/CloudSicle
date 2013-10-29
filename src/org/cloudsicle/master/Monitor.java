package org.cloudsicle.master;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import org.cloudsicle.messages.JobMetaData;

public class Monitor {

	private ConcurrentHashMap<Integer, JobMetaData> waitingJobs;
	private ConcurrentHashMap<Integer, JobMetaData> runningJobs;
	private ConcurrentHashMap<Integer, JobMetaData> finishedJobs;
	private ConcurrentHashMap<Integer, JobMetaData> failedJobs;

	public Monitor() {
		waitingJobs = new ConcurrentHashMap<Integer, JobMetaData>();
		runningJobs = new ConcurrentHashMap<Integer, JobMetaData>();
		finishedJobs = new ConcurrentHashMap<Integer, JobMetaData>();
		failedJobs = new ConcurrentHashMap<Integer, JobMetaData>();

	}

	public void addjobWaiting(JobMetaData job) {
		waitingJobs.put(job.getId(), job);
	}

	public void moveJobToRunning(int id) {
		JobMetaData job = waitingJobs.remove(id);
		runningJobs.put(id, job);
		
	}

	public void moveJobToFinished(int id) {
		System.out.println("Trying to move Job " + id + " from " + runningJobs.keySet());

		JobMetaData job = runningJobs.remove(id);
		job.setEndtime(System.currentTimeMillis());
		finishedJobs.put(id, job);
	}

	public String toString() {
		String status = "Jobs waiting for execution: " + waitingJobs.size()
				+ "\n" + "Jobs running: " + runningJobs.size() + "\n"
				+ "Finished jobs: " + finishedJobs.size() + "\n";
		long totaltime = 0;
		for(JobMetaData job : finishedJobs.values()){
			totaltime += (job.getEndtime() - job.getStarttime());
		}
		long average = totaltime / finishedJobs.size();
		status += "Average running time of jobs: " + average + " ms";
		return status;
	}
}
