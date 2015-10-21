package commands;

public class ADebugCommand extends ACommand implements DebugCommand {

	private String type;

	public String getType() {
		return type;
	}

	public void setType(String newType) {
		type = newType;
	}

}
