package predictions;

import socket.WebSocketHandler;

public class AStatus implements Status {

	private int status; // 1 for making progress, 0 for facing difficulty
	private WebSocketHandler webSocketHandler;

	public AStatus(WebSocketHandler newWebSocketHandler) {
		webSocketHandler = newWebSocketHandler;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int newStatus) {
		status = newStatus;
	}

	// Sends message to client with new integer status
	public void sendStatus() {
		webSocketHandler.sendMessage("Status: " + status);
	}

	// TODO: send boolean val to GoogleAPI to make a comment with the status on
	// client's
	// google doc. This will require predicting what "region" the user is facing
	// difficulty in
	// and also connecting with Google API
	public void sendStatusAsComment() {

	}

}
