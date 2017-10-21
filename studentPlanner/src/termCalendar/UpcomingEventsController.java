package termCalendar;

import core.ProfileController;
import model.Profile;

public class UpcomingEventsController extends ProfileController {

	public UpcomingEventsController(Profile profile) {
		super(profile);
	}

	public void setShowWithinThreshold(int threshold) {

		this.profile.showWithinThreshold = threshold;
		this.profile.update();
	}
}
