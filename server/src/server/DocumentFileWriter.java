package server;

public interface DocumentFileWriter {

	public String getFileFromBeginning(long endTime, String documentId);

	public String getFileToEnd(String documentId);

	public String getWholeFile(String documentId);

	public void startWritingToDocument(String documentId);

	public void recordCommand(String command);
	
	public void finishWriting();

}
