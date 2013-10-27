package org.cloudsicle.slave;

import java.io.File;
import java.net.InetAddress;

public class FileLocations {

	public static String pathForOutput(InetAddress ip, String name){
		String out = ip.getHostAddress();
		out = "downloads" + File.separator + out.replaceAll("[.:]", "") + File.separator + name + ".gif";
		return out;
	}
	
	public static String pathForFileid(InetAddress ip, int fileid){
		String out = ip.getHostAddress();
		out = "downloads" + File.separator + out.replaceAll("[.:]", "") + File.separator + fileid + ".gif";
		return out;
	}
	
	public static String folderForIp(InetAddress ip){
		return "downloads" + File.separator + ip.getHostAddress().replaceAll("[.:]", "") + File.separator;
	}
	
	public static String pathForTar(InetAddress ip, String name){
		String out = ip.getHostAddress();
		out = "downloads" + File.separator + out.replaceAll("[.:]", "") + File.separator + name + ".tar.gz";
		return out;
	}
	
}
