package grades;

import core.Main;
import core.ProfileController;
import model.CourseEvent;
import model.Profile;
import planner.Planner;

public class GradesController extends ProfileController {

	public Grades grades;

	public GradesController(Profile profile) {
		super(profile);
	}

	public void updateGrade(CourseEvent e, double grade) {
		e.grade = grade;
		e.gradeEntered = true;

		e.course.calcGrades();
		this.profile.currentlySelectedTerm.calcGrades();

		// Refresh all views except for Grades.
		for (int i = 0; i < Main.driver.planner.views.size(); i++) {

			if (i != Planner.VIEW_INDEX.GRADES.val) {
				Main.driver.planner.views.get(i).refresh();
			}
		}
	}

	public void updateWeight(CourseEvent e, double weight) {
		e.weight = weight;
	}
}
