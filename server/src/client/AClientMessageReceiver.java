package client;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class AClientMessageReceiver extends Thread {

	private Socket client;
	private DocumentPredictionManager predictionManager;

	public AClientMessageReceiver(Socket newSocket, DocumentPredictionManager predictionManager) {
		System.out.println("Client receiver created");
		client = newSocket;
		this.predictionManager = predictionManager;
		this.predictionManager.sendMessageToServer("Connected");
	}

	public void run() {
		while (true) {
			try {
				InputStream inFromServer = client.getInputStream();
				DataInputStream in = new DataInputStream(inFromServer);
				String command = in.readUTF();
				if (command.equals("EXIT")) {
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
				System.out.println("Client exiting");
				System.exit(0);
			} catch (Throwable e) {
				System.out.println("bad error occured: " + e.getStackTrace());
				e.printStackTrace();
			}
		}
	}

}
