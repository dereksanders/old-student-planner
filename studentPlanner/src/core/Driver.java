package core;

import java.io.File;

import javafx.scene.paint.Color;
import model.Course;
import model.Profile;
import model.Term;
import planner.Planner;
import utility.GenericHashTable;
import utility.GenericLinkedHashTable;
import utility.IOManager;
import utility.JSONParser;

/**
 * The Class Planner.
 */
public class Driver {

	/* Static Fields */
	public static String saveDir = "res";;
	public static String backupDir = "res//backup";
	public static TimeDateThread t = new TimeDateThread();

	protected Planner planner;
	public Profile active;
	protected ProfileController pc;

	public Driver() {
		this.active = initProfile();
		this.planner = Planner.getInstance(this.active);
		this.pc = new ProfileController(this.active, this.planner);
		this.planner.pc = this.pc;
	}

	private Profile initProfile() {

		Profile active = null;
		/*
		 * Find the last modified .json file. If it does not exist, returns
		 * null.
		 */
		File lastModified = IOManager.lastModifiedJSON(saveDir);

		/* If no profiles exist, create a new one. */
		if (lastModified == null) {

			System.out.println("Creating new profile.");
			active = new Profile("default");
			JSONParser.saveProfile(active);

		} else {

			System.out.println("Loading existing profile.");
			active = JSONParser.loadProfile(lastModified);

			active.dateEvents = new GenericLinkedHashTable<>(300, false);

			/* Make correction for issue #15. */
			for (Term t : active.terms) {
				t.courseColors = new GenericHashTable<>(100);
				for (Course c : t.courses) {
					t.courseColors.put(Color.web(c.colour), c);
				}
			}
		}

		return active;
	}
}