package org.cloudsicle.master.slaves;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.cloudsicle.main.VMState;
import org.opennebula.client.Client;
import org.opennebula.client.ClientConfigurationException;

/**
 * Manage all Slave VMs.
 */
public class ResourcePool {

	private Client openNebula;
	private int maxVMs;

	private ArrayList<SlaveVM> vmsInUse = new ArrayList<SlaveVM>();
	private ConcurrentLinkedQueue<SlaveVM> vmsAvailable = new ConcurrentLinkedQueue<SlaveVM>();
	private HashMap<Integer, SlaveVM> allVms = new HashMap<Integer, SlaveVM>();

	private static int VM_TIMEOUT = 6000;

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

	public SlaveVM getVMById(int id) {
		return this.allVms.get(id);
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
		slave.setState(VMState.INIT);
		allVms.put(slave.getId(), slave);

		Thread creator = new Thread() {
			public void run() {
				while (!slave.getVm().lcmStateStr().equals("RUNNING")) {
					slave.getVm().info();
				}
				System.out.println("VM " + slave.getId() + " "
						+ slave.getVm().stateStr());
				while (!slave.testConnection()) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
					}
				}
				System.out.println("VM " + slave.getId()
						+ " SSH connection established");

				if (slave.initialize()) {
					synchronized (vmsAvailable) {
						vmsAvailable.add(slave);
						slave.setState(VMState.WAITING);
						slave.setBootEndtime(System.currentTimeMillis());
					}
					System.out.println("VM " + slave.getId()
							+ " now available. " + vmsAvailable.size()
							+ " VMs in total");
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
	public synchronized void removeVM(SlaveVM vm) throws UnreachableVMException {
		System.out.println("DEBUG: VM " + vm.getId() + " is deleted");
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
	public synchronized SlaveVM requestVM() {
		if (vmsInUse.size() >= maxVMs)
			return null;
		if (vmsAvailable.size() > 0) {
			return getAvailableVM();
		} else {
			try {
				addVM();
				synchronized (vmsAvailable) {
					while (availableVMCount() < 1) {
					} // wait for VM to become available
					return getAvailableVM();
				}
			} catch (UninstantiableException e) {
				return null;
			}
		}
	}

	public ArrayList<SlaveVM> requestVMs(int count) {
		ArrayList<SlaveVM> vms = new ArrayList<SlaveVM>();
		if (vmsInUse.size() >= maxVMs)
			return null;
		if (vmsAvailable.size() > count) {
			for (int i = 0; i < count; i++) {
				vms.add(getAvailableVM());
			}
		} else {
			try {
				for (int i = 0; i < (count - availableVMCount()); i++) {
					addVM();
				}
				while (availableVMCount() < count) {
				} // wait for VM to become available
				for (int i = 0; i < count; i++) {
					vms.add(getAvailableVM());
				}
			} catch (UninstantiableException e) {
				return null;
			}
		}
		return vms;
	}

	private synchronized SlaveVM getAvailableVM() {
		SlaveVM vm = vmsAvailable.poll();
		vmsInUse.add(vm);
		return vm;
	}

	/**
	 * Signal a Slave VM is no longer used It will be removed from the resource
	 * pool after a certain timeout
	 * 
	 * @param vm
	 *            The VM to release.
	 */
	public void releaseVM(SlaveVM vm) {
		System.out.println("DEBUG: VM " + vm.getId() + " is available again");
		vm.setState(VMState.WAITING);
		this.vmsInUse.remove(vm);
		this.vmsAvailable.add(vm);

		final int id = vm.getId();

		Thread timeout = new Thread() {
			public void run() {
				try {
					Thread.sleep(ResourcePool.VM_TIMEOUT);
				} catch (InterruptedException e) {
				}
				synchronized (vmsAvailable) {
					// always keep one VM alive
					if (availableVMCount() > 1) {

						removeVM(allVms.get(id));
					}
				}
			}
		};
		timeout.start();

	}

	public synchronized int availableVMCount() {
		int size = this.vmsAvailable.size();
		return size;
	}
	
	public synchronized int inUseVMCount(){
		return this.vmsInUse.size();
	}

	/**
	 * Hard exit all VMs in our control.
	 */
	public void exit() {
		for (SlaveVM vm : vmsInUse)
			vm.hardExit();
		for (SlaveVM vm : vmsAvailable)
			vm.hardExit();
		vmsInUse.clear();
		vmsAvailable.clear();
		System.out.println(this.allVms.values());

	}

	@Override
	public void finalize() throws Throwable {
		exit();
		super.finalize();
	}
}
