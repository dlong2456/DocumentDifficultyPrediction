package server;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class AServerMessageReceiver extends Thread implements ServerMessageReceiver {

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
				// Read input to see if there is a message from the client
				// process
				DataInputStream in = new DataInputStream(server.getInputStream());
				String command = in.readUTF();
				// Currently, command will either be a new status from
				// EclipseHelper or a 'connected' message (indicating that the
				// client process has created its message receiver)
				System.out.println("Sending message from server to web");
				//Once we receive a command, send it to the web client
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
	
	//This method terminates the running thread
	public void terminate() {
		System.out.println("Server message receiver terminating");
		running = false;
		this.interrupt();
	}

}
