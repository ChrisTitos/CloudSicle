package org.cloudsicle.master.allocation;

import java.util.ArrayList;
import java.util.HashMap;

import org.cloudsicle.communication.SocketSender;
import org.cloudsicle.main.jobs.CombineJob;
import org.cloudsicle.main.jobs.CompressJob;
import org.cloudsicle.main.jobs.DownloadJob;
import org.cloudsicle.main.jobs.ForwardJob;
import org.cloudsicle.main.jobs.IJob;
import org.cloudsicle.main.jobs.WaitForResultsJob;
import org.cloudsicle.master.Monitor;
import org.cloudsicle.master.slaves.ResourcePool;
import org.cloudsicle.master.slaves.SlaveVM;
import org.cloudsicle.messages.Activity;
import org.cloudsicle.messages.Allocation;
import org.cloudsicle.messages.JobMetaData;

public class SplitJobAllocator extends AbstractAllocator {

	private static int CUTOFF = 20;

	public SplitJobAllocator(ResourcePool pool, Monitor monitor) {
		super(pool, monitor);
	}

	@Override
	public void allocate(JobMetaData job) {
		if (job.getNumFiles() < CUTOFF) {
			// We dont split so just use the regular allocator
			IAllocator onejob = new JobPerVMAllocator(pool, monitor);
			onejob.allocate(job);
		} else {
			int vmsneeded = 3;
			ArrayList<SlaveVM> vms = pool.requestVMs(vmsneeded);
			Allocation alloc = new Allocation();

			boolean first = true;
			for (SlaveVM vm : vms) {
				if(first){
					alloc.allocate(vm, null);
					first = false;
				}
				alloc.allocate(vm, job.getFiles());
			}
			createActivity(job, alloc);
		}

	}
	
	private void createActivity(JobMetaData meta, Allocation alloc) {

		HashMap<Integer, HashMap<Integer, String>> allocs = alloc.getAllocations();
		
		//First get the VM that will combine the mid results
		int lastvmId = (new ArrayList<Integer>(allocs.keySet())).remove(0);
		allocs.remove(lastvmId);
		SlaveVM lastvm = this.pool.getVMById(lastvmId);
		WaitForResultsJob wait = new WaitForResultsJob(allocs.keySet().size());
		ArrayList<Integer> midresults = new ArrayList<Integer>();
		
		//Now create the activities for all other VMs, they should forward to the final VM
		for (Integer vmId : allocs.keySet()) {
			ArrayList<IJob> list = new ArrayList<IJob>();

			SlaveVM vm = this.pool.getVMById(vmId);
			//These files will be passed to the CombineJob of the final VM
			midresults.add(Integer.valueOf(vm.getIp().getHostAddress().replaceAll("[.:]", "")));
			
			SocketSender sender = new SocketSender(true, vm.getIp());

			ArrayList<Integer> filelist = new ArrayList<Integer>();
			HashMap<Integer, String> files =  allocs.get(vm.getId());
			filelist.addAll(files.keySet());
			DownloadJob d = new DownloadJob(filelist, meta.getSender());
			CombineJob c = new CombineJob(filelist);
			ForwardJob f = new ForwardJob(false); //don't send tar
			c.setIP(meta.getSender());
			f.setIP(lastvm.getIp());
			list.add(d);
			list.add(c);
			list.add(f);
			
			Activity activity = new Activity(list);
			activity.setClient(meta.getSender());
			activity.setVM(vm);

			try {
				System.out.println("Sending Activity to "
						+ vm.getId() + "@" + vm.getIp().getHostAddress());
				sender.send(activity, true);
			} catch (Exception e) {
				e.printStackTrace();
			} 
		}
		
		wait.setIP(meta.getSender());
		CombineJob combine = new CombineJob(midresults);
		combine.setIP(meta.getSender());
		CompressJob comp = new CompressJob();
		ForwardJob f = new ForwardJob(true);
		comp.setIP(meta.getSender());
		f.setIP(meta.getSender());
		ArrayList<IJob> finallist = new ArrayList<IJob>();
		finallist.add(wait);
		finallist.add(combine);
		finallist.add(comp);
		finallist.add(f);		
		
		Activity activity = new Activity(finallist);
		activity.setClient(meta.getSender());
		activity.setVM(lastvm);

		SocketSender sender = new SocketSender(true, lastvm.getIp());

		try {
			System.out.println("Sending Activity to "
					+ lastvm.getId() + "@" + lastvm.getIp().getHostAddress());
			sender.send(activity, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		monitor.moveJobToRunning(meta.getId());
	}

}
