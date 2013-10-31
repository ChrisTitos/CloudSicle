package org.cloudsicle.main.jobs;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import org.cloudsicle.slave.FileLocations;

public class CompressJob extends AbstractJob implements Serializable {

	private static final long serialVersionUID = 8690623578125533948L;
	private final String name;
	
	/**
	 * Create a compression job for the current user.
	 */
	public CompressJob(){
		this.name = "output";
	}
	
	/**
	 * Create a compression job for the current user
	 * with a non-standard output name.
	 * 
	 * @param name The output name
	 */
	public CompressJob(String name){
		this.name = name;
	}
	
	public String getFileName(){
		return name;
	}

	public String conjureOutputFile() throws IOException{
		String out = FileLocations.pathForTar(ip, name);
		File f = new File(out);
		f.createNewFile();
		return out;
	}

	@Override
	public JobType getJobType() {
		return JobType.COMPRESS;
	}
}
