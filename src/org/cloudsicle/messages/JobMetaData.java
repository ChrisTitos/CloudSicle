package org.cloudsicle.messages;

import java.net.InetAddress;
import java.util.HashMap;

import org.cloudsicle.communication.INeedOwnIP;
import org.cloudsicle.main.jobs.JobType;

/**
 * The JobMetaData is presented to the master by the client so that the master can allocate
 * VM(s) for the actual job
 */
public class JobMetaData extends AbstractMessage implements INeedOwnIP {

	private static final long serialVersionUID = 4966126383441403840L;
	
	private int id;
	
	private InetAddress client;

	private int totalFileSize;
	private HashMap<Integer, String> files;
	
	private long starttime;
	private long endtime;
	private HashMap<JobType,Long> jobtimes;
	
	public JobMetaData(){
		super();
		jobtimes = new HashMap<JobType, Long>();
		
	}
	
	public void setId(int id){
		this.id = id;
	}
	
	public int getId(){
		return this.id;
	}
	
	public int getTotalFileSize() {
		return totalFileSize;
	}

	public void setTotalFileSize(int totalFileSize) {
		this.totalFileSize = totalFileSize;
	}

	public int getNumFiles() {
		return this.files.size();
	}
	
	public HashMap<Integer, String> getFiles(){
		return this.files;
	}
	
	/**
	 * Pass a HashMap of file id's to file names.
	 * @param files
	 */
	public void setFiles(HashMap<Integer, String> files){
		this.files = files;
	}

	@Override
	public void setIP(InetAddress ip) {
		this.client = ip;
	}

	@Override
	public InetAddress getIP() {
		return this.client;
	}

	public long getStarttime() {
		return starttime;
	}

	public void setStarttime(long starttime) {
		this.starttime = starttime;
		startingJob(JobType.WAITING);
	}

	public long getEndtime() {
		return endtime;
	}

	public void setEndtime(long endtime) {
		this.endtime = endtime;
	}
	
	public void startingJob(JobType type){
		if(!jobtimes.containsKey(type))
			jobtimes.put(type, System.currentTimeMillis());
	}


}
