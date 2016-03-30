package commands;

import java.util.Map;

import org.eclipse.ui.IEditorPart;
import org.w3c.dom.Element;

import edu.cmu.scs.fluorite.commands.AbstractCommand;
import edu.cmu.scs.fluorite.commands.ICommand;
import edu.cmu.scs.fluorite.model.EventRecorder;

public class ALargeInsertCommand extends AbstractCommand implements InsertCommand {
	
	public ALargeInsertCommand() {

	}

	private int index;
	private String content;

	public int getIndex() {
		return index;
	}

	@Override
	public void createFrom(Element commandElement) {
		super.createFrom(commandElement);
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
		return "DocumentLargeInsertCommand";
	}

	@Override
	public String getName() {
		return "LargeInsert";
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