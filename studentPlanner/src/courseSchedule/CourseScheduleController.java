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
	 * FIXME: This should not manipulate GUI elements - breaks MVC paradigm.
	 *
	 * @param showCurrentWeek
	 *            the show current week
	 */
	public void showCurrentWeek(Boolean showCurrentWeek) {

		if (showCurrentWeek) {

			/* Disable selecting a different week. */
			schedule.selectWeek.setVisible(false);
			schedule.selectWeek.setManaged(false);

			LocalDate present = Clock.now.toLocalDate();
			schedule.selectWeek.setValue(present);
			updateWeekSelected(present);
		}

		/* If the new value is to allow choosing a different week. */
		else {
			schedule.selectWeek.setVisible(true);
			schedule.selectWeek.setManaged(true);
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

		// If any of the meetings of the new MeetingSet were actually added - it's
		// possible that they were all cancelled due to conflicts.
		if (added.getMeetings().size() > 0) {

			if (added.getCourse() != null) {
				added.getCourse().meetingSets.add(added);
			} else {
				t.nonCourseMeetingSets.add(added);

				if (this.profile.recentlyUsedColors.contains(added.getColor())) {

					this.profile.recentlyUsedColors.remove(added.getColor());
				}

				this.profile.recentlyUsedColors.add(0, added.getColor());

				if (this.profile.recentlyUsedColors.size() > 5) {

					this.profile.recentlyUsedColors.remove(5);
				}
			}

			t.updateParams();
			this.profile.update();
		}
	}

	/**
	 * Deletes the meeting from its meeting set.
	 * 
	 * @param selected
	 *            the meeting being deleted
	 */
	public void deleteMeetingFromSet(Meeting selected) {

		if (selected.set.getMeetings().size() == 1) {
			deleteMeetingSet(selected.set);
		} else {
			deleteMeeting(selected);
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

		if (deleted.getCourse() != null) {
			deleted.getCourse().meetingSets.remove(deleted);
		} else {
			t.nonCourseMeetingSets.remove(deleted);
		}

		t.updateParams();
		this.profile.update();
	}
}
