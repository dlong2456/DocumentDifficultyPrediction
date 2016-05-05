package client;

import org.json.JSONArray;
import org.json.JSONObject;

import commands.ADocumentCursorMoveCommand;
import commands.ADocumentDeleteCommand;
import commands.ADocumentIdCommand;
import commands.ADocumentInsertCommand;
import commands.ADocumentLargeDeleteCommand;
import commands.ADocumentLargeInsertCommand;
import commands.ADocumentSpellcheckCommand;
import commands.AWindowFocusCommand;
import commands.BoldCommand;
import commands.CollaborationCommand;
import commands.CreateNewTabCommand;
import commands.DocumentDeleteCommand;
import commands.DocumentInsertCommand;
import commands.HighlightCommand;
import commands.ItalicizeCommand;
import commands.ScrollCommand;
import commands.StyleCommand;
import commands.SwitchTabsCommand;
import commands.UnderlineCommand;
import commands.UpdateURLCommand;
import edu.cmu.scs.fluorite.commands.ICommand;

public class AMyJSONParser implements MyJSONParser {

	private DocumentPredictionManager predictionManager;

	public AMyJSONParser(DocumentPredictionManager newPredictionManager) {
		predictionManager = newPredictionManager;
	}

	public Object parse(String jsonString) {
		JSONObject obj = new JSONObject(jsonString);
		if (obj.has("type")) {
			if (obj.get("type").equals("command")) {
				parseCommand(obj);
			} else if (obj.get("type").equals("statusUpdate")) {
				predictionManager.handleStatusUpdate(obj);
			} else if (obj.get("type").equals("documentId")) {
				long longId = obj.getLong("documentId");
				String docIdString = obj.getString("documentIdString");
				ICommand idCommand = new ADocumentIdCommand();
				((ADocumentIdCommand) idCommand).setDocumentIdLong(longId);
				((ADocumentIdCommand) idCommand).setDocumentIdString(docIdString);
				idCommand.setTimestamp(longId);
				predictionManager.setDocumentIdString(docIdString);
				predictionManager.setDocumentId(longId);
				predictionManager.processEvent(idCommand);
			} 
		}
		return obj;
	}

