package commands;

public class ALargeInsertCommand extends AnInsertCommand {

	@Override
	public String getCommandType() {
		return "DocumentLargeInsertCommand";
	}

	@Override
	public String getName() {
		return "LargeInsert";
	}

}
