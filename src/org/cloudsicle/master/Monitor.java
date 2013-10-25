package org.cloudsicle.master;

import java.util.ArrayList;

import org.cloudsicle.messages.JobMetaData;

public class Monitor {

	private ArrayList<JobMetaData> waitingJobs;
	private ArrayList<JobMetaData> runningJobs;
	private ArrayList<JobMetaData> finishedJobs;
	private ArrayList<JobMetaData> failedJobs;

	public Monitor() {
		ArrayList<JobMetaData> waitingJobs = new ArrayList<JobMetaData>();
		ArrayList<JobMetaData> runningJobs = new ArrayList<JobMetaData>();
		ArrayList<JobMetaData> finishedJobs = new ArrayList<JobMetaData>();
		ArrayList<JobMetaData> failedJobs = new ArrayList<JobMetaData>();

	}

	public void addjobWaiting(JobMetaData job) {
		waitingJobs.add(job);
	}

	public void removejobWaiting(JobMetaData job) {
		waitingJobs.remove(job);
	}

	public void addjobRunning(JobMetaData job) {
		runningJobs.add(job);
	}

	public void removejobRunning(JobMetaData job) {
		runningJobs.remove(job);
	}

	public void addjobFinished(JobMetaData job) {
		finishedJobs.add(job);
	}

	public void removejobFinished(JobMetaData job) {
		finishedJobs.remove(job);
	}

	public String toString() {
		String status = "Jobs waiting for execution: " + waitingJobs.size()
				+ "\n" + "Jobs running: " + runningJobs.size() + "\n"
				+ "Finished jobs: " + finishedJobs.size() + "\n";
		long totaltime = 0;
		for(JobMetaData job : finishedJobs){
			totaltime += (job.getEndtime() - job.getStarttime());
		}
		long average = totaltime / finishedJobs.size();
		status += "Average running time of jobs: " + average + " ms";
		return status;
	}
}
