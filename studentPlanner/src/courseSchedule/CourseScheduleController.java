package courseSchedule;

import java.time.LocalDate;
import java.time.LocalDateTime;
import core.Driver;
import core.ProfileController;
import javafx.scene.paint.Color;
import model.Course;
import model.Meeting;
import model.MeetingSet;
import model.Profile;
import model.Term;
import planner.Planner;

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

		if (getMeetingAtTime(cell) != null) {
			return true;
		}

		return false;
	}

	public void addMeetingSet(Course c, MeetingSet ms, String repeat) {

		Term t = this.active.currentlySelectedTerm;

		MeetingSet add = new MeetingSet();
		add.repeat = repeat;

		for (Meeting m : ms.getMeetings()) {

			if (addMeeting(c, m)) {
				add.addMeeting(m);
				t.dayMeetings.put(m.date, m);
			}
		}

		c.meetingSets.add(add);
		t.updateParams();
		this.active.update();
	}

	public MeetingSet getMeetingSet(LocalDateTime cell) {

		Meeting m = getMeetingAtTime(cell);

		for (MeetingSet ms : this.active.currentlySelectedTerm.courseColors.get(Color.web(m.colour)).meetingSets) {

			if (ms.getMeetings().contains(m)) {
				return ms;
			}
		}

		return null;
	}

	public void deleteSingleInstance(MeetingSet ms, Meeting m) {

		if (ms.getMeetings().size() == 1) {
			deleteMeetingSet(ms);
		} else {

			ms.getMeetings().remove(m);
			this.active.currentlySelectedTerm.dayMeetings.del(m.date, m);
			this.active.update();
		}
	}

	public void deleteMeetingSet(MeetingSet deleted) {

		Term t = this.active.currentlySelectedTerm;

		for (Meeting m : deleted.getMeetings()) {
			t.dayMeetings.del(m.date, m);
		}

		for (Course c : this.active.currentlySelectedTerm.courses) {
			if (c.meetingSets.contains(deleted)) {
				c.meetingSets.remove(deleted);
			}
		}

		t.updateParams();
		this.active.update();
	}
}
