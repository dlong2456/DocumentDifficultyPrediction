package commands;

import java.util.Map;

import org.eclipse.ui.IEditorPart;

import edu.cmu.scs.fluorite.commands.AbstractCommand;
import edu.cmu.scs.fluorite.commands.ICommand;
import edu.cmu.scs.fluorite.model.EventRecorder;

public class AStyleCommand extends AbstractCommand implements StyleCommand {

	private int startIndex;
	private int endIndex;
	private String type;

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

	public String getType() {
		return type;
	}

	public void setType(String newType) {
		type = newType;
	}

	@Override
	public void dump() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Map<String, String> getAttributesMap() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, String> getDataMap() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCommandType() {
		return "DocumentStyleCommand";
	}

	@Override
	public String getName() {
		return "Style";
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCategory() {
		return EventRecorder.MacroCommandCategory;
	}

	@Override
	public String getCategoryID() {
		return EventRecorder.MacroCommandCategoryID;
	}

	@Override
	public boolean combine(ICommand anotherCommand) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean execute(IEditorPart target) {
		// TODO Auto-generated method stub
		return false;
	}

}
