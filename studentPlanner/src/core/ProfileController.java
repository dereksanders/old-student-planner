package core;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.PriorityQueue;

import courseSchedule.HandleConflict;
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
import planner.Planner;

/**
 * The Class ProfileController.
 */
public class ProfileController {

	public Profile active;
	public Planner planner;

	/**
	 * Instantiates a new profile controller.
	 *
	 * @param active
	 *            the active
	 * @param p
	 *            the p
	 */
	public ProfileController(Profile active, Planner p) {
		this.active = active;
		this.planner = p;
	}

	/**
	 * Sets the currently selected date.
	 *
	 * @param localDate
	 *            the new currently selected date
	 */
	public void setCurrentlySelectedDate(LocalDate localDate) {
		this.active.currentlySelectedDate = localDate;
		this.active.currentlySelectedTerm = findTerm(localDate);
		this.active.update();
	}

	/**
	 * Sets the currently selected term.
	 *
	 * @param term
	 *            the new currently selected term
	 */
	public void setCurrentlySelectedTerm(Term term) {
		this.active.currentlySelectedTerm = term;
		active.update();
	}

	/* Term Operations */

	/**
	 * Adds the term.
	 *
	 * @param add
	 *            the add
	 */
	public void addTerm(Term add) {

		/* If the user has defined a Term to add. */
		if (add != null) {

			this.active.terms.add(add);

			/*
			 * If the profile previously had zero Terms:
			 * 
			 * 1) Enable the Edit Term and Add Course buttons.
			 * 
			 * 2) Select the new Term & its start date
			 * 
			 * 3) Initialize the Term Calendar.
			 */
			if (this.active.terms.size() == 1) {

				LocalDate selected = this.active.currentlySelectedDate;

				/* If the currently selected date is within the new Term. */
				if (((selected.isEqual(add.start) || selected.isAfter(add.start))
						&& (selected.isEqual(add.end) || selected.isBefore(add.end)))) {

					setCurrentlySelectedDate(selected);
				} else {

					setCurrentlySelectedDate(this.active.terms.get(0).start);
				}
			}
		}

		/*
		 * Sort terms chronologically. TODO: Is the sorting ascending or
		 * descending?
		 */
		active.terms.sort(null);
		active.update();
	}

	public void editTerm(Term old, Term edited) {
		old.name = edited.name;
		old.start = edited.start;
		old.end = edited.end;
		this.active.update();
	}

	/**
	 * Delete term.
	 */
	public void deleteTerm(Term t) {
		this.active.terms.remove(t);
		this.active.update();
	}

	/**
	 * Find the term for the specified date.
	 *
	 * @param date
	 *            the date
	 * @return the term
	 */
	public Term findTerm(LocalDate date) {
		Term result = null;
		for (Term t : active.terms) {
			if ((t.start.isBefore(date) || t.start.equals(date)) && (t.end.isAfter(date) || t.end.equals(date))) {
				result = t;
				break;
			}
		}
		return result;
	}

	/* Course operations */

	/**
	 * Adds the course.
	 *
	 * @param addedCourse
	 *            the added course
	 * @throws IllegalCourseException
	 */
	public void addCourse(Course addedCourse) throws IllegalCourseException {

		if (addedCourse != null) {

			for (Term t : findTermsBetween(addedCourse.start, addedCourse.end)) {
				/*
				 * Error Condition: One of the terms this course is being added
				 * to has a course with the same colour.
				 */
				if (t.courseColors.get(Color.web(addedCourse.colour)) != null) {
					throw new IllegalCourseException(
							"One of the terms this course is being added to has a course with the same colour.");
				} else {
					t.courseColors.put(Color.web(addedCourse.colour), addedCourse);
					t.courses.add(addedCourse);
				}
			}

			active.update();
		}
	}

	/**
	 * Edits the course.
	 *
	 * @param original
	 *            the edited
	 */
	public void editCourse(Course original, Course edit) {

		original.name = edit.name;
		original.departmentID = edit.departmentID;
		original.code = edit.code;
		Color originalColor = Color.web(original.colour);
		original.colour = edit.colour;

		for (Term t : findTermsBetween(original.start, original.end)) {
			t.updateParams();
		}

		/*
		 * If the edited course belongs to the currently selected term, redraw
		 * views.
		 */
		if (this.active.currentlySelectedTerm.courses.contains(original)) {

			setCurrentlySelectedDate(this.active.currentlySelectedDate);
		}

		/* Check if the course's colour was changed. */

		if (!originalColor.equals(Color.web(original.colour))) {

			ArrayList<Term> courseTerms = findTermsBetween(original.start, original.end);

			for (int i = 0; i < courseTerms.size(); i++) {

				if (courseTerms.get(i).courseColors.get(Color.web(original.colour)) == null) {

					courseTerms.get(i).courseColors.del(originalColor, original);
					courseTerms.get(i).courseColors.put(Color.web(original.colour), original);

				} else {
					/*
					 * Error Condition: Edited colour is already in use by a
					 * course in courseTerms.get(i). Need to revert changes to
					 * courseColors if any have been made and reject changes.
					 * TODO: Better solution could be adding a listener to the
					 * colour picker to inform the user when this is the case.
					 */
					for (int j = 0; j < i; j++) {
						courseTerms.get(j).courseColors.del(Color.web(original.colour), original);
						courseTerms.get(j).courseColors.put(originalColor, original);
					}
					original.colour = Style.colorToHex(originalColor);
					Alert illegalColour = new Alert(AlertType.ERROR,
							"Edited colour is already in use. Colour change has been reverted.");
					illegalColour.show();
					break;
				}

				if (i == courseTerms.size() - 1) {

					for (MeetingSet ms : original.meetingSets) {
						ms.setColor(original.colour);
					}

					for (CourseEvent e : original.events) {
						e.colour = original.colour;
					}
				}
			}
		}

		active.update();
	}

