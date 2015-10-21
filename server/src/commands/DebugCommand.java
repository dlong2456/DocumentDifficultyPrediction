package commands;

public interface DebugCommand extends Command {
	public String getType();

	public void setType(String type);
}
