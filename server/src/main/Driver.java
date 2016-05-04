package main;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.websocket.server.WebSocketHandler;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

import server.AMyWebSocket;

public class Driver {

	public static void main(String[] args) {
		// Open a Jetty Websocket for the client to connect to
		Server server = new Server(5050); // 8080 for localhost, 5050 for CS
											// server
		WebSocketHandler wsHandler = new WebSocketHandler() {
			@Override
			public void configure(WebSocketServletFactory factory) {
				factory.register(AMyWebSocket.class);
			}
		};
		server.setHandler(wsHandler);
		try {
			server.start();
			server.join();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}