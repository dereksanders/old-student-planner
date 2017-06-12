package core;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

import javafx.scene.paint.Color;
import model.CalendarEvent;
import model.Course;
import model.CourseEvent;
import model.Meeting;
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
		active.update();
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
	 */
	public void addCourse(Course addedCourse) {
		if (addedCourse != null) {
			for (Term t : findTermsBetween(addedCourse.start, addedCourse.end)) {
				t.courses.add(addedCourse);
			}
			for (Meeting m : addedCourse.meetings) {
				m.colour = addedCourse.colour;
				active.dayMeetings.get(m.dayOfWeekInt - 1).add(m);
				active.currentlySelectedTerm.updateParams(m);
			}
			active.courseColors.put(Color.web(addedCourse.colour), addedCourse);
			active.update();
		}
	}

	/**
	 * Edits the course.
	 *
	 * @param edited
	 *            the edited
	 */
	public void editCourse(Course edited, Course changes) {

		edited.name = changes.name;
		edited.departmentID = changes.departmentID;
		edited.code = changes.code;
		edited.colour = changes.colour;

		for (Term t : findTermsBetween(edited.start, edited.end)) {
			t.resetParams();
		}

		/*
		 * If the edited course belongs to the currently selected term, redraw
		 * views.
		 */
		if (this.active.currentlySelectedTerm.courses.contains(edited)) {

			setCurrentlySelectedDate(this.active.currentlySelectedDate);
		}

		/*
		 * In case the course's colour was changed. TODO: Remove mapping of
		 * previous colour to edited course.
		 */
		if (this.active.courseColors.get(Color.web(edited.colour)) == null) {
			this.active.courseColors.put(Color.web(edited.colour), edited);
			for (Meeting m : edited.meetings) {
				m.colour = edited.colour;
			}
			for (CourseEvent e : edited.events) {
				e.colour = edited.colour;
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
		this.active.courseColors.del(Color.web(deletedCourse.colour), deletedCourse);
		for (Term t : findTermsBetween(deletedCourse.start, deletedCourse.end)) {
			t.courses.remove(deletedCourse);
		}
		for (Meeting m : deletedCourse.meetings) {
			this.active.dayMeetings.get(m.dayOfWeekInt - 1).remove(m);
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

	/**
	 * Delete meeting.
	 *
	 * @param c
	 *            the c
	 * @param m
	 *            the m
	 */
	public void deleteMeeting(Course c, Meeting m) {
		if (m != null) {
			c.meetings.remove(m);
			active.dayMeetings.get(m.dayOfWeekInt - 1).remove(m);
			active.update();
		}
	}

	/**
	 * Adds the meeting.
	 *
	 * @param currentlySelected
	 *            the currently selected
	 * @param m
	 *            the m
	 */
	public void addMeeting(Course currentlySelected, Meeting m) {
		if (m != null) {
			m.colour = currentlySelected.colour;

			/* TODO: Deal with conflict. */
			// ArrayList<Meeting> outerConflict =
			// m.conflictsWithCourses(chooseTerm.getValue().courses);
			//
			// if (outerConflict.size() > 0) {
			// confirmAddMeetings = HandleConflict.display(m, outerConflict);
			// }

			// ArrayList<Meeting> allConflicts = new ArrayList<>();
			// allConflicts.addAll(outerConflict);
			// Meeting.deleteMeetings(allConflicts);
			currentlySelected.meetings.add(m);
			active.dayMeetings.get(m.dayOfWeekInt - 1).add(m);
			active.update();
			// CourseSchedule.setTodaysMeetings();
		}
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
			active.courseColors.get(Color.web((event).colour)).events.add((CourseEvent) event);
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
			active.courseColors.get(Color.web((event).colour)).events.remove(event);
		}
		active.update();
	}

	public Course getCourseFromColor(Color c) {

		return this.active.courseColors.get(c);
	}

	/**
	 * Gets the meeting at time.
	 *
	 * @param cell
	 *            the cell
	 * @return the meeting at time
	 */
	public Meeting getMeetingAtTime(LocalDateTime cell) {

		for (Course c : this.active.currentlySelectedTerm.courses) {
			for (Meeting m : c.meetings) {
				if ((m.dayOfWeekInt == cell.getDayOfWeek().getValue())
						&& (m.start.isBefore(cell.toLocalTime()) || m.start.equals(cell.toLocalTime()))
						&& (m.end.isAfter(cell.toLocalTime()) || m.end.equals(cell.toLocalTime()))) {
					return m;
				}
			}
		}
		return null;
	}
}
