package client;

import org.json.JSONObject;

import difficultyPrediction.statusManager.StatusListener;
import edu.cmu.scs.fluorite.commands.ICommand;

public interface DocumentPredictionManager extends StatusListener {

	public void processEvent(ICommand event);

	public void handleStatusUpdate(JSONObject obj);

	public int getStatus();

	public void setStatus(int newStatus);

	public long getDocumentId();

	public void setDocumentId(long newId);

	public void sendMessageToServer(String message);

	public void setDocumentIdString(String docIdString);

}
