package main;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.websocket.server.WebSocketHandler;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

import socket.AWebSocketHandler;

public class Driver {

	public static void main(String[] args) throws Exception {
		Server server = new Server(8080);
		WebSocketHandler wsHandler = new WebSocketHandler() {
			@Override
			public void configure(WebSocketServletFactory factory) {
				factory.register(AWebSocketHandler.class);
			}
		};
		server.setHandler(wsHandler);
		server.start();
		server.join();
		// Database db = new ADatabase();
		// System.out.println(db.getStudentStatus(1));
		// db.setStudentStatus(1, "in difficulty");
		// System.out.println(db.getStudentStatus(1));
	}
}