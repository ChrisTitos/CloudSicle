package org.cloudsicle.main.jobs;

import java.io.Serializable;

public class WaitForResultsJob extends AbstractJob implements Serializable {
	
	private int expectedFiles;
	
	public WaitForResultsJob(int expected){
		expectedFiles = expected;
	}
	
	public int getExpectedCount(){
		return expectedFiles;
	}

}
