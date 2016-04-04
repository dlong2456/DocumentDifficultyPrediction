package commands;

public interface DocumentInsertCommand {

	public int getIndex();

	public void setIndex(int newIndex);

	public String getContent();

	public void setContent(String newContent);

}
