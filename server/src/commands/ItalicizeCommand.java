package commands;

public class ItalicizeCommand extends AStyleCommand {

	@Override
	public String getCommandType() {
		return "DocumentItalicizeCommand";
	}

	@Override
	public String getName() {
		return "Italicize";
	}

}
