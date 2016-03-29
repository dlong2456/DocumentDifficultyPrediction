package server;

import java.util.ArrayList;
import java.util.List;

public class SocketManager {

	private static final SocketManager INSTANCE = new SocketManager();

	public static SocketManager getInstance() {
		return INSTANCE;
	}

	private List<MyWebSocket> frontendSockets = new ArrayList<>();

	public void join(MyWebSocket frontendSocket) {
		frontendSockets.add(frontendSocket);
	}

	public void part(MyWebSocket socket) {
		int index = frontendSockets.indexOf(socket);
		frontendSockets.remove(socket);
	}

	public void writeAllMembers(String message) {
		for (MyWebSocket member : frontendSockets) {
			member.getSession().getRemote().sendStringByFuture(message);
		}
	}

	public void writeSpecificMember(String memberName, String message) {
		MyWebSocket member = findMemberByName(memberName);
		member.getSession().getRemote().sendStringByFuture(message);
	}

	public MyWebSocket findMemberByName(String memberName) {
		return frontendSockets.get(0);
		// left as exercise to reader
	}
}
