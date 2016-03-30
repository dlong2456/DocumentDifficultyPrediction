package commands;

public class ALargeDeleteCommand extends ADeleteCommand {

	@Override
	public String getCommandType() {
		return "DocumentLargeDeleteCommand";
	}

	@Override
	public String getName() {
		return "LargeDelete";
	}

}
