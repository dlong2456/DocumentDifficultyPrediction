package predictions;

public class AStatus implements Status {

	private boolean status; // 1 for making progress, 0 for facing difficulty

	public boolean getStatus() {
		return status;
	}

	public void setStatus(boolean newStatus) {
		status = newStatus;
	}
	
	public void sendStatus() {
		//TODO: send boolean val to client so it can update status (Making Progress vs. Slow progress)
	}
	
	public void sendStatusAsComment() {
		//TODO: send boolean val to GoogleAPI to make a comment on user's google doc
		//returns true if message sent successfully, false if not.  
		//blocking or nonblocking message passing?
	}

}
