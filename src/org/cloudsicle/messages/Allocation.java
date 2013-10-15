package org.cloudsicle.messages;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;

import org.cloudsicle.master.slaves.SlaveVM;

public class Allocation implements IMessage {

	private static final long serialVersionUID = 8123268038063650981L;
	
	/**
	 * A hashmap that maps the VM's to a list of files allocated for that VM
	 */
	private HashMap<InetAddress, ArrayList<String>> allocations;
	
	public Allocation(){
		allocations = new HashMap<InetAddress, ArrayList<String>>();
	}
	
	public void allocate(SlaveVM vm, ArrayList<String> files){
		allocations.put(vm.getIp(), files);
	}
	
	public HashMap<InetAddress, ArrayList<String>> getAllocations(){
		return this.allocations;
	}

}
