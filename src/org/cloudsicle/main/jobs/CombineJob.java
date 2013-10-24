package org.cloudsicle.main.jobs;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;

import org.cloudsicle.communication.INeedOwnIP;
import org.cloudsicle.slave.FileLocations;

public class CombineJob implements IJob, INeedOwnIP {
	
	private final int[] files;
	private final String name;
	private InetAddress ip;
	
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
