package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.json.JSONException;
import org.json.JSONObject;

// TODO: Eventually, handle multiple teachers by using a SocketManager and broadcasting to all
@WebSocket
public class AMyWebSocket implements MyWebSocket {
	private Session session;
	private AServer server;
	private static Session teacherSession;
	private DocumentFileWriter writer;

	@OnWebSocketClose
	public void onClose(int statusCode, String reason) {
		if (this.session == teacherSession) {
			teacherSession = null;
		} else {
			// Close file writer
			writer.finishWriting();
			DocumentWriterManager.getInstance().part(writer);
			// Remove this instance of AMyWebScocket form the SocketManager
			SocketManager.getInstance().part(this);
			// Close the server
			server.close();
		}
		System.out.println("Close: statusCode=" + statusCode + ", reason=" + reason);
	}

	@OnWebSocketError
	public void onError(Throwable t) {
		System.out.println("Error: " + t.getMessage());
	}

	@OnWebSocketConnect
	public void onConnect(Session session) {
		this.session = session;
		try {
			getSession().getRemote().sendString("handshake");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Receives messages from the web client(s)
	@OnWebSocketMessage
	public void onMessage(String message) {
		if (message.equals("teacher")) {
			System.out.println("teacher");
			teacherSession = this.session;
		} else if (message.equals("student")) {
			System.out.println("student");
			try {
				// Create a new server socket
				int port = 0; // passing 0 -> it will find any free port
				Thread t = new AServer(port, this);
				// Save the server reference for later use
				SocketManager.getInstance().join(this);
				server = ((AServer) t);
				// Start the server
				t.start();
				Runtime rt = Runtime.getRuntime();
				// Fork a new process and run the PredictionManager
				String strClassPath = System.getProperty("java.class.path");
				Process proc2 = rt.exec(
						"java -cp " + strClassPath + " client.ADocumentPredictionManager " + ((AServer) t).getPort());
				// Prepare to read the output and errors generated by proc2
				BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc2.getInputStream()));
				BufferedReader stdError = new BufferedReader(new InputStreamReader(proc2.getErrorStream()));
				Thread stdOutPrinter = new StdOutPrinter(stdInput, stdError);
				stdOutPrinter.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (message.contains("wholeDocument")) {
			JSONObject obj = new JSONObject(message);
			String docId = obj.getString("documentId");
			DocumentFileWriter fw = DocumentWriterManager.getInstance().getWriterById(docId);
			if (fw != null) {
				String wholeDoc = fw.getWholeFile(docId);
				sendMessage(wholeDoc);
			}
		} else if (message.contains("documentFromBeginning")) {
			JSONObject obj = new JSONObject(message);
			String docId = obj.getString("documentId");
			long endTime = obj.getLong("endTime");
			DocumentFileWriter fw = DocumentWriterManager.getInstance().getWriterById(docId);
			if (fw != null) {
				String returnString = fw.getFileFromBeginning(endTime, docId);
				sendMessage(returnString);
			}
		} else if (message.contains("documentToEnd")) {
			JSONObject obj = new JSONObject(message);
			String docId = obj.getString("documentId");
			DocumentFileWriter fw = DocumentWriterManager.getInstance().getWriterById(docId);
			if (fw != null) {
				String returnString = fw.getFileToEnd(docId);
				sendMessage(returnString);
			}
		} else if (message.contains("statusHistory")) {

		} else {
			if (server != null) {
				server.sendMessageToClient(message);
			}
			if (message.contains("documentId")) {
				JSONObject obj = new JSONObject(message);
				if (obj.get("type").equals("documentId")) {
					String docIdString = obj.getString("documentIdString");
					// Create a new file writer
					writer = new ADocumentFileWriter();
					DocumentWriterManager.getInstance().join(writer, docIdString);
					writer.startWritingToDocument(docIdString);
				}
			}
		}
	}

	public Session getSession() {
		return session;
	}

	public AServer getServer() {
		return server;
	}

	// Send message to the web client
	public void sendMessage(String message) {
		System.out.println("OUTGOING MESSAGE: " + message);
		try {
			if (teacherSession != null) {
				teacherSession.getRemote().sendString(message);
			} else {
				System.out.println("Teacher Session Null");
			}
			try {
				if (writer != null) {
					writer.recordCommand(message);
				}
				JSONObject obj = new JSONObject(message);
				if (!(obj.has("documentId") && obj.has("status"))) {
					getSession().getRemote().sendString(message);
				}
			} catch (JSONException e) {
				// This exception will be thrown if the message can't be parsed
				// into a JSON object.
				// In this case we will still send it to the student
				getSession().getRemote().sendString(message);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}