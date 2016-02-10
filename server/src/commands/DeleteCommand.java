package commands;

public interface DeleteCommand {

	public int getStartIndex();

	public void setStartIndex(int newStartIndex);

	public int getEndIndex();

	public void setEndIndex(int newEndIndex);

}
