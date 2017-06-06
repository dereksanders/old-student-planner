package core;

import java.time.LocalDate;
import java.util.ArrayList;

import javafx.scene.paint.Color;
import model.CalendarEvent;
import model.Course;
import model.Meeting;
import model.Profile;
import model.Term;

public class ProfileController {

	public Profile active;
	public Planner planner;

	public ProfileController(Profile active, Planner p) {
		this.active = active;
		this.planner = p;
	}

	public void setCurrentlySelectedDate(LocalDate localDate) {
		this.active.currentlySelectedDate = localDate;
		this.active.currentlySelectedTerm = findTerm(localDate);
		active.update();
	}

	public void setCurrentlySelectedTerm(Term term) {
		this.active.currentlySelectedTerm = term;
		active.update();
	}

	/* Term Operations */

	/**
	 * Adds the term.
	 *
	 * @param addedTerm
	 *            the added term
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

	public void deleteTerm() {

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

	public void addCourse(Course addedCourse) {
		if (addedCourse != null) {
			for (Term t : findTermsBetween(addedCourse.start, addedCourse.end)) {
				t.courses.add(addedCourse);
			}
			for (Meeting m : addedCourse.meetings) {
				active.dayMeetings.get(m.dayOfWeekInt - 1).add(m);
				active.currentlySelectedTerm.updateParams(m);
			}
			active.courseColors.put(Color.web(addedCourse.colour), addedCourse);
			active.update();
		}
	}

	public void editCourse(Course edited) {

		/* If the user has confirmed changes to an existing course */
		if (edited != null) {

			for (Term t : findTermsBetween(edited.start, edited.end)) {
				t.resetParams();
			}

			/*
			 * If the edited course belongs to the currently selected term,
			 * redraw views.
			 */
			if (this.active.currentlySelectedTerm.courses.contains(edited)) {

				setCurrentlySelectedDate(this.active.currentlySelectedDate);
			}

			active.update();
		}
	}

	public void deleteCourse(Course deletedCourse) {
		for (Term t : findTermsBetween(deletedCourse.start, deletedCourse.end)) {
			t.courses.remove(deletedCourse);
		}
		for (CalendarEvent e : deletedCourse.events) {
			this.active.dateEvents.del(e.start.toLocalDate(), e);
		}
		active.update();
	}

	public ArrayList<Term> findTermsBetween(LocalDate start, LocalDate end) {

		ArrayList<Term> termsBetween = new ArrayList<>();

		for (Term t : this.active.terms) {
			if ((t.start.isEqual(start) || t.start.isAfter(start)) && (t.end.isEqual(end) || t.end.isBefore(end))) {
				termsBetween.add(t);
			}
		}

		return termsBetween;
	}

	public void deleteMeeting(Course c, Meeting m) {
		if (m != null) {
			c.meetings.remove(m);
			active.dayMeetings.get(m.dayOfWeekInt - 1).remove(m);
		}
	}

	public void addMeeting(Course currentlySelected, Meeting m) {
		if (m != null) {

			m.colour = currentlySelected.colour;
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
			// CourseSchedule.setTodaysMeetings();
		}
	}

	public void addEvent(Course course, CalendarEvent event, LocalDate date) {
		active.dateEvents.put(date, event);
		if (course != null) {
			active.courseColors.get(Color.web((event).colour)).peek().events.add(event);
		}
		active.update();
	}

	public void deleteEvent(Course course, CalendarEvent event, LocalDate date) {
		active.dateEvents.del(date, event);
		if (course != null) {
			active.courseColors.get(Color.web((event).colour)).peek().events.remove(event);
		}
		active.update();
	}
}
