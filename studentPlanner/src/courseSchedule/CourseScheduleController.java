package courseSchedule;

import java.time.LocalDate;
import java.time.LocalDateTime;

import core.Driver;
import core.Planner;
import core.ProfileController;
import model.Course;
import model.Meeting;
import model.Profile;

public class CourseScheduleController extends ProfileController {

	public CourseSchedule schedule;

	public CourseScheduleController(Profile active, Planner p) {
		super(active, p);
	}

	public void showCurrentWeek(Boolean newVal) {
		/*
		 * Actions to perform when the checkbox to show the current week is
		 * toggled.
		 */

		/* If the new value is to show the current week. */
		if (newVal) {

			/* Disable selecting a different week. */
			schedule.selectWeek.setVisible(false);

			LocalDate present = Driver.t.current.toLocalDate();
			schedule.selectWeek.setValue(present);
			updateWeekSelected(present);
		}

		/* If the new value is to allow choosing a different week. */
		else {
			schedule.selectWeek.setVisible(true);
			schedule.selectWeek.requestFocus();
		}
	}

	/**
	 * Update week selected.
	 *
	 * @param newDate
	 *            the new date
	 */
	public void updateWeekSelected(LocalDate newDate) {

		setCurrentlySelectedDate(newDate);
	}

	public boolean timeIsOccupied(LocalDateTime cell) {

		for (Course c : this.active.currentlySelectedTerm.courses) {
			for (Meeting m : c.meetings) {
				if ((m.dayOfWeekInt == cell.getDayOfWeek().getValue())
						&& (m.start.isBefore(cell.toLocalTime()) || m.start.equals(cell.toLocalTime()))
						&& (m.end.isAfter(cell.toLocalTime()) || m.end.equals(cell.toLocalTime()))) {
					return true;
				}
			}
		}

		return false;
	}
}
