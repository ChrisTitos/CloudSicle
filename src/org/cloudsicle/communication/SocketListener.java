package org.cloudsicle.communication;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

import org.cloudsicle.messages.IMessage;

public class SocketListener extends Thread {

	private ServerSocket serverSocket;
	private IMessageHandler mHandler;

	public SocketListener(IMessageHandler m, int port) throws IOException {
		serverSocket = new ServerSocket(port);
		System.out.println("LISTENING ON: " + serverSocket.getInetAddress() + ":" + serverSocket.getLocalPort());
		mHandler = m;
	}
	
	public SocketListener(IMessageHandler m) throws IOException {
		this(m, DefaultNetworkVariables.DEFAULT_PORT);
	}

	public void run() {
		while (true) {
			try {
				Socket s = serverSocket.accept();
				System.out.println("DEBUG: SERVERSOCKET: ACCEPTED CONNECTION");
				Thread t = new Thread(new SocketThread(s, this.mHandler));
				t.start();
			} catch (SocketTimeoutException s) {
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private class SocketThread implements Runnable {

		private Socket socket;
		private IMessageHandler mHandler;

		public SocketThread(Socket s, IMessageHandler mh) {
			socket = s;
			mHandler = mh;
		}

		@Override
		public void run() {
			try {
				InputStream is = socket.getInputStream();
				ObjectInputStream ois = new ObjectInputStream(is);

				IMessage message = (IMessage) ois.readObject();
				if (message instanceof INeedOwnIP)
					((INeedOwnIP) message).setIP(socket.getInetAddress());
				
				mHandler.process(message);
	            socket.close();


			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}

}
