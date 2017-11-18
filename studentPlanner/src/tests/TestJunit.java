package tests;

import java.time.LocalDate;
import java.util.ArrayList;

import org.junit.BeforeClass;
import org.junit.Test;

import core.Driver;
import core.Main;
import core.ProfileController;
import core.Style;
import model.Course;
import model.Profile;
import model.Term;

public class TestJunit {

	private static Profile test;
	private static ProfileController pc;
	private static Term summer;

	@BeforeClass
	public static void init() {

		test = new Profile("Test");
		pc = new ProfileController(test);
		pc.profile.currentlySelectedDate = LocalDate.now();
		Main.driver = new Driver("test", "test//backup");
	}

	@Test
	public void addTerm() {

		summer = new Term("Summer", LocalDate.of(2017, 5, 1), LocalDate.of(2017, 8, 31));
		pc.addTerm(summer);
	}

	@Test
	public void addCourse() {

		pc.addCourse(new Course("Psychology", "PSYC", 101, pc.getTermsBetween(summer, summer), new ArrayList<>(),
				new ArrayList<>(), Style.colorToHex(Style.appBlue)));
	}
}
