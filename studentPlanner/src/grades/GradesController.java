package grades;

import core.ProfileController;
import model.Profile;
import planner.Planner;

public class GradesController extends ProfileController {

	public Grades grades;

	public GradesController(Profile active, Planner p) {
		super(active, p);
	}

}
