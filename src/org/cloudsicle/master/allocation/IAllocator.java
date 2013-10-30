package org.cloudsicle.master.allocation;

import org.cloudsicle.messages.JobMetaData;

public interface IAllocator {
	
	public void allocate(JobMetaData job);

}
