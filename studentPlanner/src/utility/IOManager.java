package utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
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

	public static void createDirectory(String directory) {
		try {
			Files.createDirectory(Paths.get(directory));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void removeDirectory(String directory) {
		try {
			Files.delete(Paths.get(directory));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Delete file.
	 *
	 * @param filename
	 *            the filename
	 */
	public static void deleteFile(String filename) {

		Path file = Paths.get(filename);

		try {
			Files.delete(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Finds the last modified .json file in the directory.
	 *
	 * @param directory
	 *            the directory
	 * @return the file
	 */
	public static File lastModifiedJSON(String directory) {

		File[] profiles = findJSONFiles(directory);

		if (profiles.length == 0) {

			System.out.println("No profiles found. Returning null.");
			return null;

		} else {

			File lastModified = profiles[0];

			if (profiles.length > 1) {
				for (int i = 1; i < profiles.length; i++) {
					if (profiles[i].lastModified() < lastModified.lastModified()) {

						lastModified = profiles[i];
					}
				}
			}
			return lastModified;
		}
	}

	/**
	 * Finds all .json files in the directory.
	 *
	 * @param directory
	 *            the directory name
	 * @return the file[]
	 */
	public static File[] findJSONFiles(String directory) {
		File dir = new File(directory);

		return dir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String filename) {
				return filename.endsWith(".json");
			}
		});
	}
}