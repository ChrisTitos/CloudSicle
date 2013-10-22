package org.cloudsicle.messages;

public class StatusUpdate implements IMessage {


	private static final long serialVersionUID = -7130640575707394426L;
	private String message;
	
	public StatusUpdate(String message){
		this.message = message;
	}
	
	public String getMessage(){
		return this.message;
	}

}
