package server;

import java.io.BufferedReader;
import java.io.IOException;

public class StdOutPrinter extends Thread {

	BufferedReader stdInput;
	BufferedReader stdError;

	public StdOutPrinter(BufferedReader stdInput, BufferedReader stdError) {
		this.stdInput = stdInput;
		this.stdError = stdError;
	}

	public void run() {
		while (true) {
			// Read the output from proc2
			String s = null;
			try {
				while ((s = stdInput.readLine()) != null) {
					System.out.println(s);
				}
				// Read any errors from proc2
				while ((s = stdError.readLine()) != null) {
					System.out.println("ERROR:" + s);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

}
