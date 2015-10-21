package predictions;

public interface CommandPercentage {

	public double getNavigationPercentage();

	public double getDeletePercentage();

	public double getStylePercentage();

	public double getInsertPercentage();
	
	public double getDebugPercentage();

	public void setStylePercentage(double newStylePercentage);

	public void setDeletePercentage(double newDeletePercentage);

	public void setNavigationPercentage(double newNavigationPercentage);

	public void setInsertPercentage(double newInsertPercentage);
	
	public void setDebugPercentage(double newDebugPercentage);

}
