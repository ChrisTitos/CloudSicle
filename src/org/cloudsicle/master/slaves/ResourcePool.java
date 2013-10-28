package org.cloudsicle.master.slaves;

import java.util.ArrayList;
import org.opennebula.client.Client;
import org.opennebula.client.ClientConfigurationException;

/**
 * Manage all Slave VMs.
 */
public class ResourcePool {

	private Client openNebula;
	private int maxVMs;

	private ArrayList<SlaveVM> vmsInUse = new ArrayList<SlaveVM>();
	private ArrayList<SlaveVM> vmsAvailable = new ArrayList<SlaveVM>();

	/**
	 * Initialize the new Resource Pool Defaults to a maximum of 20 VMs.
	 * 
	 * @throws ClientConfigurationException
	 *             If the nebula client failed to initialize
	 */
	public ResourcePool() throws ClientConfigurationException {
		this.openNebula = new Client();
		this.maxVMs = 20;
		addVM(); // create an initial VM
	}

	/**
	 * Initialize the new Resource Pool
	 * 
	 * @param maxvms
	 *            The maximum amount of VMs
	 * @throws ClientConfigurationException
	 *             If the nebula client failed to initialize
	 */
	public ResourcePool(int maxvms) throws ClientConfigurationException {
		this.openNebula = new Client();
		this.maxVMs = maxvms;
	}

	/**
	 * Adds a virtual machine to our pool. It creates a thread that will wait
	 * untill the VM is ready.
	 * 
	 * @throws UninstantiableException
	 *             If something went wrong creating the vm
	 */
	private void addVM() {
		final SlaveVM slave = new SlaveVM(openNebula);
		slave.createVM();
		Thread creator = new Thread() {
			public void run() {
				while (!slave.getVm().lcmStateStr().equals("RUNNING")) {
					slave.getVm().info();
				}
				System.out.println("VM " + slave.getId() + " "
						+ slave.getVm().stateStr());
				while (!slave.testConnection()) {
					try {Thread.sleep(2000);} catch (InterruptedException e) {}
				}
				System.out.println("VM " + slave.getId()
						+ " SSH connection established");
				if (slave.initialize()) {
					vmsAvailable.add(slave);
					System.out.println("VM " + slave.getId() + " now available. " + vmsAvailable.size() + " VMs in total");
				}
			}
		};
		creator.start();
	}

	/**
	 * Remove a virtual machine from our pool.
	 * 
	 * @param vm
	 *            The VM to be removed
	 * @throws UnreachableVMException
	 *             If we could not reach the VM to tell them to shut down
	 */
	private void removeVM(SlaveVM vm) throws UnreachableVMException {
		if (vmsInUse.contains(vm))
			vmsInUse.remove(vm);
		if (vmsAvailable.contains(vm))
			vmsAvailable.remove(vm);
		vm.hardExit();
	}

	/**
	 * Request a new VM for use.
	 * 
	 * @return A Slave VM to work on, or null if we have to wait for a VM to
	 *         start up
	 */
	public SlaveVM requestVM() {
		if (vmsInUse.size() >= maxVMs)
			return null;
		if (vmsAvailable.size() > 0) {
			SlaveVM vm = vmsAvailable.remove(0);
			vmsInUse.add(vm);
			return vm;
		} else {
			try {
				addVM();
				return null;
			} catch (UninstantiableException e) {
				return null;
			}
		}
	}

	/**
	 * Signal a Slave VM is no longer used
	 * 
	 * @param vm
	 *            The VM to release.
	 */
	public void releaseVM(SlaveVM vm) {
		removeVM(vm);
	}
	
	public synchronized int availableVMCount(){
		return this.vmsAvailable.size();
	}

	/**
	 * Hard exit all VMs in our control.
	 */
	public void exit() {
		for (SlaveVM vm : vmsInUse)
			removeVM(vm);
		for (SlaveVM vm : vmsAvailable)
			removeVM(vm);
	}
	
	@Override
	public void finalize() throws Throwable{
		exit();
		super.finalize();
	}
}
