package commands;

import edu.cmu.scs.fluorite.commands.CompilationCommand;

public class ADocumentSpellcheckCommand extends CompilationCommand {

	@Override
	public String getCommandType() {
		return "DocumentSpellcheckCommand";
	}

	@Override
	public String getName() {
		return "Spellcheck";
	}

}
