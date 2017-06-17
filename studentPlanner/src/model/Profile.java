package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Observable;
import core.ProfileSave;
import utility.GenericLinkedHashTable;
import utility.JSONParser;

/**
 * The Class Profile.
 */
public class Profile extends Observable {

	public String name;
	public Term currentlySelectedTerm;

	/* Gets initialized in Driver#start to current date. */
	public LocalDate currentlySelectedDate;

	public ArrayList<Term> terms;
	public int showWithinThreshold = 14;
	public GenericLinkedHashTable<LocalDate, CalendarEvent> dateEvents;
	public ArrayList<CalendarEvent> personalEvents;

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
		this.personalEvents = new ArrayList<>();
	}

	public Profile(ProfileSave p) {
		this.name = p.name;
		this.currentlySelectedTerm = p.currentlySelectedTerm;
		this.currentlySelectedDate = p.currentlySelectedDate;
		this.terms = p.terms;
		this.showWithinThreshold = p.showWithinThreshold;
		this.dateEvents = p.dateEvents;
		this.personalEvents = p.personalEvents;
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
		JSONParser.saveProfile(this);
		setChanged();
		notifyObservers();
	}

	@Override
	public String toString() {
		return this.name;
	}
}