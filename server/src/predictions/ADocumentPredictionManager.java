package predictions;

import javax.swing.JFileChooser;

import org.json.JSONObject;

import config.FactorySingletonInitializer;
import difficultyPrediction.ADifficultyPredictionPluginEventProcessor;
import difficultyPrediction.DifficultyPredictionSettings;
import difficultyPrediction.DifficultyRobot;
import edu.cmu.scs.fluorite.commands.ICommand;
import edu.cmu.scs.fluorite.model.EventRecorder;
import socket.WebSocketHandler;

public class ADocumentPredictionManager implements DocumentPredictionManager {
	private WebSocketHandler webSocketHandler;
	private static int currentStatus; // 1 for making progress, 0 for facing
										// difficulty

	public ADocumentPredictionManager(WebSocketHandler newWebSocketHandler) {
		webSocketHandler = newWebSocketHandler;
		DifficultyPredictionSettings.setReplayMode(true);
		DifficultyPredictionSettings.setSegmentLength(5);
//		JFileChooser fileChooser = new JFileChooser("C:\\Users\\Duri\\Documents");
		FactorySingletonInitializer.configure();
		EventRecorder.getInstance().initCommands();
		DifficultyRobot.getInstance().addStatusListener(this);
	}

	public void processEvent(ICommand event) {
		System.out.println("sending event");
		EventRecorder.getInstance().recordCommand(event);
//		ADifficultyPredictionPluginEventProcessor.getInstance().newCommand(event);
	}

	@Override
	public void newStatus(String aStatus) {
		// TODO Auto-generated method stub
	}

	@Override
	public void newAggregatedStatus(String aStatus) {
		// TODO Auto-generated method stub
	}

	// Listens to status events. When a status is received, this updates the
	// currentStatus and sends the status to the client
	@Override
	public void newStatus(int aStatus) {
		System.out.println("receiving status");
		currentStatus = aStatus;
		webSocketHandler.sendMessage("{ status: '" + currentStatus + "'}");
	}

	public void handleStatusUpdate(JSONObject obj) {
		System.out.println("Status update: " + obj);
		int newStatus = obj.getInt("makingProgress");
		if (newStatus != currentStatus) {
			currentStatus = newStatus;
			// TODO: Send to EclipseHelper, store the details and difficulty
			// type.
			// TODO: Update status in database and send update to teacher client
		}
	}

	@Override
	public void newAggregatedStatus(int aStatus) {
		// TODO Auto-generated method stub

	}

	@Override
	public void newManualStatus(String aStatus) {
		// TODO Auto-generated method stub

	}

	public int getStatus() {
		return currentStatus;
	}

	public void setStatus(int newStatus) {
		currentStatus = newStatus;
	}

}
