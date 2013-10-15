package org.cloudsicle.master.slaves;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;

import org.opennebula.client.Client;
import org.opennebula.client.OneResponse;
import org.opennebula.client.vm.VirtualMachine;

/**
 * A slave VM as seen by the Master
 */
public class SlaveVM {

	private int id;
	private VirtualMachine vm;
	private InetAddress ip;
	
	private boolean inUse = false;
	
	/**
	 * Set up a slave VM
	 * 
	 * @param client The open nebula client
	 * @throws UninstantiableException If something went wrong when initializing
	 */
	public SlaveVM(Client client) throws UninstantiableException{
		setUpId(client);
		setUpVm(client);
		setUpIp();
	}
	
	/**
	 * Set up a new VM id
	 * 
	 * @param client The open nebula client
	 * @throws UninstantiableException If something went wrong when requesting a new id
	 */
	private void setUpId(Client client) throws UninstantiableException{
		ClassLoader cl = SlaveVM.class.getClassLoader();
		String vmTemplate = new Scanner(cl.getResourceAsStream("centos-smallnet-qcow2.one")).useDelimiter("\\A").next();
		OneResponse rc = VirtualMachine.allocate(client, vmTemplate);
		
		if (rc.isError())
			throw new UninstantiableException();
		
		id = Integer.parseInt(rc.getMessage());
	}
	
	/**
	 * Set up a VirtualMachine object for this id
	 * 
	 * @param client The open nebula client
	 * @throws UninstantiableException If something went wrong when holding a VM
	 */
	private void setUpVm(Client client) throws UninstantiableException{
		vm = new VirtualMachine(id, client);
		
		OneResponse rc = vm.hold();
		if (rc.isError())
			throw new UninstantiableException();
	}
	
	/**
	 * Set up the VM ip
	 * 
	 * @throws UninstantiableException If something went wrong when interpreting the VM IP
	 */
	private void setUpIp() throws UninstantiableException{
		OneResponse rc = vm.info();			

		if (rc.isError())
			throw new UninstantiableException();
		
		Scanner sc = new Scanner(rc.getMessage());
		while (!"IP=\"".equals(sc.findInLine("IP=\"")))
			sc.nextLine();
		String ip = sc.nextLine();
		
		try {
			this.ip = InetAddress.getByName(ip.substring(0, ip.indexOf('"')));
		} catch (UnknownHostException e) {
			throw new UninstantiableException();
		}
		
	}
	
	/**
	 * Flag if this VM is currently in use by the scheduler
	 * 
	 * @param b New value
	 */
	public void setIsInUse(boolean b){
		this.inUse = b;
	}
	
	/**
	 * @return Whether this VM is in use by the scheduler
	 */
	public boolean isInUse(){
		return inUse;
	}

	/**
	 * @return The id of this VM
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return The open nebula API VM object we own
	 */
	public VirtualMachine getVm() {
		return vm;
	}

	/**
	 * The ip of the VM
	 */
	public InetAddress getIp() {
		return ip;
	}
	
	/**
	 * Stateless exit
	 */
	public void hardExit(){
		vm.delete();
	}
	
}
