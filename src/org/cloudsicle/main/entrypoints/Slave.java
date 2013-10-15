package org.cloudsicle.main.entrypoints;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.cloudsicle.communication.IMessageHandler;
import org.cloudsicle.communication.SocketListener;
import org.cloudsicle.messages.Activity;
import org.cloudsicle.messages.IMessage;
import org.cloudsicle.messages.SoftExit;

public class Slave implements IMessageHandler{

	private SocketListener listener;
	
	/**
	 * Initialize our Slave
	 * 
	 * @throws IOException If we failed to deploy gifsicle on the environment
	 */
	public Slave() throws IOException{
		deployExecutable();
		listener = new SocketListener(this, 17123);
	}
	
	/**
	 * Copy our gifsicle executable from the jar to the environment
	 */
	private void deployExecutable() throws IOException{
		InputStream is = Slave.class.getResourceAsStream("gifsicle");
		FileOutputStream fos = new FileOutputStream(new File("~/gifsicle"));
		while (is.available() > 0)
			fos.write(is.read());
		fos.flush();
		fos.close();
		is.close();
	}
	
	
	
	@Override
	public void process(IMessage message) {
		if (message instanceof Activity){
			// TODO
		} else if (message instanceof SoftExit){
			// TODO
		}
	}

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		Slave slave = new Slave();
	}

}
