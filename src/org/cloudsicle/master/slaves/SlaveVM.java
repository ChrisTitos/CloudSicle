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
	
	public SlaveVM(Client client) throws UninstantiableException{
		setUpId(client);
		setUpVm(client);
		setUpIp();
	}
	
	private void setUpId(Client client) throws UninstantiableException{
		ClassLoader cl = SlaveVM.class.getClassLoader();
		String vmTemplate = new Scanner(cl.getResourceAsStream("centos-smallnet-qcow2.one")).useDelimiter("\\A").next();
		OneResponse rc = VirtualMachine.allocate(client, vmTemplate);
		
		if (rc.isError())
			throw new UninstantiableException();
		
		id = Integer.parseInt(rc.getMessage());
	}
	
	private void setUpVm(Client client) throws UninstantiableException{
		vm = new VirtualMachine(id, client);
		
		OneResponse rc = vm.hold();
		if (rc.isError())
			throw new UninstantiableException();
	}
	
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
	
	public void setIsInUse(boolean b){
		this.inUse = b;
	}
	
	public boolean isInUse(){
		return inUse;
	}

	public int getId() {
		return id;
	}

	public VirtualMachine getVm() {
		return vm;
	}

	public InetAddress getIp() {
		return ip;
	}
	
}
