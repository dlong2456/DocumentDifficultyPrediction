package server;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class AServer extends Thread {
	private ServerSocket serverSocket;
	Socket server;
	MyWebSocket webSocket;
	Thread messageReceiver;
	boolean running = true;

	public AServer(int port, MyWebSocket webSocket) throws IOException {
		serverSocket = new ServerSocket(port);
		this.webSocket = webSocket;
		Runtime.getRuntime().addShutdownHook(new Thread(){
		    public void run(){
		    	System.out.println("telling client to exit before i die");
		    	sendMessageToClient("EXIT");
		        try {
					server.close();
					serverSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		    }
		});
	}

	public int getPort() {
		return serverSocket.getLocalPort();
	}

	public ServerSocket getServer() {
		return serverSocket;
	}

	public void run() {
		while (!Thread.currentThread().isInterrupted()) {
			try {
				System.out.println("Waiting for client on port " + serverSocket.getLocalPort() + "...");
				server = serverSocket.accept();
				System.out.println("Server connected to " + server.getRemoteSocketAddress());
				messageReceiver = new AServerMessageReceiver(server, webSocket);
				messageReceiver.start();
			} catch (SocketTimeoutException s) {
				System.out.println("Socket timed out!");
				break;
			} catch (IOException e) {
				e.printStackTrace();
				break;
			}
		}
	}

	public void close() {
		System.out.println("server terminating");
		//Shut down the client
		sendMessageToClient("EXIT");
		try {
			//Terminate this thread
			running = false;
			this.interrupt();
			//Terminate the message receiver thread
			((AServerMessageReceiver) messageReceiver).terminate();
			//Close the socket being used in the message receiver
			server.close();
			//Close the socket being used here
			//NOTE: This will throw an exception because server.accept() is blocking. Just ignore it.
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendMessageToClient(String message) {
		if (server != null) {
			System.out.println("Sending message to client...");
			DataOutputStream out;
			try {
				out = new DataOutputStream(server.getOutputStream());
				out.writeUTF(message);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
