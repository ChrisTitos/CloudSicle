package org.cloudsicle.main.jobs;

import java.io.Serializable;

public class WaitForResultsJob extends AbstractJob implements Serializable {
	
	private static final long serialVersionUID = 2159715527974072864L;
	private int expectedFiles;
	
	public WaitForResultsJob(int expected){
		expectedFiles = expected;
	}
	
	public int getExpectedCount(){
		return expectedFiles;
	}

	@Override
	public JobType getJobType() {
		return JobType.WAITRESULT;
	}

}
