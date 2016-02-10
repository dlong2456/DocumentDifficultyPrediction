package predictions;

import config.FactorySingletonInitializer;
import difficultyPrediction.ADifficultyPredictionPluginEventProcessor;
import difficultyPrediction.DifficultyPredictionSettings;
import difficultyPrediction.DifficultyRobot;
import edu.cmu.scs.fluorite.commands.ICommand;
import socket.WebSocketHandler;

public class ADocumentPredictionManager implements DocumentPredictionManager {
	private WebSocketHandler webSocketHandler;

	public ADocumentPredictionManager(WebSocketHandler newWebSocketHandler) {
		webSocketHandler = newWebSocketHandler;
		DifficultyPredictionSettings.setReplayMode(true);
		FactorySingletonInitializer.configure();
		DifficultyRobot.getInstance().addStatusListener(this);
	}

	public void processEvent(ICommand event) {
		ADifficultyPredictionPluginEventProcessor.getInstance().newCommand(event);
	}

	@Override
	public void newStatus(String aStatus) {
		// TODO Auto-generated method stub
	}

	@Override
	public void newAggregatedStatus(String aStatus) {
		// TODO Auto-generated method stub

	}

	// Listens to status events. When a status is received, this creates a new
	// status object and sends the status to the client
	@Override
	public void newStatus(int aStatus) {
		Status status = new AStatus(webSocketHandler);
		status.setStatus(aStatus);
		status.sendStatus();
	}

	@Override
	public void newAggregatedStatus(int aStatus) {
		// TODO Auto-generated method stub

	}

	@Override
	public void newManualStatus(String aStatus) {
		// TODO Auto-generated method stub

	}

}
