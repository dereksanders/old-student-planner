package courseSchedule;

import java.time.LocalDate;
import java.time.LocalDateTime;

import core.Clock;
import core.ProfileController;
import model.Meeting;
import model.MeetingSet;
import model.Profile;
import model.Term;

/**
 * The Class CourseScheduleController.
 */
public class CourseScheduleController extends ProfileController {

	public CourseSchedule schedule;

	/**
	 * Instantiates a new course schedule controller.
	 *
	 * @param profile
	 *            the profile
	 */
	public CourseScheduleController(Profile profile) {
		super(profile);
	}

	/**
	 * Show current week.
	 *
	 * @param showCurrentWeek
	 *            the show current week
	 */
	public void showCurrentWeek(Boolean showCurrentWeek) {

		if (showCurrentWeek) {

			/* Disable selecting a different week. */
			schedule.selectWeek.setVisible(false);

			LocalDate present = Clock.now.toLocalDate();
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

	/**
	 * Time is occupied.
	 *
	 * @param cell
	 *            the cell
	 * @return true, if successful
	 */
	public boolean timeIsOccupied(LocalDateTime cell) {

		return getMeetingAtTime(cell) != null;
	}

	/**
	 * Adds the meeting set.
	 *
	 * @param course
	 *            the course the meetings belong to
	 * @param meetingSet
	 *            the set of all meetings being added - some may not end up being
	 *            added due to conflicts
	 * @param repeat
	 *            the repeat setting of the meeting
	 */
	public void addMeetingSet(MeetingSet meetingSet, String repeat) {

		Term t = this.profile.currentlySelectedTerm;

		MeetingSet added = new MeetingSet();
		added.repeat = repeat;

		for (Meeting m : meetingSet.getMeetings()) {

			if (addMeeting(m)) {
				added.addMeeting(m);
				m.set = added;
				t.dayMeetings.put(m.date, m);
			}
		}

		added.getCourse().meetingSets.add(added);
		t.updateParams();
		this.profile.update();
	}

	/**
	 * Deletes the meeting from its meeting set.
	 * 
	 * @param deleted
	 *            the meeting being deleted
	 */
	public void deleteMeetingFromSet(Meeting deleted) {

		if (deleted.set.getMeetings().size() == 1) {
			deleteMeetingSet(deleted.set);
		} else {
			deleteMeeting(deleted);
			this.profile.update();
		}
	}

	/**
	 * Deletes the meeting set.
	 *
	 * @param deleted
	 *            the meeting set being deleted
	 */
	public void deleteMeetingSet(MeetingSet deleted) {

		Term t = this.profile.currentlySelectedTerm;

		for (Meeting m : deleted.getMeetings()) {
			t.dayMeetings.del(m.date, m);
		}

		deleted.getCourse().meetingSets.remove(deleted);

		t.updateParams();
		this.profile.update();
	}
}
