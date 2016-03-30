package commands;

import java.util.Map;

import edu.cmu.scs.fluorite.commands.DifficulyStatusCommand;

public class DocumentStatusCorrection extends DifficulyStatusCommand {

	private String type;
	private String details;

	public DocumentStatusCorrection(Status status) {
		super(status);
	}

	public void setType(String newType) {
		type = newType;
	}

	public void setDetails(String newDetails) {
		details = newDetails;
	}

	public String getType() {
		return type;
	}

	public String getDetails() {
		return details;
	}

	@Override
	public Map<String, String> getAttributesMap() {
		Map<String, String> attrMap = super.getAttributesMap();
		attrMap.put("difficultyType", type);
		attrMap.put("details", details);
		return attrMap;
	}

}
