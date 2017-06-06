package grades;

import core.Planner;
import core.ProfileController;
import model.Profile;

public class GradesController extends ProfileController {

	public Grades grades;

	public GradesController(Profile active, Planner p) {
		super(active, p);
	}

}
