package commands;

public class HighlightCommand extends AStyleCommand {

	@Override
	public String getCommandType() {
		return "DocumentHighlightCommand";
	}

	@Override
	public String getName() {
		return "Highlight";
	}

}
