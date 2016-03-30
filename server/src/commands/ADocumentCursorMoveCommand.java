package commands;

import java.util.HashMap;
import java.util.Map;

import edu.cmu.scs.fluorite.commands.MoveCaretCommand;

public class ADocumentCursorMoveCommand extends MoveCaretCommand {
	private String left;
	private String top;

	public String getLeft() {
		return left;
	}

	public String getTop() {
		return top;
	}

	public void setTop(String newTop) {
		top = newTop;
	}

	public void setLeft(String newLeft) {
		left = newLeft;
	}

	@Override
	public Map<String, String> getAttributesMap() {
		Map<String, String> attrMap = new HashMap<String, String>();
		attrMap.put("left", left);
		attrMap.put("top", top);
		return attrMap;
	}
	
	@Override
	public String getCommandType() {
		return "ADocumentCursorMoveCommand";
	}

	@Override
	public String getName() {
		return "CursorMove";
	}

}
