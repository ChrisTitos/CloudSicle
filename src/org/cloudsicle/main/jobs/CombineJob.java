package org.cloudsicle.main.jobs;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.util.ArrayList;

import org.cloudsicle.communication.INeedOwnIP;
import org.cloudsicle.slave.FileLocations;

public class CombineJob extends AbstractJob implements Serializable {

	private static final long serialVersionUID = -855918850745018227L;
	private final ArrayList<Integer> files;
	private final String name;
	
	/**
	 * Create a combine job with a list of file identifiers.
	 * 
	 * @param files The file identifiers to combine into one gif
	 */
	public CombineJob(ArrayList<Integer> files){
		this.files = files;
		this.name = "output";
	}
	
	/**
	 * Create a combine job with a list of file identifiers
	 * with a non-standard output name.
	 * 
	 * @param files The file identifiers to combine into one gif
	 * @param name The output name
	 */
	public CombineJob(ArrayList<Integer> files, String name){
		this.files = files;
		this.name = name;
	}
	
	public ArrayList<Integer> getFiles(){
		return files;
	}
	
	public String getFileName(){
		return name;
	}

	public String conjureOutputFile() throws IOException{
		String out = FileLocations.pathForOutput(ip, name);
		File f = new File(out);
		f.createNewFile();
		return out;
	}
}
