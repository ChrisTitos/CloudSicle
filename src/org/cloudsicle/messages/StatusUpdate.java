package org.cloudsicle.messages;

import org.cloudsicle.main.VMState;
import org.cloudsicle.main.jobs.JobType;

public class StatusUpdate extends AbstractMessage {

	private static final long serialVersionUID = -7130640575707394426L;
	private String message;
	private VMState state;
	private int vmId;
	private JobType jobType;

	public StatusUpdate(String message, int vm, VMState state) {
		this(message, vm, state, JobType.UNKNOWN);
	}

	public StatusUpdate(String message, int vm, VMState state, JobType jobtype) {
		this.message = message;
		this.state = state;
		this.jobType = jobtype;
		this.vmId = vm;
	}

	public String getMessage() {
		return this.message;
	}

	public VMState getState() {
		return state;
	}

	public int getVmId() {
		return vmId;
	}

	public void setVmId(int vmId) {
		this.vmId = vmId;
	}
	
	public void setJobType(JobType jt){
		this.jobType = jt;
	}
	
	public JobType getJobType(){
		return this.jobType;
	}

}
