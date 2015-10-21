package predictions;

public class ACommandPercentage implements CommandPercentage {
	
	private double navigationPercentage;
	private double deletePercentage;
	private double stylePercentage;
	private double insertPercentage;
	private double debugPercentage;

	public double getNavigationPercentage() {
		return navigationPercentage;
	}
	
	public double getDeletePercentage() {
		return deletePercentage;
	}
	
	public double getStylePercentage() {
		return stylePercentage;
	}
	
	public double getInsertPercentage() {
		return insertPercentage;
	}
	
	public double getDebugPercentage() {
		return debugPercentage;
	}
	
	public void setDebugPercentage(double newDebugPercentage) {
		debugPercentage = newDebugPercentage;
	}
	
	public void setStylePercentage(double newStylePercentage) {
		stylePercentage = newStylePercentage;
	}
	
	public void setDeletePercentage(double newDeletePercentage) {
		deletePercentage = newDeletePercentage;
	}
	
	public void setNavigationPercentage(double newNavigationPercentage) {
		navigationPercentage = newNavigationPercentage;
	}
	
	public void setInsertPercentage(double newInsertPercentage) {
		insertPercentage = newInsertPercentage;
	}

}
