package org.cloudsicle.main.entrypoints;

import org.cloudsicle.client.Session;
import org.cloudsicle.client.gui.Frontend;

public class Client {

	public static void main(String[] args) {
		Session s = new Session();
		Frontend.launch(s);
	}

}
