package controllers;

import core.Planner;
import core.Profile;
import core.ProfileController;
import views.TermCalendar;

public class TermCalendarController extends ProfileController {

	public TermCalendar calendar;

	public TermCalendarController(Profile active, Planner p) {
		super(active, p);
	}

}
