package grades;

import core.ProfileController;
import model.CourseEvent;
import model.Profile;

public class GradesController extends ProfileController {

	public Grades grades;

	public GradesController(Profile profile) {
		super(profile);
	}

	public void updateGrade(CourseEvent e, double grade) {
		e.grade = grade;
		e.gradeEntered = true;
		this.profile.update();
	}

	public void updateWeight(CourseEvent e, double weight) {
		e.weight = weight;
		this.profile.update();
	}
}
