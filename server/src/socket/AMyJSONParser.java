package socket;

import java.util.Date;

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
import commands.NavigationCommand;
import commands.StyleCommand;
import predictions.ACommandPercentage;
import predictions.CommandPercentage;
import predictions.DecisionTreeModel;

public class AMyJSONParser implements MyJSONParser {

	public Object parse(String jsonString) {
		JSONObject obj = new JSONObject(jsonString);
		if (obj.has("type")) {
			if (obj.get("type").equals("commandPercentage")) {
				CommandPercentage commandPercentages = parseCommandPercentage(obj);
				DecisionTreeModel decisionTreeModel = new DecisionTreeModel();
				decisionTreeModel.predictSituation(commandPercentages.getInsertPercentage(), commandPercentages.getDebugPercentage(), commandPercentages.getNavigationPercentage(), commandPercentages.getStylePercentage(), commandPercentages.getDeletePercentage());
			} else if (obj.get("type").equals("command")) {
				parseCommand(obj);
			}
		}
		return obj;
	}

	private void parseCommand(JSONObject obj) {
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
			insertCommand.setTimestamp(new Date(insertCommandObject.getLong("timeStamp")));
			insertCommand.setContent(insertCommandObject.getString("content"));
			insertCommand.setIndex(insertCommandObject.getInt("index"));
			System.out.println("Insert Command Content: " + insertCommand.getContent());
			System.out.println("Insert Command Index: " + insertCommand.getIndex());
			System.out.println("Insert Command Timestamp: " + insertCommand.getTimestamp());
			// TODO: do something with this command. Store it?
		}
		JSONArray deleteCommands = obj.getJSONArray("deleteCommands");
		for (int i = 0; i < deleteCommands.length(); i++) {
			DeleteCommand deleteCommand = new ADeleteCommand();
			deleteCommandObject = deleteCommands.getJSONObject(i);
			deleteCommand.setTimestamp(new Date(deleteCommandObject.getLong("timeStamp")));
			deleteCommand.setEndIndex(deleteCommandObject.getInt("endIndex"));
			deleteCommand.setStartIndex(deleteCommandObject.getInt("startIndex"));
			System.out.println("Delete Command Timestamp: " + deleteCommand.getTimestamp());
			System.out.println("Delete Command Start Index: " + deleteCommand.getStartIndex());
			System.out.println("Delete Command End Index: " + deleteCommand.getEndIndex());
			// TODO: do something with this command. Store it?
		}
		// TODO: Bug with parsing style commands. "not a JSON object"
		JSONArray styleCommands = obj.getJSONArray("styleCommands");
		System.out.println(styleCommands);
		for (int i = 0; i < styleCommands.length(); i++) {
			StyleCommand styleCommand = new AStyleCommand();
			styleCommandObject = styleCommands.getJSONObject(i);
			styleCommand.setTimestamp(new Date(styleCommandObject.getLong("timeStamp")));
			styleCommand.setEndIndex(styleCommandObject.getInt("endIndex"));
			styleCommand.setStartIndex(styleCommandObject.getInt("startIndex"));
			styleCommand.setType(styleCommandObject.getString("type"));
			System.out.println("Style Command Timestamp: " + styleCommand.getTimestamp());
			System.out.println("Style Command Start Index: " + styleCommand.getStartIndex());
			System.out.println("Style Command End Index: " + styleCommand.getEndIndex());
			System.out.println("Style Command Type: " + styleCommand.getType());
			// TODO: do something with this command. Store it?
		}
		JSONArray navigationCommands = obj.getJSONArray("navigationCommands");
		for (int i = 0; i < navigationCommands.length(); i++) {
			NavigationCommand navigationCommand = new ANavigationCommand();
			navigationCommandObject = navigationCommands.getJSONObject(i);
			navigationCommand.setTimestamp(new Date(navigationCommandObject.getLong("timeStamp")));
			System.out.println("Navigation Command Timestamp: " + navigationCommand.getTimestamp());
			// TODO: do something with this command. Store it?
		}
		JSONArray spellcheckCommands = obj.getJSONArray("spellcheckCommands");
		for (int i = 0; i<spellcheckCommands.length(); i++) {
			DebugCommand debugCommand = new ADebugCommand();
			spellcheckCommandObject = spellcheckCommands.getJSONObject(i);
			debugCommand.setTimestamp(new Date(spellcheckCommandObject.getLong("timeStamp")));
			debugCommand.setType(spellcheckCommandObject.getString("type"));
			System.out.println("Debug Command Timestamp: " + debugCommand.getTimestamp());
			System.out.println("Debug Command Type: " + debugCommand.getType());
			//TODO: do something with this command
		}
		JSONArray collaborationCommands = obj.getJSONArray("collaborationCommands");
		for (int i = 0; i<collaborationCommands.length(); i++) {
			DebugCommand debugCommand = new ADebugCommand();
			collaborationCommandObject = collaborationCommands.getJSONObject(i);
			debugCommand.setTimestamp(new Date(collaborationCommandObject.getLong("timeStamp")));
			debugCommand.setType("collaborationCommand");
			System.out.println("Debug Command Timestamp: " + debugCommand.getTimestamp());
			System.out.println("Debug Command Type: " + debugCommand.getType());
			//TODO: do something with this command
		}
	}

	private CommandPercentage parseCommandPercentage(JSONObject obj) {
		// TODO: Do I need to do error checking if I am sending the objects and
		// they always have these three properties?
		CommandPercentage commandPercentageObject = new ACommandPercentage();
		commandPercentageObject.setNavigationPercentage(obj.getDouble("navigationPercentage"));
		commandPercentageObject.setDeletePercentage(obj.getDouble("deletionPercentage"));
		commandPercentageObject.setInsertPercentage(obj.getDouble("insertionPercentage"));
		commandPercentageObject.setStylePercentage(obj.getDouble("stylePercentage"));
		commandPercentageObject.setDebugPercentage(obj.getDouble("debugPercentage"));
		System.out.println("Navigation Percentage: " + commandPercentageObject.getNavigationPercentage());
		System.out.println("Deletion Percentage: " + commandPercentageObject.getDeletePercentage());
		System.out.println("Insertion Percentage: " + commandPercentageObject.getInsertPercentage());
		System.out.println("Style Percentage: " + commandPercentageObject.getStylePercentage());
		System.out.println("Debug Percentage: " + commandPercentageObject.getDebugPercentage());
		return commandPercentageObject;
	}

}