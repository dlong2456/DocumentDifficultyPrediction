package commands;

public interface StyleCommand extends Command {
	
	public int getStartIndex();

	public void setStartIndex(int newStartIndex);

	public int getEndIndex();

	public void setEndIndex(int newEndIndex);

	public String getType();

	public void setType(String newType);

}
