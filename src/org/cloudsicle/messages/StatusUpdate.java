package org.cloudsicle.messages;

import org.cloudsicle.main.VMState;

public class StatusUpdate extends AbstractMessage {

	private static final long serialVersionUID = -7130640575707394426L;
	private String message;
	private VMState state;
	private int metajob;
	private int vmId;

	public StatusUpdate(String message, int vm, VMState state) {
		this(message, vm, state, -1);
	}

	public StatusUpdate(String message, int vm, VMState state, int metaJob) {
		this.message = message;
		this.state = state;
		this.metajob = metaJob;
		this.vmId = vm;
	}

	public String getMessage() {
		return this.message;
	}

	public VMState getState() {
		return state;
	}

	public int getMetaJobId() {
		return this.metajob;
	}

	public void setMetaJob(int id) {
		this.metajob = id;
	}

	public int getVmId() {
		return vmId;
	}

	public void setVmId(int vmId) {
		this.vmId = vmId;
	}

}
