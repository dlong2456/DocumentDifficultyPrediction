package predictions;

public interface StatusTable {

	public int put(int key, Status status);

	public Status get(int key);

}
