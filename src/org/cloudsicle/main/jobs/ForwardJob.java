package org.cloudsicle.main.jobs;

import java.io.Serializable;
import java.net.InetAddress;

import org.cloudsicle.communication.INeedOwnIP;

public class ForwardJob extends AbstractJob implements Serializable {

	private static final long serialVersionUID = 7668738592907238533L;

	private final boolean compressed; 
	private final String name;
	private String remoteFileName;
	
	/**
	 * Forward the results to the sender of this message.
	 * 
	 * @param compressed Whether we send the job's output.tar.gz (True) or output.gif (False)
	 */
	public ForwardJob(boolean compressed){
		this.compressed = compressed;
		this.name = "output";
	}
	
	/**
	 * Forward the results to the sender of this message
	 * with a non-standard output name.
	 * 
	 * @param compressed Whether we send the job's output.tar.gz (True) or output.gif (False)
	 * @param name The output name
	 */
	public ForwardJob(boolean compressed, String name){
		this.compressed = compressed;
		this.name = name;
	}
	
	public String getFileName(){
		return name;
	}
	
	/**
	 * Are we forwarding output.tar.gz?
	 * 
	 * @return Whether we are forwarding output.tar.gz
	 */
	public boolean isTarForwarder(){
		return compressed;
	}
	
	/**
	 * Are we forwarding output.gif?
	 * (Convenience method for !isTarForwarder())
	 * 
	 * @return Whether we are forwarding output.gif
	 */
	public boolean isGifForwarder(){
		return !compressed;
	}

	/**
	 * Get the filename as it should appear on the receiving end
	 * after forwarding.
	 * 
	 * @return The remote file name
	 */
	public String getRemoteFileName() {
		return remoteFileName;
	}

	/**
	 * Set the filename as it should appear on the remote
	 * system.
	 * 
	 * @param remoteFileName The file name to set
	 */
	public void setRemoteFileName(String remoteFileName) {
		this.remoteFileName = remoteFileName;
	}

}
