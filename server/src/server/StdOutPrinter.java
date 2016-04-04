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
			String s = null;
			try {
				// Read stdIn (so we can see the output of a forked process)
				while ((s = stdInput.readLine()) != null) {
					System.out.println(s);
				}
				// Read stdError (so we can see the errors of a forked process)
				while ((s = stdError.readLine()) != null) {
					System.out.println("ERROR:" + s);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

}
