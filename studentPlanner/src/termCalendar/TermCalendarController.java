package termCalendar;

import core.ProfileController;
import model.Profile;
import planner.Planner;

public class TermCalendarController extends ProfileController {

	public TermCalendar calendar;

	public TermCalendarController(Profile active, Planner p) {
		super(active, p);
	}

}