	/**
	 * Delete course.
	 *
	 * @param deletedCourse
	 *            the deleted course
	 */
	public void deleteCourse(Course deletedCourse) {

		for (Term t : findTermsBetween(deletedCourse.start, deletedCourse.end)) {
			t.courseColors.del(Color.web(deletedCourse.colour), deletedCourse);
			t.courses.remove(deletedCourse);

			for (MeetingSet ms : deletedCourse.meetingSets) {
				for (Meeting m : ms.getMeetings()) {
					if (t.dayMeetings.get(m.date).contains(m)) {
						t.dayMeetings.del(m.date, m);
					}
				}
			}

			t.updateParams();
		}
		for (CalendarEvent e : deletedCourse.events) {
			this.active.dateEvents.del(e.start.toLocalDate(), e);
		}
		active.update();
	}

	/**
	 * Find terms between.
	 *
	 * @param start
	 *            the start
	 * @param end
	 *            the end
	 * @return the array list
	 */
	public ArrayList<Term> findTermsBetween(LocalDate start, LocalDate end) {

		ArrayList<Term> termsBetween = new ArrayList<>();

		for (Term t : this.active.terms) {
			if ((t.start.isEqual(start) || t.start.isAfter(start)) && (t.end.isEqual(end) || t.end.isBefore(end))) {
				termsBetween.add(t);
			}
		}

		return termsBetween;
	}

	private void deleteMeeting(Meeting m) {

		for (Course c : this.active.currentlySelectedTerm.courses) {

			for (MeetingSet ms : c.meetingSets) {

				if (ms.getMeetings().contains(m)) {
					ms.getMeetings().remove(m);
					this.active.currentlySelectedTerm.dayMeetings.del(m.date, m);
				}
			}
		}
	}

	/**
	 * Adds the meeting.
	 *
	 * @param currentlySelected
	 *            the currently selected the m
	 */
	protected boolean addMeeting(Course currentlySelected, Meeting m) {

		if (m != null) {

			boolean deleteConflicts = true;

			Term t = this.active.currentlySelectedTerm;

			ArrayList<Meeting> possibleConflicts = new ArrayList<>();

			if (t.dayMeetings.get(m.date) != null) {
				possibleConflicts.addAll(t.dayMeetings.get(m.date));
			}

			ArrayList<Meeting> conflicts = m.conflictsWith(possibleConflicts);

			if (conflicts.size() > 0) {

				deleteConflicts = new HandleConflict(m, conflicts, this).display();
			}

			if (deleteConflicts) {

				for (Meeting conflict : conflicts) {

					deleteMeeting(conflict);
				}

				m.colour = currentlySelected.colour;
				return true;
			}
		}
		return false;
	}

	/**
	 * Adds the event.
	 *
	 * @param course
	 *            the course
	 * @param event
	 *            the event
	 * @param date
	 *            the date
	 */
	public void addEvent(Course course, CalendarEvent event, LocalDate date) {
		active.dateEvents.put(date, event);
		if (course != null) {
			for (Term t : findTermsBetween(course.start, course.end)) {
				t.courseColors.get(Color.web((event).colour)).events.add((CourseEvent) event);
			}
		} else {
			active.personalEvents.add(event);
		}
		active.update();
	}

	/**
	 * Delete event.
	 *
	 * @param course
	 *            the course
	 * @param event
	 *            the event
	 * @param date
	 *            the date
	 */
	public void deleteEvent(Course course, CalendarEvent event, LocalDate date) {
		active.dateEvents.del(date, event);
		if (course != null) {
			for (Term t : findTermsBetween(course.start, course.end)) {
				t.courseColors.get(Color.web((event).colour)).events.remove(event);
			}
		} else {
			active.personalEvents.remove(event);
		}
		active.update();
	}

	public Course getCourseFromColor(Color c) {

		return this.active.currentlySelectedTerm.courseColors.get(c);
	}

	/**
	 * Gets the meeting at time.
	 *
	 * @param cell
	 *            the cell
	 * @return the meeting at time
	 */
	public Meeting getMeetingAtTime(LocalDateTime cell) {

		PriorityQueue<Meeting> meetingsThatDay = this.active.currentlySelectedTerm.dayMeetings.get(cell.toLocalDate());

		if (meetingsThatDay != null) {
			for (Meeting m : meetingsThatDay) {
				if (m.start.isBefore(cell.toLocalTime()) || m.start.equals(cell.toLocalTime())
						&& (m.end.isAfter(cell.toLocalTime()) || m.end.equals(cell.toLocalTime()))) {
					return m;
				}
			}
		}

		return null;
	}
}
