package commands;

import java.util.HashMap;
import java.util.Map;

import edu.cmu.scs.fluorite.commands.CutCommand;

public class ADocumentLargeDeleteCommand extends CutCommand {

	private int startIndex;
	private int endIndex;

	public int getStartIndex() {
		return startIndex;
	}

	public void setStartIndex(int newStartIndex) {
		startIndex = newStartIndex;
	}

	public int getEndIndex() {
		return endIndex;
	}

	public void setEndIndex(int newEndIndex) {
		endIndex = newEndIndex;
	}

	@Override
	public Map<String, String> getAttributesMap() {
		Map<String, String> attrMap = new HashMap<String, String>();
		attrMap.put("startIndex", Integer.toString(startIndex));
		attrMap.put("endIndex", Integer.toString(endIndex));
		return attrMap;
	}

	@Override
	public String getCommandType() {
		return "DocumentLargeDeleteCommand";
	}

	@Override
	public String getName() {
		return "LargeDelete";
	}

}
