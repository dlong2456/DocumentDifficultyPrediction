package server;

public interface DocumentFileWriter {

	public String getFileFromBeginning(int percentage, String documentId);

	public String getSubstringFromIndex(String documentId, int index, int substringSize);

	public String getWholeFile(String documentId);

	public void startWritingToDocument(String documentId);

	public void recordCommand(String command);

	public void finishWriting();

	public String getStatusGivenPercentage(int percentage, String documentId);

}
