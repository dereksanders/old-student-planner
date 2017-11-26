package core;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;

import utility.IOManager;

public class Logger {

	private String filePath;

	public Logger(String filePath) {

		this.filePath = filePath;

		// Create logging file.
		if (!Files.exists(Paths.get(this.filePath))) {
			IOManager.writeFile("", this.filePath);
		}
	}

	public void post(String funcName, String action) {

		String log = LocalDateTime.now() + "\n" + funcName + "\n" + action + "\n\n";

		IOManager.appendToFile(log, filePath);
	}
}
