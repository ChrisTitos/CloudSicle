package org.cloudsicle.slave;

import org.cloudsicle.main.jobs.CombineJob;
import org.cloudsicle.main.jobs.CompressJob;
import org.cloudsicle.main.jobs.ForwardJob;
import org.cloudsicle.main.jobs.IJob;
import org.cloudsicle.main.jobs.PresentJob;
import org.cloudsicle.main.jobs.ProduceJob;

public class JobExecutor {

	private final IJob job;
	private final JobType type;
	
	public JobExecutor(IJob job){
		this.job = job;
		if (job instanceof CombineJob)
			type = JobType.COMBINE;
		else if (job instanceof CompressJob)
			type = JobType.COMPRESS;
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
	public void run() throws UnknownJobException{
		switch (type){
		case COMBINE:
			executeCombineJob((CombineJob) job);
			break;
		case COMPRESS:
			executeCompressJob((CompressJob) job);
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
	
	private void executeCombineJob(CombineJob job){
		// TODO
	}
	
	private void executeCompressJob(CompressJob job){
		// TODO
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
		COMBINE, COMPRESS, FORWARD, PRESENT, PRODUCE, UNKNOWN;
	}
}
