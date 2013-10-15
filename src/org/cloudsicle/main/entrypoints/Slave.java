package org.cloudsicle.main.entrypoints;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Slave {

	/**
	 * Initialize our Slave
	 * 
	 * @throws IOException If we failed to deploy gifsicle on the environment
	 */
	public Slave() throws IOException{
		deployExecutable();
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
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
