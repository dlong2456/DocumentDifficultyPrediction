package commands;

public interface InsertCommand extends Command {

	public int getIndex();

	public void setIndex(int newIndex);

	public String getContent();

	public void setContent(String newContent);

}
