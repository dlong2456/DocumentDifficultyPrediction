package server;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class AServer extends Thread implements Server {
	private ServerSocket serverSocket;
	private Socket server;
	private MyWebSocket webSocket;
	private Thread messageReceiver;

	public AServer(int port, MyWebSocket webSocket) throws IOException {
		// Create a serverSocket to accept connections from clients
		serverSocket = new ServerSocket(port);
		System.out.println("Server socket created");
		this.webSocket = webSocket;
		// Add a shutdown hook so we can clean up our resources and tell the
		// client process to kill itself if the server process is terminated
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				// Tell client process to kill itself
				sendMessageToClient("EXIT");
				try {
					// Close the server
					server.close();
					// Close the server socket
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
				// Accept blocks while waiting for a client to connect on the
				// port. Once connected, accept() returns a server to use to
				// communicate with the client.
				server = serverSocket.accept();
				System.out.println("Server connected to " + server.getRemoteSocketAddress());
				// Once we connect to a client, create a message receiver using
				// the new server
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

	// Function to close the server and clean up resources
	public void close() {
		System.out.println("Server terminating");
		// Shut down the client
		sendMessageToClient("EXIT");
		try {
			// Terminate this thread
			this.interrupt();
			// Terminate the message receiver thread
			((AServerMessageReceiver) messageReceiver).terminate();
			// Close the socket being used in the message receiver
			server.close();
			// Close the socket being used here
			// NOTE: This will throw an exception because server.accept() is
			// blocking. Just ignore it (there was not a better solution on the
			// web).
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// This method sends a message to the client process
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
