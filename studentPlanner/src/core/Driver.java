package core;

import java.io.File;

import model.Profile;
import planner.Planner;
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
		}

		return active;
	}
}