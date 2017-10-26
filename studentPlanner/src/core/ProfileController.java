package core;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.PriorityQueue;

import courseSchedule.HandleConflict;
import courseSchedule.PromptNewColor;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.paint.Color;
import model.CalendarEvent;
import model.Course;
import model.CourseEvent;
import model.Meeting;
import model.MeetingSet;
import model.Profile;
import model.Term;

/**
 * Performs operations on a profile.
 */
public class ProfileController {

	public Profile profile;

	/**
	 * Instantiates a new profile controller.
	 *
	 * @param profile
	 *            the profile
	 */
	public ProfileController(Profile profile) {

		this.profile = profile;
	}

	/**
	 * Sets the currently selected date.
	 *
	 * @param localDate
	 *            the new currently selected date
	 */
	public void setCurrentlySelectedDate(LocalDate localDate) {

		this.profile.currentlySelectedDate = localDate;
		this.profile.currentlySelectedTerm = findTerm(localDate);
		this.profile.update();
	}

	/**
	 * Sets the currently selected term.
	 *
	 * @param term
	 *            the new currently selected term
	 */
	public void setCurrentlySelectedTerm(Term term) {

		this.profile.currentlySelectedTerm = term;
		profile.update();
	}

	public boolean currentlySelectedTermCoursesExist() {

		if (this.profile.currentlySelectedTerm.courses.size() > 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Gets the next color in the sequence of application colors.
	 *
	 * @return the next color
	 */
	public Color getNextColor() {
		for (Color c : Style.selectableColors) {
			if (this.profile.currentlySelectedTerm.courseColors.get(Style.colorToHex(c)) == null) {
				return c;
			}
		}
		return Color.WHITE;
	}

	/* Term Operations */

	/**
	 * Adds the term.
	 *
	 * @param added
	 *            the term being added
	 */
	public void addTerm(Term added) {

		/* If the user has defined a Term to add. */
		if (added != null) {

			if (!existingTermOverlaps(added)) {

				this.profile.terms.add(added);

				if (this.profile.terms.size() == 1) {

					LocalDate selected = this.profile.currentlySelectedDate;

					/*
					 * If the currently selected date is within the new term, keep it as the
					 * selected date and select the new term.
					 */
					if (findTerm(selected).equals(added)) {

						this.profile.currentlySelectedTerm = added;

					} else {

						/* Set the currently selected date to the start date of the new Term. */
						setCurrentlySelectedDate(this.profile.terms.get(0).start);
					}
				}

			} else {

				new Alert(AlertType.ERROR,
						"An existing term conflicts with the one being added. Please enter non-overlapping start and end dates for terms.")
								.showAndWait();
			}
		}

		profile.terms.sort(null);
		profile.update();
	}

	/**
	 * Edits the values of *only* the editable fields of the term.
	 *
	 * @param original
	 *            the term being edited
	 * @param edited
	 *            the term with the desired values
	 */
	public void editTerm(Term original, Term edited) {

		if (!existingTermOverlaps(original, edited)) {

			original.name = edited.name;
			original.start = edited.start;
			original.end = edited.end;
			this.profile.update();

		} else {

			new Alert(AlertType.ERROR,
					"An existing term conflicts with your changes. Please enter non-overlapping start and end dates for terms.")
							.showAndWait();
		}
	}

	/**
	 * Deletes term.
	 *
	 * @param deleted
	 *            the term being deleted
	 */
	public void deleteTerm(Term deleted) {

		this.profile.terms.remove(deleted);

		if (this.profile.terms.isEmpty()) {
			setCurrentlySelectedDate(Clock.now.toLocalDate());
		} else {
			setCurrentlySelectedDate(this.profile.terms.get(0).start);
		}

		this.profile.update();
	}

	/**
	 * Finds the term for the specified date.
	 *
	 * @param date
	 *            the date
	 * @return the term
	 */
	public Term findTerm(LocalDate date) {

		Term found = null;

		for (Term t : profile.terms) {
			if ((t.start.isBefore(date) || t.start.equals(date)) && (t.end.isAfter(date) || t.end.equals(date))) {
				found = t;
				break;
			}
		}

		return found;
	}

	/**
	 * Gets the terms between.
	 *
	 * @param start
	 *            the start
	 * @param end
	 *            the end
	 * @return the terms between
	 */
	public ArrayList<Term> getTermsBetween(Term start, Term end) {

		ArrayList<Term> termsBetween = new ArrayList<>();

		int startIndex = this.profile.terms.indexOf(start);
		int endIndex = this.profile.terms.indexOf(end);

		for (int i = startIndex; i <= endIndex; i++) {
			termsBetween.add(this.profile.terms.get(i));
		}

		return termsBetween;
	}

	/**
	 * Checks if an existing term overlaps with the start and end dates of the term
	 * being added.
	 *
	 * @param added
	 *            the term being added
	 * @return true, if an existing term overlaps
	 */
	private boolean existingTermOverlaps(Term added) {

		boolean overlapExists = false;

		for (Term t : this.profile.terms) {

			if (!(t.end.isBefore(added.start) || t.start.isAfter(added.end))) {
				overlapExists = true;
			}
		}

		return overlapExists;
	}

	/**
	 * Checks if an existing term (other than the original) overlaps with the start
	 * and end dates of the term being edited.
	 *
	 * @param added
	 *            the term being added
	 * @return true, if an existing term overlaps
	 */
	private boolean existingTermOverlaps(Term original, Term edited) {

		boolean overlapExists = false;

		for (Term t : this.profile.terms) {

			if (!t.equals(original)) {

				if (!(t.end.isBefore(edited.start) || t.start.isAfter(edited.end))) {
					overlapExists = true;
				}
			}
		}

		return overlapExists;
	}

	/* Course Operations */

	/**
	 * Adds the course.
	 *
	 * @param added
	 *            the course being added
	 */
	public void addCourse(Course added) {

		if (added != null) {

			while (colorInUse(added)) {
				added.color = promptNewColor(added.color);
			}

			for (Term t : added.terms) {
				t.courseColors.put(added.color, added);
				t.courses.add(added);
			}

			profile.update();
		}
	}

	/**
	 * Edits the values of *only* the editable fields of the course.
	 *
	 * @param original
	 *            the course being edited
	 * @param edited
	 *            the course with the desired values
	 */
	public void editCourse(Course original, Course edited) {

		original.name = edited.name;
		original.departmentID = edited.departmentID;
		original.code = edited.code;

		String originalColor = original.color;
		original.color = edited.color;

		/* Check if the course's color was changed. */
		if (!originalColor.equals(original.color)) {

			// If the new color is taken, keep prompting the user for another - the user
			// should also be able to choose the original color again.
			while (!originalColor.equals(original.color) && colorInUse(original)) {
				original.color = promptNewColor(original.color);
			}

			for (Term t : original.terms) {
				t.courseColors.del(originalColor);
				t.courseColors.put(original.color, original);
			}

			/* Change the color of all of the course's meetings. */
			for (MeetingSet ms : original.meetingSets) {
				for (Meeting m : ms.getMeetings()) {
					m.color = original.color;
				}
			}

			/* Change the color of all of the course's events. */
			for (CourseEvent e : original.events) {
				e.color = original.color;
			}
		}

		profile.update();
	}

	/**
	 * Deletes the course.
	 *
	 * @param deleted
	 *            the deleted course
	 */
	public void deleteCourse(Course deleted) {

		for (Term t : deleted.terms) {
			t.courseColors.del(deleted.color);
			t.courses.remove(deleted);

			for (MeetingSet ms : deleted.meetingSets) {
				for (Meeting m : ms.getMeetings()) {
					if (t.dayMeetings.get(m.date) != null && t.dayMeetings.get(m.date).contains(m)) {
						t.dayMeetings.del(m.date, m);
					}
				}
			}

			t.updateParams();
		}

		for (CalendarEvent e : deleted.events) {
			this.profile.currentlySelectedTerm.dateEvents.del(e.start.toLocalDate(), e);
		}

		profile.update();
	}

	/**
	 * Checks if the color of the course being added is in use in any of its terms.
	 *
	 * @param added
	 *            the course being added
	 * @return true, if in use
	 */
	private boolean colorInUse(Course added) {

		boolean colorInUse = false;

		for (Term t : added.terms) {
			/*
			 * Error Condition: One of the terms this course is being added to has a course
			 * with the same color.
			 */
			if (t.courseColors.get(added.color) != null) {
				new Alert(AlertType.ERROR,
						"One of the terms this course is being added to has a course with the same color.")
								.showAndWait();
				colorInUse = true;
				break;
			}
		}

		return colorInUse;
	}

	/**
	 * Prompts the user to choose a new color for the course being added.
	 *
	 * @param oldColor
	 *            the old color
	 * @return the new color
	 */
	private String promptNewColor(String oldColor) {
		return new PromptNewColor(oldColor).display();
	}

	/* Meeting Operations */

	/**
	 * Adds the meeting.
	 *
	 * @param added
	 *            the meeting being added
	 * @return true, if successful
	 */
	protected boolean addMeeting(Meeting added) {

		if (added != null) {

			boolean deleteConflicts = true;

			ArrayList<Meeting> possibleConflicts = new ArrayList<>();

			Term t = this.profile.currentlySelectedTerm;
			PriorityQueue<Meeting> sameDayMeetings = t.dayMeetings.get(added.date);

			if (sameDayMeetings != null) {
				possibleConflicts.addAll(sameDayMeetings);
			}

			ArrayList<Meeting> conflicts = added.conflictsWith(possibleConflicts);

			if (conflicts.size() > 0) {

				deleteConflicts = new HandleConflict(added, conflicts, this).display();
			}

			if (deleteConflicts) {

				for (Meeting conflict : conflicts) {

					deleteMeeting(conflict);
				}

				return true;
			}
		}
		return false;
	}

	/**
	 * Deletes the meeting.
	 *
	 * @param conflict
	 *            the meeting being deleted
	 */
	protected void deleteMeeting(Meeting conflict) {

		conflict.set.getMeetings().remove(conflict);
		this.profile.currentlySelectedTerm.dayMeetings.del(conflict.date, conflict);
	}

	/**
	 * Gets the meeting occurring at the specified time.
	 *
	 * @param dateTime
	 *            the date and time
	 * @return the meeting at the specified time
	 */
	public Meeting getMeetingAtTime(LocalDateTime dateTime) {

		PriorityQueue<Meeting> meetingsThatDay = this.profile.currentlySelectedTerm.dayMeetings
				.get(dateTime.toLocalDate());

		if (meetingsThatDay != null) {
			for (Meeting m : meetingsThatDay) {
				if ((m.start.isBefore(dateTime.toLocalTime()) || m.start.equals(dateTime.toLocalTime()))
						&& (m.end.isAfter(dateTime.toLocalTime()))) {
					return m;
				}
			}
		}

		return null;
	}

	/* Event Operations */

	/**
	 * Adds the event.
	 *
	 * @param course
	 *            the course the event belongs to - can be null if the event is
	 *            personal
	 * @param added
	 *            the event being added
	 * @param date
	 *            the date of the event
	 */
	public void addEvent(Course course, CalendarEvent added, LocalDate date) {

		this.profile.currentlySelectedTerm.dateEvents.put(date, added);

		if (course != null && added instanceof CourseEvent) {

			course.events.add((CourseEvent) added);
			((CourseEvent) added).course = course;

		} else {

			if (this.profile.recentlyUsedColors.contains(added.color)) {

				this.profile.recentlyUsedColors.remove(added.color);
			}

			this.profile.recentlyUsedColors.add(0, added.color);

			if (this.profile.recentlyUsedColors.size() > 5) {

				this.profile.recentlyUsedColors.remove(5);
			}
		}

		profile.update();
	}

	/**
	 * Deletes the event.
	 *
	 * @param course
	 *            the course the event belongs to - can be null if the event is
	 *            personal
	 * @param deleted
	 *            the event being deleted
	 * @param date
	 *            the date of the event
	 */
	public void deleteEvent(Course course, CalendarEvent deleted, LocalDate date) {

		this.profile.currentlySelectedTerm.dateEvents.del(date, deleted);

		if (course != null) {
			course.events.remove(deleted);
		}

		this.profile.update();
	}
}
