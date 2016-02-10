package predictions;

import java.util.ArrayList;
//TODO: Is this necessary?
public class AStatusTable implements StatusTable {
	
	private ArrayList<Integer> keys = new ArrayList<Integer>();
	private ArrayList<Status> statuses = new ArrayList<Status>();
	
	public int put(int key, Status status) {
		keys.add(key);
		statuses.add(status);
		return key; //returns key so that it can be sent to client and then back to server
	}
	
	public Status get(int key) {
		int index = keys.indexOf(key);
		return statuses.get(index);
	}

}
