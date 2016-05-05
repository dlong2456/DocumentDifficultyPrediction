package server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.json.JSONException;
import org.json.JSONObject;

public class ADocumentFileWriter implements DocumentFileWriter {
	private PrintWriter out;
	private String documentId;
	private long lastTimeStamp;
	private long firstTimeStamp;
	private boolean firstCommandReceived = false;

	// start -> certain point
	public String getFileFromBeginning(int percentage, String documentId) {
		long endTime = firstTimeStamp + (long) Math.floor((lastTimeStamp - firstTimeStamp) * percentage * .1);
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
						textData.insert(insertIdx - 1, content);
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
		String initialString = textData.toString();
		initialString = replaceSpecialCharacters(initialString);
		if (!initialString.equals(null)) {
			String returnString = "{ \"beginningOfDoc\": \"" + initialString + "\" }";
			return returnString;
		} else {
			return null;
		}
	}

	public String getStatusGivenPercentage(int percentage, String documentId) {
		long endTime = firstTimeStamp + (long) Math.floor((lastTimeStamp - firstTimeStamp) * percentage * .1);
		File f = new File("docLogs/" + documentId);
		try (BufferedReader br = new BufferedReader(new FileReader(f))) {
			String line;
			JSONObject statusCommand = null;
			while ((line = br.readLine()) != null) {
				JSONObject command = new JSONObject(line);
				if (command.getString("commandType").equals("status")) {
					statusCommand = command;
				}
				if (command.getLong("timeStamp") > endTime) {
					if (statusCommand != null) {
						String returnString = "{ statusGivenPercentage: " + statusCommand.getInt("status") + " }";
						statusCommand = null;
						return returnString;
					} else {
						String returnString = "{ statusGivenPercentage: " + -1 + " }";
						return returnString;
					}
				}
			}
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String returnString = "{ statusGivenPercentage: " + -1 + " }";
		return returnString;
	}

	// substring
	public String getSubstringFromIndex(String documentId, int index, int substringSize) {
		String returnString = null;
		try {
			String wholeFile = wholeDocumentNoJSON(documentId);
			if ((wholeFile.length() >= index) && index >= substringSize) {
				String initialString = wholeFile.substring(index - substringSize, index);
				initialString = replaceSpecialCharacters(initialString);
				returnString = "{ substring: \"" + initialString + "\" , documentId: " + "\"" + documentId + "\" }";
			} else if (index < substringSize) {
				// get everything back to the beginning of the file
				String firstString = wholeFile.substring(0, index);
				int end;
				if ((substringSize > wholeFile.length()) || (substringSize < firstString.length())) {
					end = wholeFile.length();
				} else {
					end = substringSize;
				}
				String secondString = wholeFile.substring(index, end);
				String initialString = firstString + secondString;
				initialString = replaceSpecialCharacters(initialString);
				returnString = "{ \"substring\": \"" + initialString + "\" , documentId: " + "\"" + documentId + "\" }";
			}
			System.out.println("SUBSTRING" + returnString);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnString;
	}

	// whole document
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
						textData.insert(insertIdx - 1, content);
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
		String string = textData.toString();
		if (!string.equals(null)) {
			string = replaceSpecialCharacters(string);
			String returnString = "{ \"wholeDocument\": \"" + string + "\" }";
			return returnString;
		} else {
			return null;
		}
	}

	private String wholeDocumentNoJSON(String documentId) {
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
						textData.insert(insertIdx - 1, content);
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
		String initialString = textData.toString();
		return initialString;
	}

	public void startWritingToDocument(String documentId) {
		this.documentId = documentId;
		File f = new File("docLogs/" + documentId);
		try {
			out = new PrintWriter(new FileWriter(f, true));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Record insert, delete, and status
	public void recordCommand(String command) {
		try {
			JSONObject newCommand = new JSONObject(command);
			if (newCommand.has("deleteCommands")) {
				JSONObject deleteCommandObject = (JSONObject) newCommand.getJSONArray("deleteCommands").get(0);
				lastTimeStamp = deleteCommandObject.getLong("timeStamp");
				if (firstCommandReceived == false) {
					firstTimeStamp = deleteCommandObject.getLong("timeStamp");
				}
				firstCommandReceived = true;
				String deleteCommand = deleteCommandObject.toString();
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
				JSONObject insertCommandObject = (JSONObject) newCommand.getJSONArray("insertCommands").get(0);
				String insertCommand = insertCommandObject.toString();
				if (insertCommandObject.getLong("timeStamp") > lastTimeStamp) {
					lastTimeStamp = insertCommandObject.getLong("timeStamp");
				}
				if ((insertCommandObject.getLong("timeStamp") < firstTimeStamp) || (firstCommandReceived == false)) {
					firstTimeStamp = insertCommandObject.getLong("timeStamp");
				}
				firstCommandReceived = true;
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
		try {
			JSONObject newCommand = new JSONObject(command);
			if (newCommand.has("commandType")) {
				if (newCommand.getString("commandType").equals("status")) {
					String statusCommand = newCommand.toString();
					if (out == null) {
						startWritingToDocument(documentId);
					}
					if (!statusCommand.equals(null)) {
						out.println(statusCommand);
						out.flush();
					}
				}
			}
		} catch (JSONException e) {
			// This is expected, just ignore these commands
		}
	}

	public void finishWriting() {
		out.close();
	}

	private String replaceSpecialCharacters(String initialString) {
		initialString = initialString.replace("\n", "\\n");
		initialString = initialString.replace("\"", "\\\"");
		return initialString;
	}

}
