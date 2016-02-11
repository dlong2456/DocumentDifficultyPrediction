package socket;

import java.io.IOException;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

@WebSocket
public class AWebSocketHandler implements WebSocketHandler {
	// List of all current clients being serviced by the handler
	// private Set<WebSocketHandler> clients = new Set<WebSocketHandler>();
	private Session session;

	@OnWebSocketClose
	public void onClose(int statusCode, String reason) {
		// clients.remove(this);
		System.out.println("Close: statusCode=" + statusCode + ", reason=" + reason);
	}

	@OnWebSocketError
	public void onError(Throwable t) {
		System.out.println("Error: " + t.getMessage());
	}

	@OnWebSocketConnect
	public void onConnect(Session session) {
		this.session = session;
		// clients.add(this);
		System.out.println("Connect: " + session.getRemoteAddress().getAddress());
	}

	// receives messages from the client(s)
	@OnWebSocketMessage
	public void onMessage(String message) {
		if (message.equals("Message Received")) { // Message from teacher client

		} else { // Message from student client
			MyJSONParser jsonParser = new AMyJSONParser(this);
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
