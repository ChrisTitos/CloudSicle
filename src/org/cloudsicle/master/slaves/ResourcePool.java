package org.cloudsicle.master.slaves;

import org.opennebula.client.Client;
import org.opennebula.client.ClientConfigurationException;

/**
 * TODO Add VM's to a certain limit <<<
 *
 */
public class ResourcePool {

	private Client openNebula;
	
	public ResourcePool() throws ClientConfigurationException{
		this.openNebula = new Client();
	}
	
	public SlaveVM addVM() throws UninstantiableException{
		SlaveVM slave = new SlaveVM(openNebula);
		// TODO
		return null;
	}
	
	private void removeVM(SlaveVM vm) throws UnreachableVMException{
		// TODO
	}
	
	public void exit(){
		// TODO
	}
}
