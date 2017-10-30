package grades;

import core.Main;
import core.ProfileController;
import model.CourseEvent;
import model.Profile;
import model.Term;
import planner.Planner;

public class GradesController extends ProfileController {

	public Grades grades;

	public GradesController(Profile profile) {
		super(profile);
	}

	public void updateGrade(CourseEvent e, double grade) {

		e.grade = grade;
		e.state = CourseEvent.STATES.GRADED.val;

		e.course.calcGrades();

		for (Term t : e.course.terms) {
			t.calcGrades();
		}

		// Refresh all views except for Grades so that the selected course and event do
		// not reset.
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
