package org.cloudsicle.messages;

import java.util.ArrayList;

import org.cloudsicle.main.jobs.IJob;

/**
 * An Activiy Message contains an array of Jobs to be executed by the receiver.
 */
public class Activity implements IMessage {
	
	private static final long serialVersionUID = 5915331727737049076L;
	private ArrayList<IJob> jobs;
	
	public Activity(ArrayList<IJob> j){
		jobs = j;
	}
	
	public ArrayList<IJob> getJobs(){
		return jobs;
	}
	
}
