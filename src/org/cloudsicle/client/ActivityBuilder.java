package org.cloudsicle.client;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.cloudsicle.communication.DefaultNetworkVariables;
import org.cloudsicle.main.jobs.CombineJob;
import org.cloudsicle.main.jobs.DownloadJob;
import org.cloudsicle.main.jobs.IJob;
import org.cloudsicle.messages.Activity;
import org.cloudsicle.messages.Allocation;

public class ActivityBuilder {
	
	public static Activity createActivity(Allocation alloc){
		
		ArrayList<IJob> list = new ArrayList<IJob>();
		HashMap<InetAddress, List<String>> allocs = alloc.getAllocations();
		
		for (InetAddress vm : allocs.keySet()) {
			ArrayList<String> files = (ArrayList<String>) allocs.get(vm);
			for(String filename : files){
				DownloadJob d = new DownloadJob(DefaultNetworkVariables.DEFAULT_FTP_PORT, filename.hashCode());
				list.add(d);
			}
			//CombineJob c = new CombineJob();
		}
		
		Activity activity = new Activity(list);
		
		return activity;
	}

}
