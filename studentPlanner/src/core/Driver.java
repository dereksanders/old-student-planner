package core;

import java.io.File;
import java.time.LocalDateTime;
import java.time.LocalTime;

import model.Profile;
import planner.Planner;
import utility.IOManager;

/**
 * The Class Planner.
 */
public class Driver {

	public TaskScheduler clock;
	public Planner planner;
	protected Profile active;
	protected ProfileController pc;

	public Driver() {

		this.active = initProfile();
		this.pc = new ProfileController(this.active);
		this.clock = TaskScheduler.getInstance(LocalDateTime.of(this.active.lastQuit, LocalTime.of(0, 0)), this.pc);
		this.planner = Planner.getInstance(this.active, this.pc);
	}

	private Profile initProfile() {

		Profile active = null;
		/*
		 * Find the last modified file. If it does not exist, returns null.
		 */
		File lastModified = IOManager.getLastModifiedFile(Main.saveDir, Main.saveExtension);

		/* If no profiles exist, create a new one. */
		if (lastModified == null) {

			System.out.println("Creating new profile.");
			active = new Profile("default");
			active.save(Main.saveDir, Main.backupDir);

		} else {

			System.out.println("Loading existing profile.");
			active = (Profile) IOManager.loadObject(lastModified);
		}

		return active;
	}
}