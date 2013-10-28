package org.cloudsicle.communication;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class DefaultNetworkVariables {

	public static int DEFAULT_PORT 		= 21007;
	public static int DEFAULT_FTP_PORT 	= 21008;
	
	public static String DAS4_USERNAME = null;
	public static String DAS4_PASSWORD = null;
	
	public static void loadDAS4InfoFromConfig() throws IOException{
		Properties prop = new Properties();
		FileInputStream fis = new FileInputStream("config.txt");

		prop.load(fis);
		
		if (!prop.containsKey("username"))
			throw new IOException("Missing username entry for config.txt! (key=username)");
		if (!prop.containsKey("password"))
			throw new IOException("Missing password entry for config.txt! (key=password)");
		
		DAS4_USERNAME = prop.getProperty("username");
		DAS4_PASSWORD = prop.getProperty("password");
		
		if (prop.containsKey("comport"))
			try{
				DEFAULT_PORT = Integer.parseInt((String) prop.get("comport"));
			} catch (NumberFormatException e){}
		
		if (prop.containsKey("ftpport"))
			try{
				DEFAULT_FTP_PORT = Integer.parseInt((String) prop.get("ftpport"));
			} catch (NumberFormatException e){}
	}
}
