package org.cloudsicle.main.jobs;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;

import org.cloudsicle.communication.INeedOwnIP;
import org.cloudsicle.slave.FileLocations;

public class CombineJob implements IJob, INeedOwnIP, Serializable {

	private static final long serialVersionUID = -855918850745018227L;
	private final int[] files;
	private final String name;
	private InetAddress ip;
	
	/**
	 * Create a combine job with a list of file identifiers.
	 * 
	 * @param files The file identifiers to combine into one gif
	 */
	public CombineJob(int[] files){
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
	public CombineJob(int[] files, String name){
		this.files = files;
		this.name = name;
	}
	
	public int[] getFiles(){
		return files;
	}
	
	public String getFileName(){
		return name;
	}

	@Override
	public void setIP(InetAddress ip) {
		this.ip = ip;
	}

	@Override
	public InetAddress getIP() {
		return ip;
	}
	
	public String conjureOutputFile() throws IOException{
		String out = FileLocations.pathForOutput(ip, name);
		File f = new File(out);
		f.createNewFile();
		return out;
	}
}
