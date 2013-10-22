package org.cloudsicle.main.jobs;

import java.io.File;
import java.util.ArrayList;

public class ProduceJob implements IJob {
	
	private ArrayList<File> files;
	private int delay;
	private boolean loop;
	
	public ProduceJob(){
		files = new ArrayList<File>();
	}
	
	public void addFile(File f){
		files.add(f);
	}
	
	public void addFiles(ArrayList<File> files){
		for(File f : files){
			this.addFile(f);
		}
	}

	public int getDelay() {
		return delay;
	}

	public void setDelay(int delay) {
		this.delay = delay;
	}

	public boolean isLoop() {
		return loop;
	}

	public void setLoop(boolean loop) {
		this.loop = loop;
	}
	

}
