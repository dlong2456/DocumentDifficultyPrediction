package difficultyPrediction.metrics;

import java.util.HashMap;
import java.util.Map;

public enum CommandClassificationSchemeName {
	A0("leaveoneouta0/", new AnA0CommandCategories()),
	A1("leaveoneouta1/", new AnA1CommandCategories()),
	A2("leaveoneouta2/", new AnA2CommandCategories()),
	A3("leaveoneouta3/", new AnA3CommandCategories()),
	A4("leaveoneouta4/", new AnA4CommandCategories()),
	A5("leaveoneouta5/", new AnA5CommandCategories());
	
	private String dir;
	protected CommandCategoryMapping initialMapping;
	public static  Map <CommandClassificationSchemeName, CommandCategoryMapping> nameToMapping = new HashMap();
	
	private CommandClassificationSchemeName(String dir, CommandCategoryMapping aCommandCategories) {
		this.dir=dir;
		initialMapping = aCommandCategories;
//		nameToMapping.put(this, aCommandCategories);
		
	}

	public String getSubDir() {
		return this.dir;
		
	}
	public CommandCategoryMapping getCommandCategoryMapping() {
		CommandCategoryMapping aDynamicMapping = nameToMapping.get(this);
		if (aDynamicMapping == null) {
			return initialMapping;
		}
		else {
			return aDynamicMapping;
		}
		
	}
	// will override initial mapping in commandCatgeories
	public static void associate (CommandClassificationSchemeName aName, CommandCategoryMapping aMapping ) {
		nameToMapping.put(aName, aMapping);
	}
}
