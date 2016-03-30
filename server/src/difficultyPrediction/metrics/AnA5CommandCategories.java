package difficultyPrediction.metrics;

import bus.uigen.ObjectEditor;

public class AnA5CommandCategories extends AnA0CommandCategories {
	CommandName[] editOrInsertCommands = { CommandName.DocumentInsertCommand, CommandName.DocumentBoldCommand,
			CommandName.DocumentHighlightCommand, CommandName.DocumentItalicizeCommand,
			CommandName.DocumentUnderlineCommand, CommandName.DocumentDeleteCommand };

	CommandName[] debugCommands = { CommandName.DocumentCollaborationCommand, CommandName.DocumentSpellcheckCommand,
			CommandName.DocumentLargeDeleteCommand, CommandName.DocumentLargeInsertCommand, CommandName.DocumentScrollCommand, CommandName.ADocumentCursorMoveCommand };

	CommandName[] navigationCommands = { };
	CommandName[] focusCommands = { CommandName.DocumentUpdateURLCommand, CommandName.DocumentCreateNewTabCommand,
			CommandName.DocumentSwitchTabsCommand, CommandName.DocumentWindowFocusCommand };

	public AnA5CommandCategories(boolean aMapCategories) {

		super(false); // do not call map commands from super as our variables
						// are not initialized at that point
		if (aMapCategories) {
			mapCategories();
		}

	}

	public AnA5CommandCategories() {
		this(true);
	}

	@Override
	protected CommandName[] editOrInsertCategory() {
		return editOrInsertCommands;
	}

	@Override
	protected CommandName[] focusCategory() {
		return focusCommands;
	}

	@Override
	protected CommandName[] navigationCategory() {
		return navigationCommands;
	}

	@Override
	protected CommandName[] debugCategory() {
		return debugCommands;
	}

	public static void main(String[] args) {
		CommandCategoryMapping commandsToFeatures = new AnA5CommandCategories();
		ObjectEditor.edit(commandsToFeatures);
	}
}
