package server;

import java.util.ArrayList;
import java.util.List;

public class DocumentWriterManager {
	
	private static final DocumentWriterManager INSTANCE = new DocumentWriterManager();
	private List<DocumentFileWriter> documentWriters = new ArrayList<DocumentFileWriter>();
	private List<String> documentIds = new ArrayList<String>();

	public static DocumentWriterManager getInstance() {
		return INSTANCE;
	}


	public void join(DocumentFileWriter fileWriter, String docId) {
		documentWriters.add(fileWriter);
		documentIds.add(docId);
	}

	public void part(DocumentFileWriter fileWriter) {
		int index = documentWriters.indexOf(fileWriter);
		documentWriters.remove(fileWriter);
		documentIds.remove(index);
	}

	public DocumentFileWriter getWriterById(String docId) {
		DocumentFileWriter fw = null;
		try {
		int index = documentIds.indexOf(docId);
		fw = documentWriters.get(index);
		} catch (ArrayIndexOutOfBoundsException e) {
			//Let fw return as null. Web socket will handle the rest. 
		}
		return fw;
	}

}
