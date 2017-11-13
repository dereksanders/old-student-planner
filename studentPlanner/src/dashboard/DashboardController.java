package dashboard;

import core.ProfileController;
import model.CalendarEvent;
import model.CourseEvent;
import model.Profile;

public class DashboardController extends ProfileController {

	public Dashboard dashboard;

	public DashboardController(Profile profile) {

		super(profile);
	}

	public void addPriority(CalendarEvent e) {

		this.profile.currentlySelectedTerm.priorities.add(e);

		// Only the dashboard view is affected by changes to the term's priorities.
		this.dashboard.refresh();
	}

	public void deletePriority(CalendarEvent e) {

		this.profile.currentlySelectedTerm.priorities.remove(e);

		this.dashboard.refresh();
	}

	public void increasePriority(CalendarEvent e) {

		int prevIndex = this.profile.currentlySelectedTerm.priorities.indexOf(e);

		if (prevIndex != 0) {

			CalendarEvent temp = this.profile.currentlySelectedTerm.priorities.get(prevIndex - 1);
			this.profile.currentlySelectedTerm.priorities.remove(prevIndex - 1);

			this.profile.currentlySelectedTerm.priorities.add(prevIndex, temp);
		}

		this.dashboard.refresh();
	}

	public void decreasePriority(CalendarEvent e) {

		int prevIndex = this.profile.currentlySelectedTerm.priorities.indexOf(e);

		if (prevIndex != this.profile.currentlySelectedTerm.priorities.size() - 1) {

			this.profile.currentlySelectedTerm.priorities.add(prevIndex,
					this.profile.currentlySelectedTerm.priorities.get(prevIndex + 1));

			this.profile.currentlySelectedTerm.priorities.remove(prevIndex + 2);
		}

		this.dashboard.refresh();
	}

	public void updateState(CourseEvent e, int state) {

		e.state = state;

		if (state == CourseEvent.STATES.SUBMITTED.val) {

			this.profile.currentlySelectedTerm.priorities.remove(e);
		}

		this.profile.update();
	}
}
