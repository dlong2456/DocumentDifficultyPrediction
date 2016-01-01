package socket;

import java.io.IOException;
import java.util.Set;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

@WebSocket
public class AWebSocketHandler implements WebSocketHandler {
	//List of all current clients being serviced by the handler
//	private Set<WebSocketHandler> clients = new Set<WebSocketHandler>();
	private Session session;
	
	@OnWebSocketClose
	public void onClose(int statusCode, String reason) {
//		clients.remove(this);
		System.out.println("Close: statusCode=" + statusCode + ", reason=" + reason);
	}

	@OnWebSocketError
	public void onError(Throwable t) {
		System.out.println("Error: " + t.getMessage());
	}

	@OnWebSocketConnect
	public void onConnect(Session session) {
		this.session = session;
//		clients.add(this);
		System.out.println("Connect: " + session.getRemoteAddress().getAddress());
	}

	@OnWebSocketMessage
	public void onMessage(String message) {
		//TODO: message is JSON with a statusVal and a statusID
		//After parsing, get(statusID) from statusTable and then setStatus on that instance
		if (message.equals("Message Received")) { //Message from teacher client
			try {
			    this.getSession().getRemote().sendString("Hello World");
			} catch (IOException e) {
			    e.printStackTrace(System.err);
			}
		} else { //Message from student client
			MyJSONParser jsonParser = new AMyJSONParser();
			jsonParser.parse(message);
		}

	}
	
	public Session getSession() {
		return session;
	}
	
	//TODO: How do I tell which client I am sending this to? I only want to send the message to the teacher client
	// Or do i send it to both and just ignore it in the student client?
	// Or maybe on request send info to client and then we are in the right session?
	// Sending a message: 
	//	for(WebSocketHandler client: clients) {
	//	client.getSession().getRemote().sendString(arg0);
	//}
//	try
//	{
//	    remote.sendString("Hello World");
//	}
//	catch (IOException e)
//	{
//	    e.printStackTrace(System.err);
//	}
}
