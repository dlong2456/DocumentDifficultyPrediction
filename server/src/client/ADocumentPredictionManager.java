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

import org.json.JSONException;
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
		// Create a new PredictionManager
		DocumentPredictionManager predictionManager = new ADocumentPredictionManager();
		// The port number is passed as an argument to main when the process is
		// run
		int port = Integer.parseInt(args[0]);
		try {
			// localhost when running in Eclipse, classroom.cs.unc.edu or
			// 152.2.130.35 when running on CS server
//			client = new Socket("152.2.130.35", port);
			//client = new Socket("152.2.130.35", port);
			 client = new Socket("localhost", port);
			System.out.println("Client connected to " + client.getRemoteSocketAddress());
			// Create a new client message receiver to receive messages from
			// server
			Thread messageReceiver = new AClientMessageReceiver(client, predictionManager);
			messageReceiver.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public ADocumentPredictionManager() {
		// Set prediction parameters within EclipseHelper
		// CommandClassificationSchemeName.A4 is also designed to work with
		// documents
		APredictionParameters.getInstance().setCommandClassificationScheme(CommandClassificationSchemeName.A5);
		// Set startup lag and segment length - they are short because the user
		// study we originally tested this on was only a two page paper
		APredictionParameters.getInstance().setStartupLag(5);
		APredictionParameters.getInstance().setSegmentLength(10);
		// TODO: What does replay mode do again?
		DifficultyPredictionSettings.setReplayMode(true);
		FactorySingletonInitializer.configure();
		// Start EventRecorder
		EventRecorder.getInstance().initCommands();
		// Add ourselves as a status listener so we can receive predictions
		DifficultyRobot.getInstance().addStatusListener(this);
		// Enable this when debugging or analyzing on a local server - we can
		// visualize the commands and predictions being made
		// Disable this when running on CS server because it causes the program
		// to fail
		// APredictionController.createUI();
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

	// Right now, we are listening to statuses rather than aggregated statuses
	// since difficulties are hard to trigger in a 2 page document
	// This method listens to automatic status predictions from EclipseHelper
	@Override
	public void newStatus(int aStatus) {
		// If student is struggling, then send an email to notify the
		// teacher.
		if (aStatus != currentStatus && aStatus == 1) {
			sendEmail();
		}
		currentStatus = aStatus;
		System.out.println("NEW STATUS: " + currentStatus);
		// Send the prediction to be displayed to the student.
		sendMessageToServer("{ status: '" + currentStatus + "'}");
	}

	// This method handles when a user corrects their status manually
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
				try {
					// If the student has entered details and difficulty type,
					// then add these to the command object
					((DocumentStatusCorrection) statusCommand).setDetails(obj.getString("details"));
					((DocumentStatusCorrection) statusCommand).setType(obj.getString("difficultyType"));
					// Send email to notify the teacher
					sendEmail();
				} catch (JSONException e) {
					// This exception will be thrown if the student has not
					// entered details and difficulty type.
					// We still want to send an email to notify the teacher
					sendEmail();
				}
			}
			// Send command to EventRecorder
			processEvent(statusCommand);
		}
	}

	// Listens to aggregate status events. Not using this at the moment - maybe
	// for longer papers.
	@Override
	public void newAggregatedStatus(int aStatus) {
		// TODO Auto-generated method stub
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
		sendMessageToServer("{ documentId: " + documentId + " , status: '" + currentStatus + "'}");
		// Recipient's email ID needs to be specified
		String to = "documenthelper1@gmail.com";

		// Sender's email ID needs to be specified
		String from = "documenthelper1@gmail.com";

		// Get system properties
		Properties properties = System.getProperties();
		properties.put("mail.smtp.auth", "true");
		properties.put("mail.smtp.starttls.enable", "true");
		properties.put("mail.smtp.host", "smtp.gmail.com");
		properties.put("mail.smtp.port", "587"); // 587 is the gmail port number

		// This only works for gmail accounts without two way authentication
		// enabled
		Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("documenthelper1@gmail.com", "MixedFocus");
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

	// This method sends a message to the server (which will then send it to the
	// web client)
	public void sendMessageToServer(String message) {
		OutputStream outToServer;
		try {
			outToServer = client.getOutputStream();
			DataOutputStream out = new DataOutputStream(outToServer);
			out.writeUTF(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
