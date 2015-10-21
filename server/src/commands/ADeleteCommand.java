package commands;

public class ADeleteCommand extends ACommand implements DeleteCommand {

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

}
