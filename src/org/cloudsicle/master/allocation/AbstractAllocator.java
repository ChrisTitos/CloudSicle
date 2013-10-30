package org.cloudsicle.master.allocation;

import org.cloudsicle.master.Monitor;
import org.cloudsicle.master.slaves.ResourcePool;

public abstract class AbstractAllocator implements IAllocator {
	
	protected ResourcePool pool;
	protected Monitor monitor;
	
	public AbstractAllocator(ResourcePool pool, Monitor monitor){
		this.pool = pool;
		this.monitor = monitor;		
	}

}
