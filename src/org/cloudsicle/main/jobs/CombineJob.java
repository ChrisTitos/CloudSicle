package org.cloudsicle.main.jobs;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;

import org.cloudsicle.communication.INeedOwnIP;

public class CombineJob implements IJob, INeedOwnIP {
	
	private final int[] files;
	private InetAddress ip;
	
	public CombineJob(int[] files){
		this.files = files;
	}
	
	public int[] getFiles(){
		return files;
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
		String out = ip.getHostAddress();
		out = "downloads" + File.separator + out.replaceAll("[.:]", "") + File.separator + "output.gif";
		File f = new File(out);
		f.createNewFile();
		return out;
	}
}
