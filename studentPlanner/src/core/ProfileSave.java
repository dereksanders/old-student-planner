package core;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.PriorityQueue;

import javafx.scene.paint.Color;
import model.CalendarEvent;
import model.Course;
import model.Meeting;
import model.Profile;
import model.Term;
import utility.GenericHashTable;
import utility.GenericLinkedHashTable;

public class ProfileSave {

	public String name;
	public Term currentlySelectedTerm;

	/* Gets initialized in Driver#start to current date. */
	public LocalDate currentlySelectedDate;

	public ArrayList<Term> terms;
	public GenericHashTable<Color, Course> courseColors;
	public int showWithinThreshold = 14;
	public ArrayList<PriorityQueue<Meeting>> dayMeetings;
	public GenericLinkedHashTable<LocalDate, CalendarEvent> dateEvents;

	public ProfileSave(Profile p) {
		this.name = p.name;
		this.currentlySelectedTerm = p.currentlySelectedTerm;
		this.currentlySelectedDate = p.currentlySelectedDate;
		this.terms = p.terms;
		this.courseColors = p.courseColors;
		this.showWithinThreshold = p.showWithinThreshold;
		this.dayMeetings = p.dayMeetings;
		this.dateEvents = p.dateEvents;
	}
}