	private void parseCommand(JSONObject obj) {
		predictionManager.sendMessageToServer(obj.toString());
		JSONObject insertCommandObject;
		JSONObject deleteCommandObject;
		JSONObject boldCommandObject;
		JSONObject italicizeCommandObject;
		JSONObject underlineCommandObject;
		JSONObject highlightCommandObject;
		JSONObject scrollCommandObject;
		JSONObject createNewTabCommandObject;
		JSONObject switchTabsCommandObject;
		JSONObject updateURLCommandObject;
		JSONObject spellcheckCommandObject;
		JSONObject collaborationCommandObject;
		JSONObject windowFocusCommandObject;
		JSONObject cursorMoveCommandObject;
		JSONArray insertCommands = obj.getJSONArray("insertCommands");
		for (int i = 0; i < insertCommands.length(); i++) {
			insertCommandObject = insertCommands.getJSONObject(i);
			// if more than 100 characters are being inserted, assume this is a
			// large insert command
			if (insertCommandObject.getString("content").length() > 100) {
				ICommand largeInsertCommand = new ADocumentLargeInsertCommand();
				largeInsertCommand.setTimestamp(insertCommandObject.getLong("timeStamp"));
				((ADocumentLargeInsertCommand) largeInsertCommand).setContent(insertCommandObject.getString("content"));
				((ADocumentLargeInsertCommand) largeInsertCommand).setIndex(insertCommandObject.getInt("index"));
				predictionManager.processEvent((ICommand) largeInsertCommand);
				// otherwise, it is a regular insert command
			} else {
				DocumentInsertCommand insertCommand = new ADocumentInsertCommand();
				((ICommand) insertCommand).setTimestamp(insertCommandObject.getLong("timeStamp"));
				insertCommand.setContent(insertCommandObject.getString("content"));
				insertCommand.setIndex(insertCommandObject.getInt("index"));
				predictionManager.processEvent((ICommand) insertCommand);
			}
		}
		JSONArray deleteCommands = obj.getJSONArray("deleteCommands");
		for (int i = 0; i < deleteCommands.length(); i++) {
			deleteCommandObject = deleteCommands.getJSONObject(i);
			// if more than 100 characters are being deleted, assume this is a
			// large delete command
			if (deleteCommandObject.getInt("endIndex") - deleteCommandObject.getInt("startIndex") > 100) {
				ICommand largeDeleteCommand = new ADocumentLargeDeleteCommand();
				largeDeleteCommand.setTimestamp(deleteCommandObject.getLong("timeStamp"));
				((ADocumentLargeDeleteCommand) largeDeleteCommand).setEndIndex(deleteCommandObject.getInt("endIndex"));
				((ADocumentLargeDeleteCommand) largeDeleteCommand).setStartIndex(deleteCommandObject.getInt("startIndex"));
				predictionManager.processEvent((ICommand) largeDeleteCommand);
				// otherwise, it is a regular delete command
			} else {
				DocumentDeleteCommand deleteCommand = new ADocumentDeleteCommand();
				((ICommand) deleteCommand).setTimestamp(deleteCommandObject.getLong("timeStamp"));
				deleteCommand.setEndIndex(deleteCommandObject.getInt("endIndex"));
				deleteCommand.setStartIndex(deleteCommandObject.getInt("startIndex"));
				predictionManager.processEvent((ICommand) deleteCommand);
			}
		}
		JSONArray boldCommands = obj.getJSONArray("boldCommands");
		for (int i = 0; i < boldCommands.length(); i++) {
			StyleCommand boldCommand = new BoldCommand();
			boldCommandObject = boldCommands.getJSONObject(i);
			((ICommand) boldCommand).setTimestamp(boldCommandObject.getLong("timeStamp"));
			boldCommand.setEndIndex(boldCommandObject.getInt("endIndex"));
			boldCommand.setStartIndex(boldCommandObject.getInt("startIndex"));
			predictionManager.processEvent((ICommand) boldCommand);
		}
		JSONArray underlineCommands = obj.getJSONArray("underlineCommands");
		for (int i = 0; i < underlineCommands.length(); i++) {
			StyleCommand underlineCommand = new UnderlineCommand();
			underlineCommandObject = underlineCommands.getJSONObject(i);
			((ICommand) underlineCommand).setTimestamp(underlineCommandObject.getLong("timeStamp"));
			underlineCommand.setEndIndex(underlineCommandObject.getInt("endIndex"));
			underlineCommand.setStartIndex(underlineCommandObject.getInt("startIndex"));
			predictionManager.processEvent((ICommand) underlineCommand);
		}
		JSONArray italicizeCommands = obj.getJSONArray("italicizeCommands");
		for (int i = 0; i < italicizeCommands.length(); i++) {
			StyleCommand italicizeCommand = new ItalicizeCommand();
			italicizeCommandObject = italicizeCommands.getJSONObject(i);
			((ICommand) italicizeCommand).setTimestamp(italicizeCommandObject.getLong("timeStamp"));
			italicizeCommand.setEndIndex(italicizeCommandObject.getInt("endIndex"));
			italicizeCommand.setStartIndex(italicizeCommandObject.getInt("startIndex"));
			predictionManager.processEvent((ICommand) italicizeCommand);
		}
		JSONArray highlightCommands = obj.getJSONArray("highlightCommands");
		for (int i = 0; i < highlightCommands.length(); i++) {
			StyleCommand highlightCommand = new HighlightCommand();
			highlightCommandObject = highlightCommands.getJSONObject(i);
			((ICommand) highlightCommand).setTimestamp(highlightCommandObject.getLong("timeStamp"));
			highlightCommand.setEndIndex(highlightCommandObject.getInt("endIndex"));
			highlightCommand.setStartIndex(highlightCommandObject.getInt("startIndex"));
			predictionManager.processEvent((ICommand) highlightCommand);
		}
		JSONArray scrollCommands = obj.getJSONArray("scrollCommands");
		for (int i = 0; i < scrollCommands.length(); i++) {
			ICommand scrollCommand = new ScrollCommand();
			scrollCommandObject = scrollCommands.getJSONObject(i);
			scrollCommand.setTimestamp(scrollCommandObject.getLong("timeStamp"));
			predictionManager.processEvent(scrollCommand);
		}
		JSONArray switchTabCommands = obj.getJSONArray("switchTabCommands");
		for (int i = 0; i < switchTabCommands.length(); i++) {
			ICommand switchTabCommand = new SwitchTabsCommand();
			switchTabsCommandObject = switchTabCommands.getJSONObject(i);
			switchTabCommand.setTimestamp(switchTabsCommandObject.getLong("timeStamp"));
			predictionManager.processEvent(switchTabCommand);
		}
		JSONArray windowFocusCommands = obj.getJSONArray("windowFocusCommands");
		for (int i = 0; i < windowFocusCommands.length(); i++) {
			ICommand windowFocusCommand = new AWindowFocusCommand();
			windowFocusCommandObject = windowFocusCommands.getJSONObject(i);
			windowFocusCommand.setTimestamp(windowFocusCommandObject.getLong("timeStamp"));
			predictionManager.processEvent(windowFocusCommand);
		}
		JSONArray updateURLCommands = obj.getJSONArray("updateURLCommands");
		for (int i = 0; i < updateURLCommands.length(); i++) {
			ICommand updateURLCommand = new UpdateURLCommand();
			updateURLCommandObject = updateURLCommands.getJSONObject(i);
			updateURLCommand.setTimestamp(updateURLCommandObject.getLong("timeStamp"));
			predictionManager.processEvent(updateURLCommand);
		}
		JSONArray createNewTabCommands = obj.getJSONArray("createNewTabCommands");
		for (int i = 0; i < createNewTabCommands.length(); i++) {
			ICommand createNewTabCommand = new CreateNewTabCommand();
			createNewTabCommandObject = createNewTabCommands.getJSONObject(i);
			createNewTabCommand.setTimestamp(createNewTabCommandObject.getLong("timeStamp"));
			predictionManager.processEvent(createNewTabCommand);
		}
		JSONArray spellcheckCommands = obj.getJSONArray("spellcheckCommands");
		for (int i = 0; i < spellcheckCommands.length(); i++) {
			ADocumentSpellcheckCommand spellcheckCommand = new ADocumentSpellcheckCommand();
			spellcheckCommandObject = spellcheckCommands.getJSONObject(i);
			((ICommand) spellcheckCommand).setTimestamp(spellcheckCommandObject.getLong("timeStamp"));
			predictionManager.processEvent((ICommand) spellcheckCommand);
		}
		JSONArray collaborationCommands = obj.getJSONArray("collaborationCommands");
		for (int i = 0; i < collaborationCommands.length(); i++) {
			CollaborationCommand collaborationCommand = new CollaborationCommand();
			collaborationCommandObject = collaborationCommands.getJSONObject(i);
			((ICommand) collaborationCommand).setTimestamp(collaborationCommandObject.getLong("timeStamp"));
			predictionManager.processEvent((ICommand) collaborationCommand);
		}
		JSONArray cursorMoveCommands = obj.getJSONArray("cursorCommands");
		for (int i = 0; i < cursorMoveCommands.length(); i++) {
			ADocumentCursorMoveCommand cursorMoveCommand = new ADocumentCursorMoveCommand();
			cursorMoveCommandObject = cursorMoveCommands.getJSONObject(i);
			((ICommand) cursorMoveCommand).setTimestamp(cursorMoveCommandObject.getLong("timeStamp"));
			cursorMoveCommand.setLeft(cursorMoveCommandObject.getString("left"));
			cursorMoveCommand.setTop(cursorMoveCommandObject.getString("top"));
			predictionManager.processEvent((ICommand) cursorMoveCommand);
		}
	}

}