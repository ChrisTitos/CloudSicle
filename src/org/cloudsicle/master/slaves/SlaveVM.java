package org.cloudsicle.master.slaves;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.opennebula.client.Client;
import org.opennebula.client.OneResponse;
import org.opennebula.client.vm.VirtualMachine;

/**
 * A slave VM as seen by the Master
 */
public class SlaveVM {

	private int id;
	private Client client;
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
		this.client = client;
	}
	
	public void createVM(){
		ClassLoader cl = SlaveVM.class.getClassLoader();
		String vmTemplate = new Scanner(cl.getResourceAsStream("centos-smallnet-qcow2.one")).useDelimiter("\\A").next();
		OneResponse rc = VirtualMachine.allocate(client, vmTemplate);
		
		if (rc.isError())
			throw new UninstantiableException();
		
		id = Integer.parseInt(rc.getMessage());
		vm = new VirtualMachine(id, client);
		
		setUpIp();
	}
	
	public boolean isRunning(){		
		return vm.lcmStateStr() == "RUNNING";
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
		
		Pattern p = Pattern.compile("<DNS><!\\[CDATA\\[*([^\"]*)\\]\\]></DNS>");
		Matcher m = p.matcher(rc.getMessage());
		String ip = "";
		if (m.find()) {
		    ip = m.group(1);
		}
		System.out.println("DEBUG: Created VM with IP " + ip);
		
		try {
			this.ip = InetAddress.getByName(ip);
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
	
	/**
	 * Create the ssh command to launch the remote jar on the slave
	 */
	private String buildCommand(){
		return "ssh root@"
				+ ip
				+ " \"cat > slave.jar\" < slave.jar"
				+ ";ssh root@"
				+ ip
				+" \"java -jar slave.jar\"";
	}
	
	/**
	 * Start the remote jar file
	 * 
	 * @return Whether we succeeded in starting the remote jar
	 */
	public boolean initialize(){
		try {
			Process p = Runtime.getRuntime().exec(buildCommand());
			return true;
		} catch (IOException e){
			return false;
		}
	}
	
}
