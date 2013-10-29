package org.cloudsicle.master.slaves;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.cloudsicle.communication.SocketSender;
import org.cloudsicle.main.VMState;
import org.cloudsicle.messages.JobMetaData;
import org.opennebula.client.Client;
import org.opennebula.client.OneResponse;
import org.opennebula.client.vm.VirtualMachine;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

/**
 * A slave VM as seen by the Master
 */
public class SlaveVM {

	private int id;
	private Client client;
	private VirtualMachine vm;
	private InetAddress ip;
	private VMState state;

	private int processedJobs;
	private int assignedJob;

	private long bootStarttime;
	private long bootEndtime;
	private long boottime;
	private long aliveTime;

	/**
	 * Set up a slave VM
	 * 
	 * @param client
	 *            The open nebula client
	 * @throws UninstantiableException
	 *             If something went wrong when initializing
	 */
	public SlaveVM(Client client) throws UninstantiableException {
		processedJobs = 0;
		this.client = client;
	}

	public void createVM() {
		ClassLoader cl = SlaveVM.class.getClassLoader();
		String vmTemplate = new Scanner(
				cl.getResourceAsStream("centos-smallnet-qcow2.one"))
				.useDelimiter("\\A").next();
		vmTemplate = vmTemplate.replace("$USER", System.getenv("USER"));
		OneResponse rc = VirtualMachine.allocate(client, vmTemplate);

		if (rc.isError())
			throw new UninstantiableException();

		id = Integer.parseInt(rc.getMessage());
		vm = new VirtualMachine(id, client);
		this.setBootStarttime(System.currentTimeMillis());

		setUpIp();
	}

	public boolean isRunning() {
		return vm.lcmStateStr().equals("RUNNING");
	}

	/**
	 * Set up the VM ip
	 * 
	 * @throws UninstantiableException
	 *             If something went wrong when interpreting the VM IP
	 */
	private void setUpIp() throws UninstantiableException {
		OneResponse rc = vm.info();

		if (rc.isError())
			throw new UninstantiableException();

		Pattern p = Pattern
				.compile("<IP_PUBLIC><!\\[CDATA\\[*([^\"]*)\\]\\]></IP_PUBLIC>");
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
	public void hardExit() {
		OneResponse result = vm.shutdown();
		if (result.isError())
			System.out.println(result.getErrorMessage());

		this.aliveTime = System.currentTimeMillis() - this.bootStarttime;
	}

	/**
	 * Start the remote jar file
	 * 
	 * @return Whether we succeeded in starting the remote jar
	 */
	public boolean initialize() {
		System.out.println("DEBUG: Deploying slave.jar to VM " + ip);
		SocketSender sender = new SocketSender(false, ip);
		sender.sendFile("slave.jar");
		sender.sendFile("config.txt");

		JSch jsch = new JSch();

		Session session;
		try {
			jsch.addIdentity("~/.ssh/id_dsa");

			session = jsch.getSession("root", ip.getHostAddress(), 22);
			session.setConfig("StrictHostKeyChecking", "no");
			session.connect();
			Channel channel = session.openChannel("exec");
			((ChannelExec) channel).setCommand("java -jar slave.jar");
			channel.connect();
			channel.disconnect();
			session.disconnect();

			return true;

		} catch (JSchException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean testConnection() {
		JSch jsch = new JSch();
		try {
			jsch.addIdentity("~/.ssh/id_dsa");

			Session session = jsch.getSession("opennebula",
					ip.getHostAddress(), 22);
			session.setConfig("StrictHostKeyChecking", "no");

			session.connect();

			session.disconnect();
			return true;
		} catch (JSchException e) {
			System.out.println("No SSH possible to " + ip + ", "
					+ e.getMessage());
			return false;
		}

	}

	public void setState(VMState state) {
		this.state = state;
	}

	public VMState getState() {
		return this.state;
	}

	public void assignJob(JobMetaData job) {
		this.assignedJob = job.getId();
		this.processedJobs++;
	}

	public int getAssignedJob() {
		return assignedJob;
	}

	public long getBootStarttime() {
		return bootStarttime;
	}

	public void setBootStarttime(long bootStarttime) {
		this.bootStarttime = bootStarttime;
	}

	public long getBootEndtime() {
		return bootEndtime;
	}

	public void setBootEndtime(long bootEndtime) {
		this.bootEndtime = bootEndtime;
		this.boottime = this.bootEndtime - this.bootStarttime;
	}

	public long getBoottime() {
		return boottime;
	}

	/**
	 * Get the total running time from creation to termination of this VM.
	 * 
	 * @return
	 */
	public long getRunningTime() {
		return this.aliveTime;
	}

	public String toString() {
		String result = "VM #" + id + ": \n" 
				+ "State: " + state + " \n"
				+ "Boot time: " + (boottime / 1000) + "s \n" 
				+ "Total running time: " + (aliveTime /1000)	+ "s \n"
				+ "Jobs processed: " + processedJobs + "\n" +
						"-----";
		return result;
	}

}
