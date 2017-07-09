package model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Observable;

import core.Driver;
import utility.GenericLinkedHashTable;
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

	/* Gets initialized in Driver#start to current date. */
	public LocalDate currentlySelectedDate;

	public ArrayList<Term> terms;
	public int showWithinThreshold = 14;
	public GenericLinkedHashTable<LocalDate, CalendarEvent> dateEvents;

	/**
	 * Instantiates a new profile.
	 *
	 * @param name
	 *            the name
	 */
	public Profile(String name) {

		this.name = name;
		this.terms = new ArrayList<>();
		this.dateEvents = new GenericLinkedHashTable<>(300, true);
	}

	public boolean coursesExist() {
		boolean coursesExist = false;
		for (Term t : this.terms) {
			if (t.courses.size() != 0) {
				coursesExist = true;
			}
		}
		return coursesExist;
	}

	public void update() {
		save();
		setChanged();
		notifyObservers();
	}

	public void save() {
		IOManager.saveObject(this, Driver.saveDir);
		IOManager.saveObject(this, Driver.backupDir);
	}

	@Override
	public String toString() {
		return this.name;
	}
}