package org.cloudsicle.main;

import java.util.Scanner;

import org.opennebula.client.Client;
import org.opennebula.client.OneResponse;
import org.opennebula.client.vm.VirtualMachine;

public class CloudSicle {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Client opennebula = new Client();

			ClassLoader cl = CloudSicle.class.getClassLoader();
			String vmTemplate = new Scanner(cl.getResourceAsStream("centos-smallnet-qcow2.one")).useDelimiter("\\A").next();
			
			
			System.out.print("Trying to allocate the virtual machine... ");
			OneResponse rc = VirtualMachine.allocate(opennebula, vmTemplate);

			if (rc.isError()) {
				System.out.println("failed!");
				throw new Exception(rc.getErrorMessage());
			}

			// The response message is the new VM's ID
			int newVMID = Integer.parseInt(rc.getMessage());
			System.out.println("ok, ID " + newVMID + ".");

			// We can create a representation for the new VM, using the returned
			// VM-ID
			VirtualMachine vm = new VirtualMachine(newVMID, opennebula);

			// Let's hold the VM, so the scheduler won't try to deploy it
			System.out.print("Trying to hold the new VM... ");
			rc = vm.hold();

			if (rc.isError()) {
				System.out.println("failed!");
				throw new Exception(rc.getErrorMessage());
			}

			// And now we can request its information.
			rc = vm.info();			

			if (rc.isError())
				throw new Exception(rc.getErrorMessage());

			System.out.println();
			System.out
					.println("This is the information OpenNebula stores for the new VM:");
			System.out.println(rc.getMessage() + "\n");

			// This VirtualMachine object has some helpers, so we can access its
			// attributes easily (remember to load the data first using the info
			// method).
			System.out.println("The new VM " + vm.getName() + " has status: "
					+ vm.status());

			// And we can also use xpath expressions
			System.out.println("The path of the disk is");
			System.out.println("\t" + vm.xpath("template/disk/source"));

			// We have also some useful helpers for the actions you can perform
			// on a virtual machine, like cancel or finalize:

			rc = vm.delete();
			System.out.println("\nTrying to finalize (delete) the VM "
					+ vm.getId() + "..."); 
		} catch (Exception e) {

			e.printStackTrace();
		}

	}

}
