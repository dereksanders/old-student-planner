package core;

import courseSchedule.CourseSchedule;
import courseSchedule.CourseScheduleController;
import grades.Grades;
import grades.GradesController;
import gradesPlot.GradesPlot;
import gradesPlot.GradesPlotController;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import termCalendar.TermCalendar;
import termCalendar.TermCalendarController;
import utility.JSONParser;

public class Main extends Application {

	private static Driver driver;

	/**
	 * The main method.
	 *
	 * @param args
	 *            the arguments
	 */
	public static void main(String[] args) {

		driver = new Driver();

		/* Instantiate Schedule View & Controller */
		CourseScheduleController scheduleController = new CourseScheduleController(driver.active, driver.planner);
		CourseSchedule schedule = new CourseSchedule(driver.active, scheduleController);
		driver.planner.addView(schedule);

		/* Instantiate Calendar View & Controller */
		TermCalendarController calendarController = new TermCalendarController(driver.active, driver.planner);
		TermCalendar calendar = new TermCalendar(driver.active, calendarController);
		driver.planner.addView(calendar);

		/* Instantiate Grades View & Controller */
		GradesController gradesController = new GradesController(driver.active, driver.planner);
		Grades grades = new Grades(driver.active, gradesController);
		// driver.planner.addView(grades);

		/* Instantiate GradesPlot View & Controller */
		GradesPlotController gradesPlotController = new GradesPlotController(driver.active, driver.planner);
		GradesPlot gradesPlot = new GradesPlot(driver.active, gradesPlotController);
		// driver.planner.addView(gradesPlot);

		driver.planner.refresh();

		launch(args);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javafx.application.Application#start(javafx.stage.Stage)
	 */
	@Override
	public void start(Stage window) throws Exception {

		/* Application window */
		window.setTitle("Student Planner");
		window.getIcons().add(new Image(getClass().getResourceAsStream("icon.png")));
		window.setScene(driver.planner.getScene());
		window.setResizable(true);
		window.show();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javafx.application.Application#stop()
	 */
	@Override
	public void stop() throws Exception {
		System.out.println("Saving and exiting..");
		JSONParser.saveProfile(driver.active);
	}

}
