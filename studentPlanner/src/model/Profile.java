package model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Observable;

import core.Driver;
import core.Main;
import utility.IOManager;

/**
 * The Class Profile.
 */
public class Profile extends Observable implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String name;
	public Term currentlySelectedTerm;

	// TODO
	public Term termInProgress;

	/* Gets initialized in Driver#start to current date. */
	public LocalDate currentlySelectedDate;

	public ArrayList<Term> terms;
	public int showWithinThreshold = 14;

	// Recently used colors for non-Course meetings.
	public ArrayList<String> recentlyUsedColors;

	public LocalDate lastQuit;

	/**
	 * Instantiates a new profile.
	 *
	 * @param name
	 *            the name
	 */
	public Profile(String name) {

		this.name = name;
		this.terms = new ArrayList<>();
		this.recentlyUsedColors = new ArrayList<>();
	}

	/**
	 * Tests if courses exist in this profile.
	 *
	 * @return true if courses exist
	 */
	public boolean coursesExist() {

		boolean coursesExist = false;

		for (Term t : this.terms) {
			if (t.courses.size() != 0) {
				coursesExist = true;
			}
		}

		return coursesExist;
	}

	/**
	 * Operations performed whenever this profile changes.
	 */
	public void update() {
		save();
		setChanged();
		notifyObservers();
	}

	/**
	 * Saves this profile.
	 */
	public void save() {
		IOManager.saveObject(this, Main.driver.saveDir, Driver.saveExtension);
		IOManager.saveObject(this, Main.driver.backupDir, Driver.saveExtension);
	}

	public void save(String saveDir, String backupDir) {
		IOManager.saveObject(this, saveDir, Driver.saveExtension);
		IOManager.saveObject(this, backupDir, Driver.saveExtension);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.name;
	}
}