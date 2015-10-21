package commands;

import java.util.Date;

public interface Command {
	

	public Date getTimestamp();
	
	public void setTimestamp(Date newTimestamp);

}
