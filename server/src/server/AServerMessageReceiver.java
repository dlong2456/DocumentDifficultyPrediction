package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class AServerMessageReceiver extends Thread {

	private Socket server;
	private MyWebSocket webSocket;
	private boolean running = true;

	public AServerMessageReceiver(Socket newSocket, MyWebSocket webSocket) {
		System.out.println("Server receiver created");
		server = newSocket;
		this.webSocket = webSocket;
	}

	public void run() {
		while (running) {
			try {
				DataInputStream in = new DataInputStream(server.getInputStream());
				String command = in.readUTF();
				//this is a new status
				System.out.println("STATUS MESSAGE SENDING FROM SERVER");
				webSocket.sendMessage(command);
			} catch (SocketTimeoutException s) {
				System.out.println("Socket timed out!");
				break;
			} catch (IOException e) {
				e.printStackTrace();
				break;
			}
		}
	}
	
	public void terminate() {
		System.out.println("server message receiver terminating");
		running = false;
		this.interrupt();
	}

}
