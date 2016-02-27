package commands;

public class BoldCommand extends AStyleCommand {

	@Override
	public String getCommandType() {
		return "DocumentBoldCommand";
	}

	@Override
	public String getName() {
		return "Bold";
	}

}
