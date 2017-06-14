package core;

import java.time.LocalDate;
import java.util.ArrayList;
import model.CalendarEvent;
import model.Profile;
import model.Term;
import utility.GenericLinkedHashTable;

public class ProfileSave {

	public String name;
	public Term currentlySelectedTerm;

	/* Gets initialized in Driver#start to current date. */
	public LocalDate currentlySelectedDate;

	public ArrayList<Term> terms;
	public int showWithinThreshold = 14;
	public GenericLinkedHashTable<LocalDate, CalendarEvent> dateEvents;

	public ProfileSave(Profile p) {
		this.name = p.name;
		this.currentlySelectedTerm = p.currentlySelectedTerm;
		this.currentlySelectedDate = p.currentlySelectedDate;
		this.terms = p.terms;
		this.showWithinThreshold = p.showWithinThreshold;
		this.dateEvents = p.dateEvents;
	}
}
