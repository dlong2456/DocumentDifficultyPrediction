package predictions;

public interface Status {

	public int getStatus();

	public void setStatus(int newStatus);

	public void sendStatus();

	public void sendStatusAsComment();

}
