package org.cloudsicle.master;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.cloudsicle.main.jobs.JobType;
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
	
	public void jobStatus(int id, JobType jt){
		if (runningJobs.containsKey(id))
			runningJobs.get(id).startingJob(jt);
		else if (finishedJobs.containsKey(id))
			finishedJobs.get(id).startingJob(jt);
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
		status += "Average running time of jobs: " + (average / 1000) + " s\n";
		if (finishedJobs.size() > 0)
			for (Entry<JobType, Long> entry : totalTimesForJobs().entrySet()){
				status += "> Spent " + entry.getKey().name() + ": " + (entry.getValue()/finishedJobs.size()) + " ms\n";
			}
		return status;
	}
	
	private EnumMap<JobType, Long> totalTimesForJobs(){
		EnumMap<JobType, Long> out = new EnumMap<JobType, Long>(JobType.class);
		out.put(JobType.COMBINE, 0l);
		out.put(JobType.COMPRESS, 0l);
		out.put(JobType.DOWNLOAD, 0l);
		out.put(JobType.FORWARD, 0l);
		out.put(JobType.UNKNOWN, 0l);
		out.put(JobType.WAITING, 0l);
		out.put(JobType.WAITRESULT, 0l);
		for (JobMetaData job : finishedJobs.values()) {
			EnumMap<JobType,Long> jobTimes = job.getJobTimes();
			List<Long> values = new ArrayList<Long>();
			values.addAll(jobTimes.values());
	        Collections.sort(values);
			for (Entry<JobType, Long> entry : jobTimes.entrySet()){
				int ourTimeIndex = Collections.binarySearch(values, entry.getValue());
				long time = ourTimeIndex == values.size()-1 ? job.getEndtime() - entry.getValue() :
					values.get(ourTimeIndex+1) - entry.getValue();
				out.put(entry.getKey(), out.get(entry.getKey()) + time);
			}
		}
		return out;
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
