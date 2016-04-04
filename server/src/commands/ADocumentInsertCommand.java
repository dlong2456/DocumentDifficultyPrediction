package commands;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.ui.IEditorPart;

import edu.cmu.scs.fluorite.commands.AbstractCommand;
import edu.cmu.scs.fluorite.commands.ICommand;

public class ADocumentInsertCommand extends AbstractCommand implements DocumentInsertCommand {

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
		return "DocumentInsertCommand";
	}

	@Override
	public String getName() {
		return "Insert";
	}

	@Override
	public boolean execute(IEditorPart target) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void dump() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Map<String, String> getDataMap() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCategory() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCategoryID() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean combine(ICommand anotherCommand) {
		// TODO Auto-generated method stub
		return false;
	}
}
