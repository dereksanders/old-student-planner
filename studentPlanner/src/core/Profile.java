package core;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Observable;
import java.util.PriorityQueue;

import javafx.scene.paint.Color;
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
	public GenericLinkedHashTable<Color, Course> courseColors;
	public int showWithinThreshold = 14;
	public ArrayList<PriorityQueue<Meeting>> dayMeetings;
	public GenericLinkedHashTable<LocalDate, CalendarEvent> dateEvents;

	/**
	 * Instantiates a new profile.
	 *
	 * @param name
	 *            the name
	 */
	public Profile(String name) {

		this.name = name;

		/* Initialize ObservableArrayList of all terms in the current profile */
		this.terms = new ArrayList<>();

		/* Initialize GenericHashTable of Colors mapped to Courses */
		this.courseColors = new GenericLinkedHashTable<>(366, false);

		this.dayMeetings = new ArrayList<>(7);
		for (int i = 0; i < 7; i++) {
			this.dayMeetings.add(new PriorityQueue<Meeting>());
		}

		this.dateEvents = new GenericLinkedHashTable<>(366, false);
	}

	public Profile(ProfileSave p) {
		this.name = p.name;
		this.currentlySelectedTerm = p.currentlySelectedTerm;
		this.currentlySelectedDate = p.currentlySelectedDate;
		this.terms = p.terms;
		this.courseColors = p.courseColors;
		this.showWithinThreshold = p.showWithinThreshold;
		this.dayMeetings = p.dayMeetings;
		this.dateEvents = p.dateEvents;
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
		System.out.println("Profile " + this.toString() + " Notifying " + this.countObservers() + " Observers..");
	}
}
