package commands;

public interface DeleteCommand extends Command {

	public int getStartIndex();

	public void setStartIndex(int newStartIndex);

	public int getEndIndex();

	public void setEndIndex(int newEndIndex);

}
