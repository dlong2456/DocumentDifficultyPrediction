package client;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.json.JSONObject;

import analyzer.ui.APredictionController;
import commands.DocumentStatusCorrection;
import config.FactorySingletonInitializer;
import difficultyPrediction.APredictionParameters;
import difficultyPrediction.DifficultyPredictionSettings;
import difficultyPrediction.DifficultyRobot;
import difficultyPrediction.metrics.CommandClassificationSchemeName;
import edu.cmu.scs.fluorite.commands.DifficulyStatusCommand.Status;
import edu.cmu.scs.fluorite.commands.ICommand;
import edu.cmu.scs.fluorite.model.EventRecorder;

public class ADocumentPredictionManager implements DocumentPredictionManager {
	private static int currentStatus; // 0 for making progress, 1 for facing
										// difficulty
	private static Socket client;
	private long documentId;

	public static void main(String[] args) {
		System.out.println("Client created");
		DocumentPredictionManager predictionManager = new ADocumentPredictionManager();
		// The port number is passed as an argument to main when the process is run
		int port = Integer.parseInt(args[0]);
		try {
			client = new Socket("localhost", port);
			System.out.println("Client connected to " + client.getRemoteSocketAddress());
			Thread messageReceiver = new AClientMessageReceiver(client, predictionManager);
			messageReceiver.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public ADocumentPredictionManager() {
		APredictionParameters.getInstance().setCommandClassificationScheme(CommandClassificationSchemeName.A5);
		APredictionParameters.getInstance().setStartupLag(0);
		APredictionParameters.getInstance().setSegmentLength(5);
		DifficultyPredictionSettings.setReplayMode(true);
		FactorySingletonInitializer.configure();
		EventRecorder.getInstance().initCommands();
		DifficultyRobot.getInstance().addStatusListener(this);
		APredictionController.createUI();
	}

	public void processEvent(ICommand event) {
		EventRecorder.getInstance().recordCommand(event);
	}

	@Override
	public void newStatus(String aStatus) {
		// TODO Auto-generated method stub
	}

	@Override
	public void newAggregatedStatus(String aStatus) {
		// TODO Auto-generated method stub
	}

	@Override
	public void newStatus(int aStatus) {
		currentStatus = aStatus;
		System.out.println("NEW STATUS: " + currentStatus);
		// If student is struggling, then send an email to notify the
		// teacher.
		if (aStatus != currentStatus && currentStatus == 1) {
			sendEmail();
		}
		// Send the prediction to be displayed to the student.
		sendMessageToServer("{ status: '" + currentStatus + "'}");
	}

	public void handleStatusUpdate(JSONObject obj) {
		int newStatus = obj.getInt("facingDifficulty");
		if (newStatus != currentStatus) {
			currentStatus = newStatus;
			Status status;
			if (currentStatus == 0) {
				status = Status.Making_Progress;
			} else {
				status = Status.Insurmountable;
			}
			ICommand statusCommand = new DocumentStatusCorrection(status);
			// If student is struggling
			if (newStatus == 1) {
				// And log extra details about the status update
				((DocumentStatusCorrection) statusCommand).setDetails(obj.getString("details"));
				((DocumentStatusCorrection) statusCommand).setType(obj.getString("difficultyType"));
				// Send email to notify the teacher
				sendEmail();
			}
			// Send command to EventRecorder
			processEvent(statusCommand);
		}
	}

	// Listens to status events. When a status is received, this updates the
	// currentStatus and sends the status to the server
	@Override
	public void newAggregatedStatus(int aStatus) {
//		currentStatus = aStatus;
//		System.out.println("NEW AGGREGATED STATUS: " + currentStatus);
//		// If student is struggling, then send an email to notify the
//		// teacher.
//		if (aStatus != currentStatus && currentStatus == 1) {
//			sendEmail();
//		}
//		// Send the prediction to be displayed to the student.
//		sendMessageToServer("{ status: '" + currentStatus + "'}");
	}

	@Override
	public void newManualStatus(String aStatus) {
		// TODO Auto-generated method stub

	}

	public long getDocumentId() {
		return documentId;
	}

	public void setDocumentId(long newId) {
		System.out.println("Setting document Id: " + newId);
		documentId = newId;
	}

	public int getStatus() {
		return currentStatus;
	}

	public void setStatus(int newStatus) {
		currentStatus = newStatus;
	}

	@Override
	public void newReplayedStatus(int aStatus) {
		// TODO Auto-generated method stub

	}

	private void sendEmail() {
		// Recipient's email ID needs to be mentioned.
		String to = "documenthelper1@gmail.com";

		// Sender's email ID needs to be mentioned
		String from = "documenthelper1@gmail.com";

		// Get system properties
		Properties properties = System.getProperties();
		properties.put("mail.smtp.auth", "true");
		properties.put("mail.smtp.starttls.enable", "true");
		properties.put("mail.smtp.host", "smtp.gmail.com");
		properties.put("mail.smtp.port", "587");

		Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("documenthelper1@gmail.com", "");
			}
		});

		try {
			// Create a default MimeMessage object.
			MimeMessage message = new MimeMessage(session);

			// Set From: header field of the header.
			message.setFrom(new InternetAddress(from));

			// Set To: header field of the header.
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

			// Set Subject: header field
			message.setSubject("Student Difficulty Notification");

			// Now set the actual message
			message.setText("A student is facing difficulty! Visit document " + documentId + " to help them out!");

			// Send message
			Transport.send(message);
		} catch (MessagingException mex) {
			mex.printStackTrace();
		}

	}

	public void sendMessageToServer(String message) {
		OutputStream outToServer;
		try {
			outToServer = client.getOutputStream();
			DataOutputStream out = new DataOutputStream(outToServer);
			out.writeUTF(message);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
