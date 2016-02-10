package predictions;

import difficultyPrediction.statusManager.StatusListener;
import edu.cmu.scs.fluorite.commands.ICommand;

public interface DocumentPredictionManager extends StatusListener {

	public void processEvent(ICommand event);
}
