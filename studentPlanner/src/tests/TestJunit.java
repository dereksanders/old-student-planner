package tests;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

import org.junit.*;

import core.Course;
import core.Meeting;
import core.Planner;
import core.Term;
import courseSchedule.CourseSchedule;
import javafx.scene.paint.Color;

public class TestJunit {

	@BeforeClass
	public static void init() {

		Planner.main(null);
	}

	@Test
	public void termParamsTest() {

		Term summer = new Term("Summer 2017", LocalDate.of(2017, 5, 1), LocalDate.of(2017, 8, 31));
		Planner.terms.add(summer);
		ArrayList<Term> summerTerms = new ArrayList<>();
		summerTerms.add(summer);

		ArrayList<Meeting> meetings = new ArrayList<>();
		Meeting psycLecture = new Meeting("Lecture", LocalTime.of(7, 30), LocalTime.of(9, 30), "Monday", "");
		meetings.add(psycLecture);
		Planner.dayMeetings.put("Monday", psycLecture);

		Course psyc = new Course("Psychology", "PSYC", 101, summerTerms, meetings, Planner.colorToHex(Color.RED));
		Planner.courses.add(psyc);
		Planner.courseColors.put(Color.RED, psyc);

		Planner.termCourses.put(summer, psyc);
		Planner.saveProfile("default");
		Planner.currentlySelectedTerm = Planner.terms.get(0);
		CourseSchedule.drawSchedule(Planner.currentlySelectedTerm, LocalDate.now());
	}
}
