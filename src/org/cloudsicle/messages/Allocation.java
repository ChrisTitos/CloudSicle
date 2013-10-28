package org.cloudsicle.messages;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.List;

import org.cloudsicle.master.slaves.SlaveVM;

public class Allocation extends AbstractMessage {

	private static final long serialVersionUID = 8123268038063650981L;
	
	/**
	 * A hashmap that maps the VM's to a list of files allocated for that VM
	 */
	private HashMap<InetAddress, HashMap<Integer, String>> allocations;
	
	public Allocation(){
		allocations = new HashMap<InetAddress, HashMap<Integer, String>>();
	}
	
	public void allocate(SlaveVM vm, HashMap<Integer,String> list){
		allocations.put(vm.getIp(), list);
	}
	
	public HashMap<InetAddress, HashMap<Integer, String>> getAllocations(){
		return this.allocations;
	}

}
