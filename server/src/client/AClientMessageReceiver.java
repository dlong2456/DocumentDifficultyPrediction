package client;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class AClientMessageReceiver extends Thread {

	private Socket client;
	private DocumentPredictionManager predictionManager;
	private boolean running = true;

	public AClientMessageReceiver(Socket newSocket, DocumentPredictionManager predictionManager) {
		System.out.println("Client receiver created");
		client = newSocket;
		this.predictionManager = predictionManager;
	}

	public void run() {
		while (running) {
			try {
				InputStream inFromServer = client.getInputStream();
				DataInputStream in = new DataInputStream(inFromServer);
				String command = in.readUTF();
				if (command.equals("EXIT")) {
					running = false;
					System.out.println("Client exiting");
					//close the socket
					client.close();
					//terminate the program
					System.exit(0);
				} else {
					System.out.println("Client has received message");
					MyJSONParser jsonParser = new AMyJSONParser(predictionManager);
					jsonParser.parse(command);
				}
			} catch (SocketTimeoutException s) {
				System.out.println("Socket timed out!");
				break;
			} catch (IOException e) {
				e.printStackTrace();
				break;
			}
		}
	}

}
