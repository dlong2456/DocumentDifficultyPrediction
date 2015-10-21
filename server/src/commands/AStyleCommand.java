package commands;

public class AStyleCommand extends ACommand implements StyleCommand {

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

}
