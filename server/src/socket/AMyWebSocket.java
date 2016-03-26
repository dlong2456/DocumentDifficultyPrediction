package socket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import predictions.ADocumentPredictionManager;
import predictions.DocumentPredictionManager;

@WebSocket
public class AMyWebSocket implements MyWebSocket {
	private Session session;
	private DocumentPredictionManager predictionManager;

	@OnWebSocketClose
	public void onClose(int statusCode, String reason) {
		SocketManager.getInstance().part(this);
		//stop EventRecorder
		System.out.println("Close: statusCode=" + statusCode + ", reason=" + reason);
	}

	@OnWebSocketError
	public void onError(Throwable t) {
		System.out.println("Error: " + t.getMessage());
	}

	@OnWebSocketConnect
	public void onConnect(Session session) {
		this.session = session;
		SocketManager.getInstance().join(this);
		System.out.println("This: " + this);
		System.out.println("Session: " + this.session);
		System.out.println("Remote: " + getSession().getRemote());
		System.out.println("Connect: " + session.getRemoteAddress().getAddress());
		predictionManager = new ADocumentPredictionManager(this);
	}

	// receives messages from the client(s)
	@OnWebSocketMessage
	public void onMessage(String message) {
		if (message.equals("Message Received")) { // Message from teacher client

		} else { // Message from student client
			MyJSONParser jsonParser = new AMyJSONParser(predictionManager);
			jsonParser.parse(message);
		}

	}

	public Session getSession() {
		return session;
	}

	// TODO: How do I tell which client (student or teacher) I am sending this
	// to? Or do I broadcast and figure it out client-side?
	public void sendMessage(String message) {
		try {
			getSession().getRemote().sendString(message);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
