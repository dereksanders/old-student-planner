package grades;

import core.ProfileController;
import model.CourseEvent;
import model.Profile;
import planner.Planner;

public class GradesController extends ProfileController {

	public Grades grades;

	public GradesController(Profile active, Planner p) {
		super(active, p);
	}

	public void updateGrade(CourseEvent e, double grade) {
		e.grade = grade;
		e.gradeEntered = true;
		this.active.update();
	}

	public void updateWeight(CourseEvent e, double weight) {
		e.weight = weight;
		this.active.update();
	}
}
