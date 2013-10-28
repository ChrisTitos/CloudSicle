package org.cloudsicle.messages;

import org.cloudsicle.main.VMState;

public class StatusUpdate extends AbstractMessage {


	private static final long serialVersionUID = -7130640575707394426L;
	private String message;
	private VMState state;
	private int metaId;
	
	public StatusUpdate(String message, VMState state){
		this.message = message;
		this.state = state;
	}
	
	public String getMessage(){
		return this.message;
	}
	
	public VMState getState(){
		return state;
	}

}
