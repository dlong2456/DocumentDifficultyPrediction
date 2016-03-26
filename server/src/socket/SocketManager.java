package socket;

import java.util.ArrayList;
import java.util.List;

public class SocketManager {

	private static final SocketManager INSTANCE = new SocketManager();

	public static SocketManager getInstance() {
		return INSTANCE;
	}

	private List<MyWebSocket> members = new ArrayList<>();

	public void join(MyWebSocket socket) {
		members.add(socket);
	}

	public void part(MyWebSocket socket) {
		members.remove(socket);
	}

	public void writeAllMembers(String message) {
		for (MyWebSocket member : members) {
			member.getSession().getRemote().sendStringByFuture(message);
		}
	}

	public void writeSpecificMember(String memberName, String message) {
		MyWebSocket member = findMemberByName(memberName);
		member.getSession().getRemote().sendStringByFuture(message);
	}

	public MyWebSocket findMemberByName(String memberName) {
		return members.get(0);
		// left as exercise to reader
	}
}
