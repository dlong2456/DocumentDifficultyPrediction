package difficultyPrediction.metrics;

import bus.uigen.ObjectEditor;
import difficultyPrediction.metrics.AnA0CommandCategories;
import difficultyPrediction.metrics.CommandCategoryMapping;
import difficultyPrediction.metrics.CommandName;

public class AnA4CommandCategories extends AnA0CommandCategories {

	private CommandName[] insertCategory = { CommandName.DocumentInsertCommand, CommandName.DocumentBoldCommand,
			CommandName.DocumentHighlightCommand, CommandName.DocumentItalicizeCommand,
			CommandName.DocumentUnderlineCommand

	};

	private CommandName[] removeCategory = { CommandName.DocumentDeleteCommand, };

	CommandName[] debugCommands = { CommandName.DocumentCollaborationCommand, CommandName.DocumentSpellcheckCommand,
			CommandName.DocumentLargeDeleteCommand, CommandName.DocumentLargeInsertCommand };

	CommandName[] navigationCommands = { CommandName.DocumentScrollCommand };
	CommandName[] focusCommands = { CommandName.DocumentUpdateURLCommand, CommandName.DocumentCreateNewTabCommand,
			CommandName.DocumentSwitchTabsCommand, CommandName.DocumentWindowFocusCommand };

	public AnA4CommandCategories(boolean aMapCategories) {

		super(false); // do not call map commands from super as our variables
						// are not initialized at that point
		if (aMapCategories) {
			mapCategories();
		}

	}

	public AnA4CommandCategories() {
		this(true);
	}

	@Override
	protected CommandName[] editOrInsertCategory() {
		return insertCategory;
	}

	@Override
	protected CommandName[] removeCategory() {
		return removeCategory;
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
		CommandCategoryMapping commandsToFeatures = new AnA4CommandCategories();
		ObjectEditor.edit(commandsToFeatures);
	}
}