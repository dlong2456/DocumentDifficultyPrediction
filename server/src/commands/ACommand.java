package commands;

import java.util.Date;

public class ACommand implements Command {

	private Date timestamp;

	public Date getTimestamp() {
		return timestamp;
	}
	
	public void setTimestamp(Date newTimestamp) {
		timestamp = newTimestamp;
	}

}
