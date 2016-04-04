package commands;

import java.util.HashMap;
import java.util.Map;

import edu.cmu.scs.fluorite.commands.PasteCommand;

public class ADocumentLargeInsertCommand extends PasteCommand {

	private int index;
	private String content;

	public int getIndex() {
		return index;
	}

	public void setIndex(int newIndex) {
		index = newIndex;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String newContent) {
		content = newContent;
	}

	@Override
	public Map<String, String> getAttributesMap() {
		Map<String, String> attrMap = new HashMap<String, String>();
		attrMap.put("index", Integer.toString(index));
		attrMap.put("content", content);
		return attrMap;
	}
	
	@Override
	public String getCommandType() {
		return "DocumentLargeInsertCommand";
	}

	@Override
	public String getName() {
		return "LargeInsert";
	}

}
