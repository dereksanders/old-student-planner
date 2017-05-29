package core;

import java.time.LocalDate;
import java.util.ArrayList;
import javafx.scene.paint.Color;
import utility.GenericEntry;
import utility.GenericHashTable;
import utility.GenericLinkedHashTable;
import utility.JSONParser;

/**
 * The Class Profile.
 */
public class Profile {

	public String name;
	public Term currentlySelectedTerm;
	public LocalDate currentlySelectedDate;
	public ArrayList<Term> terms;
	public GenericHashTable<Color, Course> courseColors;
	public int showWithinThreshold = 14;
	public GenericLinkedHashTable<Course, Term> courseTerms;
	public GenericLinkedHashTable<String, Meeting> dayMeetings;
	public GenericLinkedHashTable<LocalDate, CalendarEvent> dateEvents;

	/**
	 * Instantiates a new profile.
	 *
	 * @param name
	 *            the name
	 */
	public Profile(String name) {

		this.name = name;
		this.currentlySelectedDate = Planner.t.current.toLocalDate();

		/* Initialize ObservableArrayList of all terms in the current profile */
		this.terms = new ArrayList<>();

		/* Initialize GenericHashTable of Colors mapped to Courses */
		this.courseColors = new GenericHashTable<>(366);

		/*
		 * Initialize GenericLinkedHashTable of day Strings (e.g. Monday,
		 * Tuesday, etc.) to PriorityQueues of Meetings that occur weekly on
		 * that day.
		 */
		this.courseTerms = new GenericLinkedHashTable<>(366, false);
		this.dayMeetings = new GenericLinkedHashTable<>(7, false);
		this.dateEvents = new GenericLinkedHashTable<>(366, false);
	}

	/**
	 * Reset term params of all terms between the start and end dates.
	 *
	 * @param start
	 *            the start
	 * @param end
	 *            the end
	 */
	public void resetTermParams(LocalDate start, LocalDate end) {
		for (int i = 0; i < this.terms.size(); i++) {
			if (this.terms.get(i).start.isEqual(start) || this.terms.get(i).start.isAfter(start)) {
				if (this.terms.get(i).end.isEqual(end) || this.terms.get(i).end.isBefore(end)) {

					this.terms.get(i).resetParams();
				}
			}
		}
	}

	/**
	 * Adds the term.
	 *
	 * @param addedTerm
	 *            the added term
	 */
	public void addTerm(Term addedTerm) {

		this.terms.add(addedTerm);

		/*
		 * Sort terms chronologically. TODO: Is the sorting ascending or
		 * descending?
		 */
		this.terms.sort(null);

		JSONParser.saveProfile(this);
	}

	/**
	 * Perform necessary actions when adding a course to the Profile.
	 * 
	 * 1) Update courseTerms
	 * 
	 * 2) Update dayMeetings
	 * 
	 * 3) Update Term params
	 * 
	 * 4) Update courseColors
	 *
	 * @param addedCourse
	 *            the added course
	 */
	public void addCourse(Course addedCourse) {
		updateCourseTerms(addedCourse);
		for (Meeting m : addedCourse.meetings) {
			dayMeetings.put(m.dayOfWeek, m);
			currentlySelectedTerm.updateParams(m);
		}
		this.courseColors.put(new GenericEntry<Color, Course>(Color.web(addedCourse.colour), addedCourse));
		JSONParser.saveProfile(this);
	}

	private void updateCourseTerms(Course course) {
		for (Term t : this.terms) {
			if (t.start.isEqual(course.start) || t.start.isAfter(course.start)) {
				if (t.end.isEqual(course.end) || t.end.isBefore(course.end)) {
					t.courses.add(course);
					this.courseTerms.put(course, t);
				}
			}
		}
	}

	public void deleteCourse(Course deletedCourse) {
		for (Term t : this.terms) {
			if (t.start.isEqual(deletedCourse.start) || t.start.isAfter(deletedCourse.start)) {
				if (t.end.isEqual(deletedCourse.end) || t.end.isBefore(deletedCourse.end)) {
					t.courses.remove(deletedCourse);
					this.courseTerms.del(deletedCourse, t);
				}
			}
		}
		JSONParser.saveProfile(this);
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
}
