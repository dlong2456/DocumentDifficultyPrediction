package predictions;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.json.JSONObject;

import config.FactorySingletonInitializer;
import difficultyPrediction.APredictionParameters;
import difficultyPrediction.DifficultyPredictionSettings;
import difficultyPrediction.DifficultyRobot;
import difficultyPrediction.metrics.CommandClassificationSchemeName;
import edu.cmu.scs.fluorite.commands.DifficulyStatusCommand;
import edu.cmu.scs.fluorite.commands.DifficulyStatusCommand.Status;
import edu.cmu.scs.fluorite.commands.ICommand;
import edu.cmu.scs.fluorite.model.EventRecorder;
import socket.MyWebSocket;

public class ADocumentPredictionManager implements DocumentPredictionManager {
	private boolean enabled = true;
	private MyWebSocket webSocketHandler;
	private static int currentStatus; // 1 for making progress, 0 for facing
										// difficulty

	public ADocumentPredictionManager(MyWebSocket newWebSocketHandler) {
		webSocketHandler = newWebSocketHandler;
		APredictionParameters.getInstance().setCommandClassificationScheme(CommandClassificationSchemeName.A5);
		DifficultyPredictionSettings.setReplayMode(true);
		FactorySingletonInitializer.configure();
		EventRecorder.getInstance().initCommands();
		DifficultyRobot.getInstance().addStatusListener(this);
		DifficultyPredictionSettings.setSegmentLength(10);
	}

	public void processEvent(ICommand event) {
		//Only process events if there is currently a websocket session running for this document
		if (enabled) {
			EventRecorder.getInstance().recordCommand(event);
		}
	}

	@Override
	public void newStatus(String aStatus) {
		// TODO Auto-generated method stub
	}

	@Override
	public void newAggregatedStatus(String aStatus) {
		// TODO Auto-generated method stub
	}

	public void setWebSocketHandler(MyWebSocket newWebSocketHandler) {
		webSocketHandler = newWebSocketHandler;
	}

	public void disable() {
		enabled = false;
	}

	public void enable() {
		enabled = true;
	}

	// Listens to status events. When a status is received, this updates the
	// currentStatus and sends the status to the client
	@Override
	public void newStatus(int aStatus) {
		// Only make status updates if there is currently a web socket session
		// running for this document
		if (enabled) {
			currentStatus = aStatus;
			// If student is struggling, then send an email to notify the
			// teacher.
			if (aStatus != currentStatus && currentStatus == 0) {
				sendEmail();
			}
			// Send the prediction to be displayed to the student.
			try {
				webSocketHandler.sendMessage("{ status: '" + currentStatus + "'}");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void handleStatusUpdate(JSONObject obj) {
		int newStatus = obj.getInt("makingProgress");
		if (newStatus != currentStatus) {
			currentStatus = newStatus;
			Status status;
			if (currentStatus == 0) {
				status = Status.Insurmountable;
			} else {
				status = Status.Making_Progress;
			}
			ICommand statusCommand = new DifficulyStatusCommand(status);
			processEvent(statusCommand);
			// If student is struggling, then send an email to notify the
			// teacher.
			if (currentStatus == 0) {
				sendEmail();
			}
		}
	}

	@Override
	public void newAggregatedStatus(int aStatus) {
		// TODO Auto-generated method stub

	}

	@Override
	public void newManualStatus(String aStatus) {
		// TODO Auto-generated method stub

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
			message.setText("A student is facing difficulty! Click here to help them out: ");

			// Send message
			Transport.send(message);
		} catch (MessagingException mex) {
			mex.printStackTrace();
		}

	}

}
