package utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * The Class IOManager.
 *
 * @author Derek Sanders (Student no. 10021587)
 * @version 1.1
 * @since November 29th, 2015
 */

public class IOManager {

	/** The max rows. */
	private static int MAX_ROWS = 200000;

	/**
	 * Write file.
	 *
	 * @param text
	 *            the text
	 * @param filename
	 *            the filename
	 */
	public static void writeFile(String text, String filename) {

		try {

			Files.write(Paths.get("./" + filename), text.getBytes());

		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	/**
	 * Load file.
	 *
	 * @param filename
	 *            the filename
	 * @return the array of keys
	 */
	public static String[] loadFile(String filename) {

		String line;
		int lineCount = 0;
		String[] textArray = new String[MAX_ROWS];

		Charset charset = Charset.forName("UTF-8");
		Path file = Paths.get(filename);

		try (BufferedReader reader = Files.newBufferedReader(file, charset)) {

			do {

				line = reader.readLine();

				if (line != null) {

					textArray[lineCount] = line;
					lineCount++;
				}

			} while (line != null);

		} catch (IOException err) {

			err.printStackTrace();
			return null;
		}

		String[] rawData = new String[lineCount];

		int i = 0;

		while (i < lineCount) {

			rawData[i] = textArray[i];
			i++;
		}

		return rawData;
	}

	// TODO
	// java.nio.file.FileSystemException: res\default\MATH110.json: The process
	// cannot access the file because it is being used by another process.
	public static void deleteFile(String filename) {

		Path file = Paths.get(filename);

		try {
			Files.delete(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}