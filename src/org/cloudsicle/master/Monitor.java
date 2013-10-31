package org.cloudsicle.master;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.ConcurrentHashMap;

import org.cloudsicle.master.slaves.ResourcePool;
import org.cloudsicle.messages.JobMetaData;

public class Monitor implements Runnable {

	private ConcurrentHashMap<Integer, JobMetaData> waitingJobs;
	private ConcurrentHashMap<Integer, JobMetaData> runningJobs;
	private ConcurrentHashMap<Integer, JobMetaData> finishedJobs;
	private ConcurrentHashMap<Integer, JobMetaData> failedJobs;
	private ResourcePool pool;

	private PrintWriter writer;

	public Monitor() throws IOException {
		waitingJobs = new ConcurrentHashMap<Integer, JobMetaData>();
		runningJobs = new ConcurrentHashMap<Integer, JobMetaData>();
		finishedJobs = new ConcurrentHashMap<Integer, JobMetaData>();
		failedJobs = new ConcurrentHashMap<Integer, JobMetaData>();

		File file = new File("logs" + File.separator + "usage_log_"
				+ System.currentTimeMillis() + ".csv");
		file.createNewFile();

		writer = new PrintWriter(file);
		writer.println("Time,WaitingJobs,RunningJobs,VMsInUse,VMsAvailable");

		new Thread(this).start();

	}

	public void setResourcePool(ResourcePool pool) {
		this.pool = pool;
	}

	public void addjobWaiting(JobMetaData job) {
		waitingJobs.put(job.getId(), job);
	}

	public void moveJobToRunning(int id) {
		JobMetaData job = waitingJobs.remove(id);
		runningJobs.put(id, job);

	}

	public void moveJobToFinished(int id) {
		JobMetaData job = runningJobs.remove(id);
		job.setEndtime(System.currentTimeMillis());
		finishedJobs.put(id, job);
	}

	/**
	 * Add the job to the failed jobs list and also add it to waiting again.
	 * 
	 * @param id
	 * @return The job that failed
	 */
	public JobMetaData jobFailed(int id) {
		JobMetaData job = runningJobs.remove(id);
		failedJobs.put(id, job);
		addjobWaiting(job);

		return job;
	}
	
	public void finalize(){
		writer.close();
	}

	public String toString() {
		String status = "Jobs waiting for execution: " + waitingJobs.size()
				+ "\n" + "Jobs running: " + runningJobs.size() + "\n"
				+ "Finished jobs: " + finishedJobs.size() + "\n"
				+ "Failed jobs (resubmitted): " + failedJobs.size() + "\n";
		long totaltime = 0;
		for (JobMetaData job : finishedJobs.values()) {
			totaltime += (job.getEndtime() - job.getStarttime());
		}
		long average = 0;
		if (finishedJobs.size() > 0)
			average = totaltime / finishedJobs.size();
		status += "Average running time of jobs: " + (average / 1000) + " s";
		return status;
	}

	@Override
	public void run() {
		while (true) {
			if (pool != null) {
				writer.println(System.currentTimeMillis() + ","
						+ waitingJobs.size() + "," + runningJobs.size() + ","
						+ pool.inUseVMCount() + "," + pool.availableVMCount());
				writer.flush();
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
		}
	}
}
