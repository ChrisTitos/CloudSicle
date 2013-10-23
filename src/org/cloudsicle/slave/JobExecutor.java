package org.cloudsicle.slave;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import org.cloudsicle.main.jobs.CombineJob;
import org.cloudsicle.main.jobs.CompressJob;
import org.cloudsicle.main.jobs.DownloadJob;
import org.cloudsicle.main.jobs.ForwardJob;
import org.cloudsicle.main.jobs.IJob;
import org.cloudsicle.main.jobs.PresentJob;
import org.cloudsicle.main.jobs.ProduceJob;

public class JobExecutor {

	private final IJob job;
	private final JobType type;
	
	private static ConcurrentHashMap<InetAddress, ConcurrentHashMap<Integer, String>> fileSystem = new ConcurrentHashMap<InetAddress, ConcurrentHashMap<Integer, String>>();
	
	public JobExecutor(IJob job){
		this.job = job;
		if (job instanceof CombineJob)
			type = JobType.COMBINE;
		else if (job instanceof CompressJob)
			type = JobType.COMPRESS;
		else if (job instanceof DownloadJob)
			type = JobType.DOWNLOAD;
		else if (job instanceof ForwardJob)
			type = JobType.FORWARD;
		else if (job instanceof PresentJob)
			type = JobType.PRESENT;
		else if (job instanceof ProduceJob)
			type = JobType.PRODUCE;
		else
			type = JobType.UNKNOWN;
	}
	
	/**
	 * Blocking execution of our job.
	 */
	public void run() throws UnknownJobException, IOException{
		switch (type){
		case COMBINE:
			executeCombineJob((CombineJob) job);
			break;
		case COMPRESS:
			executeCompressJob((CompressJob) job);
			break;
		case DOWNLOAD:
			executeDownloadJob((DownloadJob) job);
			break;
		case FORWARD:
			executeForwardJob((ForwardJob) job);
			break;
		case PRESENT:
			executePresentJob((PresentJob) job);
			break;
		case PRODUCE:
			executeProduceJob((ProduceJob) job);
			break;
		case UNKNOWN:
			throw new UnknownJobException();
		default:
			throw new UnknownJobException();
		}
	}
	
	/**
	 * Combine the files specified by fileids in a combinejob.
	 * The output is to be found at the unique InetAddress folder of the
	 * client as "output.gif"
	 * 
	 * @param job The combine job
	 * @throws IOException If creating the output or reading the input failed
	 */
	private void executeCombineJob(CombineJob job) throws IOException{
		GifsicleRunner program = new GifsicleRunner();
		program.setDelay(1);
		program.setLoops(true);
		File output = new File(job.conjureOutputFile());
		ArrayList<File> files = new ArrayList<File>();
		for (int fileid : job.getFiles()){
			File f;
			synchronized (fileSystem){
				f = new File(fileSystem.get(job.getIP()).get(fileid));
			}
			files.add(f);
		}
		program.convert(files, output);
	}
	
	private void executeCompressJob(CompressJob job){
		// TODO
	}
	
	/**
	 * Download a resource from a client and allow each client to 
	 * access their download onto our system by a file ID.
	 * 
	 * @param job The download job to execute
	 * @throws IOException If the file could not be downloaded
	 */
	private void executeDownloadJob(DownloadJob job) throws IOException{
		String file = job.download();
		synchronized (fileSystem){
			if (!fileSystem.containsKey(job.getIP())){
				fileSystem.put(job.getIP(), new ConcurrentHashMap<Integer, String>());
			}
			ConcurrentHashMap<Integer, String> fileMapping = fileSystem.get(job.getIP());
			fileMapping.put(job.getFileID(), file);
		}
	}
	
	private void executeForwardJob(ForwardJob job){
		// TODO
	}
	
	private void executePresentJob(PresentJob job){
		// TODO
	}
	
	private void executeProduceJob(ProduceJob job){
		// TODO
	}
	
	private enum JobType{
		COMBINE, COMPRESS, DOWNLOAD, FORWARD, PRESENT, PRODUCE, UNKNOWN;
	}
}
