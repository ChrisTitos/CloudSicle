package org.cloudsicle.slave;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import org.cloudsicle.communication.FTPService;
import org.cloudsicle.communication.SocketSender;
import org.cloudsicle.main.VMState;
import org.cloudsicle.main.jobs.CombineJob;
import org.cloudsicle.main.jobs.CompressJob;
import org.cloudsicle.main.jobs.DownloadJob;
import org.cloudsicle.main.jobs.ForwardJob;
import org.cloudsicle.main.jobs.IJob;
import org.cloudsicle.messages.StatusUpdate;
import org.kamranzafar.jtar.TarEntry;
import org.kamranzafar.jtar.TarOutputStream;

import com.jcraft.jsch.JSchException;

public class JobExecutor {

	private final IJob job;
	private final JobType type;
	private SocketSender updateable;
	
	private static ConcurrentHashMap<InetAddress, ConcurrentHashMap<Integer, String>> fileSystem = new ConcurrentHashMap<InetAddress, ConcurrentHashMap<Integer, String>>();
	
	public JobExecutor(IJob job, SocketSender updater){
		this.updateable = updater;
		this.job = job;
		if (job instanceof CombineJob)
			type = JobType.COMBINE;
		else if (job instanceof CompressJob)
			type = JobType.COMPRESS;
		else if (job instanceof DownloadJob)
			type = JobType.DOWNLOAD;
		else if (job instanceof ForwardJob)
			type = JobType.FORWARD;
		else
			type = JobType.UNKNOWN;
	}
	
	/**
	 * Blocking execution of our job.
	 * @throws JSchException 
	 */
	public void run() throws UnknownJobException, IOException, JSchException{
		switch (type){
		case COMBINE:
			executeCombineJob((CombineJob) job);
			break;
		case COMPRESS:
			executeCompressJob((CompressJob) job);
			break;
		case DOWNLOAD:
			executeDownloadJob((DownloadJob) job);
			break;
		case FORWARD:
			executeForwardJob((ForwardJob) job);
			break;
		case UNKNOWN:
			throw new UnknownJobException();
		default:
			throw new UnknownJobException();
		}
	}
	
	/**
	 * Combine the files specified by fileids in a combinejob.
	 * The output is to be found at the unique InetAddress folder of the
	 * client as "output.gif"
	 * 
	 * @param job The combine job
	 * @throws IOException If creating the output or reading the input failed
	 */
	private void executeCombineJob(CombineJob job) throws IOException{
		GifsicleRunner program = new GifsicleRunner();
		program.setDelay(1);
		program.setLoops(true);
		File output = new File(job.conjureOutputFile());
		ArrayList<File> files = new ArrayList<File>();
		for (int fileid : job.getFiles()){
			File f;
			synchronized (fileSystem){
				f = new File(fileSystem.get(job.getIP()).get(fileid));
			}
			files.add(f);
		}
		program.convert(files, output);
	}
	
	/**
	 * Compress the output file of a previous combine job
	 * 
	 * @param job
	 * @throws IOException
	 */
	private void executeCompressJob(CompressJob job) throws IOException{
		File output = new File(job.conjureOutputFile());
		File input = new File(FileLocations.pathForOutput(job.getIP(), job.getFileName()));
		FileOutputStream fos = new FileOutputStream(output);
		TarOutputStream tos = new TarOutputStream(fos);
		tos.putNextEntry(new TarEntry(input, input.getName()));
	    FileInputStream fis = new FileInputStream(input);

		int data = 0;
		
		while((data = fis.read()) != -1) {
			tos.write(data);
		}
		
		tos.flush();
		fis.close();
	    tos.close();
	}
	
	/**
	 * Download a resource from a client and allow each client to 
	 * access their download onto our system by a file ID.
	 * 
	 * @param job The download job to execute
	 * @throws IOException If the file could not be downloaded
	 */
	private void executeDownloadJob(DownloadJob job) throws IOException, JSchException{
		updateable.send(new StatusUpdate("VM Executing DownloadJob", VMState.EXECUTING));

		boolean success = FTPService.downloadSock(job.getUploaderIP(), job.getSession(), "test" + File.separator);
		synchronized (fileSystem){
			if (!fileSystem.containsKey(job.getUploaderIP())){
				fileSystem.put(job.getUploaderIP(), new ConcurrentHashMap<Integer, String>());
			}
			ConcurrentHashMap<Integer, String> fileMapping = fileSystem.get(job.getUploaderIP());
			//fileMapping.put(job.getFileID(), file);
			
		}
		
		updateable.send(new StatusUpdate("VM DownloadJob result: " + success, VMState.EXECUTING));

	}
	
	/**
	 * Forward the specified output and associated resource files
	 * to another peer.
	 * 
	 * @param job The forward job to execute
	 * @throws IOException If the file could not be forwarded
	 */
	private void executeForwardJob(ForwardJob job) throws IOException, JSchException{
		InetAddress ip = job.getTarget();
		String file = "";
		// Note that the path for output IS NOT THE SAME as the ip to forward to
		if (job.isTarForwarder()){
			file = FileLocations.pathForTar(job.getIP(), job.getFileName());
		} else {
			file = FileLocations.pathForOutput(job.getIP(), job.getFileName());
		}
		// TODO forward the contents of 'file' to 'ip'
		// Now that we have completed our business, clear up all the resources associated with
		// this set of jobs.
		if (job.isTarForwarder())
			new File(FileLocations.pathForTar(job.getIP(), job.getFileName())).delete();
		new File(FileLocations.pathForOutput(job.getIP(), job.getFileName())).delete();
		ConcurrentHashMap<Integer, String> fileMapping = null;
		synchronized (fileSystem){
			fileMapping = fileSystem.remove(job.getIP());
		}
		for (String path : fileMapping.values())
			new File(path).delete();
		updateable.send(new StatusUpdate("VM Done processing all jobs.", VMState.DONE)); //we are done
	}
	
	private enum JobType{
		COMBINE, COMPRESS, DOWNLOAD, FORWARD, PRESENT, PRODUCE, UNKNOWN;
	}
}
