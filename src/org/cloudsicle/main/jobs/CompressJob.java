package org.cloudsicle.main.jobs;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;

import org.cloudsicle.communication.INeedOwnIP;
import org.cloudsicle.slave.FileLocations;

public class CompressJob implements IJob, INeedOwnIP {

	private final String name;
	private InetAddress ip;
	
	public CompressJob(String name){
		this.name = name;
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
		String out = FileLocations.pathForTar(ip, name);
		File f = new File(out);
		f.createNewFile();
		return out;
	}
}
