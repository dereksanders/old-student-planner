package json;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import com.google.gson.Gson;

import core.Course;
import core.Deliverable;
import core.Planner;
import core.Term;
import courseSchedule.CourseSchedule;
import termCalendar.TermCalendar;
import utility.IOManager;

public class Parser {

	private static Gson gson = new Gson();

	// Loads all courses in save directory
	public static void loadAll(String profileName) {

		File[] saveFiles = finder(Planner.saveDir + "//" + profileName);

		System.out.println(saveFiles.length + " save files found.");

		for (File f : saveFiles) {

			BufferedReader br = null;

			try {
				br = new BufferedReader(new FileReader(f.getPath()));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

			Course c = gson.fromJson(br, Course.class);

			for (Term t : c.terms) {
				if (!Planner.terms.contains(t)) {
					CourseSchedule.termAddStuff(t);
				}
			}
			CourseSchedule.courseAddStuff(c);

			for (Deliverable d : c.deliverables) {
				Planner.dateEvents.put(d.due.toLocalDate(), d);
			}
			TermCalendar.redrawCalendars();
		}
	}

	public static void saveJSONObject(String profileName, Course c) {

		// Create save directory if it does not already exist.
		try {
			Files.createDirectory(Paths.get(Planner.saveDir + "//" + profileName));
		} catch (IOException e) {

		}

		IOManager.writeFile(gson.toJson(c),
				Planner.saveDir + "//" + profileName + "//" + c.departmentID + c.code + ".json");
	}

	public static File[] finder(String dirName) {
		File dir = new File(dirName);

		return dir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String filename) {
				return filename.endsWith(".json");
			}
		});
	}
}
