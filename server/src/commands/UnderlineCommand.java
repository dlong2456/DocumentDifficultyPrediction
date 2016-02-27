package commands;

public class UnderlineCommand extends AStyleCommand {
	@Override
	public String getCommandType() {
		return "DocumentUnderlineCommand";
	}

	@Override
	public String getName() {
		return "Underline";
	}

}
