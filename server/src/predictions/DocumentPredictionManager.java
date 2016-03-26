package predictions;

import org.json.JSONObject;

import difficultyPrediction.statusManager.StatusListener;
import edu.cmu.scs.fluorite.commands.ICommand;
import socket.MyWebSocket;

public interface DocumentPredictionManager extends StatusListener {

	public void processEvent(ICommand event);

	public void handleStatusUpdate(JSONObject obj);

	public int getStatus();

	public void setStatus(int newStatus);

	public void setWebSocketHandler(MyWebSocket newWebSocketHandler);

	public void disable();

	public void enable();
}
