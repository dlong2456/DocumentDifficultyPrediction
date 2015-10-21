package commands;

public class AnInsertCommand extends ACommand implements InsertCommand {
	//Unify analagous things with Eclipse Helper
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
}
