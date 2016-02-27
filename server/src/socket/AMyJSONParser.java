package socket;

import org.json.JSONArray;
import org.json.JSONObject;

import commands.ADebugCommand;
import commands.ADeleteCommand;
import commands.ANavigationCommand;
import commands.AStyleCommand;
import commands.AnInsertCommand;
import commands.DebugCommand;
import commands.DeleteCommand;
import commands.InsertCommand;
import commands.StyleCommand;
import edu.cmu.scs.fluorite.commands.ICommand;
import predictions.ADocumentPredictionManager;
import predictions.DocumentPredictionManager;

public class AMyJSONParser implements MyJSONParser {

	private DocumentPredictionManager predictionManager;

	public AMyJSONParser(WebSocketHandler webSocketHandler) {
		predictionManager = new ADocumentPredictionManager(webSocketHandler);
	}

	public Object parse(String jsonString) {
		JSONObject obj = new JSONObject(jsonString);
		System.out.println(obj);
		if (obj.has("type")) {
			if (obj.get("type").equals("command")) {
				parseCommand(obj);
			} else if (obj.get("type").equals("statusUpdate")) {
				predictionManager.handleStatusUpdate(obj);
			}
		}
		return obj;
	}

	private void parseCommand(JSONObject obj) {
		System.out.println("parsing command");
		JSONObject insertCommandObject;
		JSONObject deleteCommandObject;
		JSONObject styleCommandObject;
		JSONObject navigationCommandObject;
		JSONObject spellcheckCommandObject;
		JSONObject collaborationCommandObject;
		JSONArray insertCommands = obj.getJSONArray("insertCommands");
		for (int i = 0; i < insertCommands.length(); i++) {
			InsertCommand insertCommand = new AnInsertCommand();
			insertCommandObject = insertCommands.getJSONObject(i);
			((ICommand) insertCommand).setTimestamp(insertCommandObject.getLong("timeStamp"));
			insertCommand.setContent(insertCommandObject.getString("content"));
			insertCommand.setIndex(insertCommandObject.getInt("index"));
			 predictionManager.processEvent((ICommand) insertCommand);
		}
		JSONArray deleteCommands = obj.getJSONArray("deleteCommands");
		for (int i = 0; i < deleteCommands.length(); i++) {
			DeleteCommand deleteCommand = new ADeleteCommand();
			deleteCommandObject = deleteCommands.getJSONObject(i);
			((ICommand) deleteCommand).setTimestamp(deleteCommandObject.getLong("timeStamp"));
			deleteCommand.setEndIndex(deleteCommandObject.getInt("endIndex"));
			deleteCommand.setStartIndex(deleteCommandObject.getInt("startIndex"));
			predictionManager.processEvent((ICommand) deleteCommand);
		}
		// TODO: Bug with parsing style commands. "not a JSON object"
		JSONArray styleCommands = obj.getJSONArray("styleCommands");
		System.out.println(styleCommands);
		for (int i = 0; i < styleCommands.length(); i++) {
			StyleCommand styleCommand = new AStyleCommand();
			styleCommandObject = styleCommands.getJSONObject(i);
			((ICommand) styleCommand).setTimestamp(styleCommandObject.getLong("timeStamp"));
			styleCommand.setEndIndex(styleCommandObject.getInt("endIndex"));
			styleCommand.setStartIndex(styleCommandObject.getInt("startIndex"));
			styleCommand.setType(styleCommandObject.getString("type"));
			predictionManager.processEvent((ICommand) styleCommand);
		}
		JSONArray navigationCommands = obj.getJSONArray("navigationCommands");
		for (int i = 0; i < navigationCommands.length(); i++) {
			ICommand navigationCommand = new ANavigationCommand();
			navigationCommandObject = navigationCommands.getJSONObject(i);
			navigationCommand.setTimestamp(navigationCommandObject.getLong("timeStamp"));
			predictionManager.processEvent(navigationCommand);
		}
		JSONArray spellcheckCommands = obj.getJSONArray("spellcheckCommands");
		for (int i = 0; i < spellcheckCommands.length(); i++) {
			DebugCommand debugCommand = new ADebugCommand();
			spellcheckCommandObject = spellcheckCommands.getJSONObject(i);
			((ICommand) debugCommand).setTimestamp(spellcheckCommandObject.getLong("timeStamp"));
			debugCommand.setType(spellcheckCommandObject.getString("type"));
			 predictionManager.processEvent((ICommand) debugCommand);
		}
		JSONArray collaborationCommands = obj.getJSONArray("collaborationCommands");
		for (int i = 0; i < collaborationCommands.length(); i++) {
			DebugCommand debugCommand = new ADebugCommand();
			collaborationCommandObject = collaborationCommands.getJSONObject(i);
			((ICommand) debugCommand).setTimestamp(collaborationCommandObject.getLong("timeStamp"));
			debugCommand.setType("collaborationCommand");
			predictionManager.processEvent((ICommand) debugCommand);
		}
	}

}