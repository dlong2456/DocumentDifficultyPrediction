package server;

import java.net.ServerSocket;

public interface Server {

	public void sendMessageToClient(String message);

	public void close();

	public ServerSocket getServer();

	public int getPort();

}
