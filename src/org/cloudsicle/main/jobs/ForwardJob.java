package org.cloudsicle.main.jobs;

import java.io.Serializable;
import java.net.InetAddress;

import org.cloudsicle.communication.INeedOwnIP;

public class ForwardJob implements IJob, Serializable, INeedOwnIP {

	private static final long serialVersionUID = 7668738592907238533L;

	private InetAddress ip;
	private final InetAddress target;
	private final boolean compressed; 
	private final String name;
	
	/**
	 * Forward the results to the sender of this message.
	 * 
	 * @param compressed Whether we send the job's output.tar.gz (True) or output.gif (False)
	 */
	public ForwardJob(boolean compressed){
		this.target = null;
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
		this.target = null;
		this.compressed = compressed;
		this.name = name;
	}
	
	/**
	 * Forward the results to a specified target.
	 * 
	 * @param compressed Whether we send the job's output.tar.gz (True) or output.gif (False)
	 * @param target The target to send results to
	 */
	public ForwardJob(boolean compressed, InetAddress target){
		this.target = target;
		this.compressed = compressed;
		this.name = "output";
	}
	
	/**
	 * Forward the results to a specified target
	 * with a non-standard output name.
	 * 
	 * @param compressed Whether we send the job's output.tar.gz (True) or output.gif (False)
	 * @param target The target to send results to
	 * @param name The output name
	 */
	public ForwardJob(boolean compressed, String name, InetAddress target){
		this.target = target;
		this.compressed = compressed;
		this.name = name;
	}
	
	@Override
	public void setIP(InetAddress ip) {
		this.ip = ip;
	}

	@Override
	public InetAddress getIP() {
		return ip;
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
	 * Get the target to forward to
	 * Falls back to the sender ip if no target was given.
	 * 
	 * @return The target of our forwarding job
	 */
	public InetAddress getTarget(){
		if (target == null)
			return ip;
		return target;
	}

}
