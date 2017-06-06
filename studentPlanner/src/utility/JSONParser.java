package utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import com.google.gson.Gson;
import core.Driver;
import core.ProfileSave;
import model.Profile;

/**
 * The Class JSONParser. This class loads and saves Profiles as .json objects.
 */
public class JSONParser {

	private static Gson gson = new Gson();

	/**
	 * Loads .json profile.
	 *
	 * @param saveFile
	 *            the save
	 * @return the profile
	 */
	// Loads all courses in save directory
	public static Profile loadProfile(File saveFile) {

		BufferedReader br = null;

		try {
			br = new BufferedReader(new FileReader(saveFile.getPath()));
		} catch (FileNotFoundException e) {
			System.out.println("save file is null.");
			e.printStackTrace();
		}

		ProfileSave save = gson.fromJson(br, ProfileSave.class);
		return new Profile(save);
	}

	public static void saveProfile(Profile profile) {

		saveProfile(profile, Driver.saveDir);
		saveProfile(profile, Driver.backupDir);
	}

	private static void saveProfile(Profile profile, String directory) {

		// Create save directory if it does not already exist.
		if (!Files.exists(Paths.get(directory))) {
			try {
				Files.createDirectory(Paths.get(directory));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		ProfileSave save = new ProfileSave(profile);
		IOManager.writeFile(gson.toJson(save), directory + "//" + profile.name + ".json");
	}
}
