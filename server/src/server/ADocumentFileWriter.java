package server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import org.json.JSONException;
import org.json.JSONObject;

public class ADocumentFileWriter implements DocumentFileWriter {
	private PrintWriter out;
	private String documentId;

	// start -> certain point
	public String getFileFromBeginning(long endTime, String documentId) {
		StringBuilder textData = new StringBuilder();
		File f = new File("docLogs/" + documentId);
		try (BufferedReader br = new BufferedReader(new FileReader(f))) {
			String line;
			while ((line = br.readLine()) != null) {
				JSONObject command = new JSONObject(line);
				if (command.getLong("timeStamp") > endTime) {
					break;
				}
				String type = command.getString("commandType");
				switch (type) {
				case "insert":
					int insertIdx = command.getInt("index");
					String content = command.getString("content");
					if (insertIdx > textData.length()) {

						textData.append(content);
					} else {
						textData.insert(insertIdx, content);
					}
					break;
				case "delete":
					int start = command.getInt("startIndex");
					int end = command.getInt("endIndex");
					try {
						textData.delete(start - 1, end);
					} catch (Exception e) {
						System.out.println(e.getCause().getMessage());
					}
					break;
				case "status":
					break;
				}
			}
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String returnString = "{ \"beginningOfDoc\": \"" + textData.toString() + "\" }";
		return returnString;
	}

	// certain point -> end
	public String getFileToEnd(String documentId) {
		String wholeFile = getWholeFile(documentId);
		String returnString = "{ \"endOfDoc\": \""  + wholeFile.substring(wholeFile.length() - 10, wholeFile.length()) + "\" }";
		return returnString;
	}

	// start -> end
	public String getWholeFile(String documentId) {
		StringBuilder textData = new StringBuilder();
		File f = new File("docLogs/" + documentId);
		try (BufferedReader br = new BufferedReader(new FileReader(f))) {
			String line;
			while ((line = br.readLine()) != null) {
				JSONObject command = new JSONObject(line);
				String type = command.getString("commandType");
				switch (type) {
				case "insert":
					String content = command.getString("content");
					int insertIdx = command.getInt("index");
					if (insertIdx > textData.length()) {

						textData.append(content);
					} else {
						textData.insert(insertIdx, content);
					}
					break;
				case "delete":
					int start = command.getInt("startIndex");
					int end = command.getInt("endIndex");
					try {
						textData.delete(start - 1, end);
					} catch (Exception e) {
						e.printStackTrace();
					}
					break;
				case "status":
					break;
				}
			}
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String returnString = "{ \"wholeDocument\": \"" + textData.toString() + "\" }";
		return returnString;
	}

	public void startWritingToDocument(String documentId) {
		this.documentId = documentId;
		File f = new File("docLogs/" + documentId);
		try {
			out = new PrintWriter(f);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Record insert, delete, and status
	public void recordCommand(String command) {
		try {
			JSONObject newCommand = new JSONObject(command);
			if (newCommand.has("deleteCommands")) {
				String deleteCommand = newCommand.getJSONArray("deleteCommands").get(0).toString();
				if (out == null) {
					startWritingToDocument(documentId);
				}
				if (!deleteCommand.equals(null)) {
					out.println(deleteCommand);
					out.flush();
				}
			}
		} catch (JSONException e) {
			// This is expected, just ignore these commands
		}
		try {
			JSONObject newCommand = new JSONObject(command);
			if (newCommand.has("insertCommands")) {
				String insertCommand = newCommand.getJSONArray("insertCommands").get(0).toString();
				if (out == null) {
					startWritingToDocument(documentId);
				}
				if (!insertCommand.equals(null)) {
					out.println(insertCommand);
					out.flush();
				}
			}
		} catch (JSONException e) {
			// this is expected, just ignore these commands
		}
	}

	public void finishWriting() {
		out.close();
	}

}
