package core;

import courseSchedule.CourseSchedule;
import courseSchedule.CourseScheduleController;
import grades.Grades;
import grades.GradesController;
import gradesPlot.GradesPlot;
import gradesPlot.GradesPlotController;
import javafx.application.Application;
import javafx.embed.swing.JFXPanel;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import termCalendar.TermCalendar;
import termCalendar.TermCalendarController;

public class Main extends Application {

	public static Driver driver;

	/**
	 * The main method.
	 *
	 * @param args
	 *            the arguments
	 */
	public static void main(String[] args) {

		/*
		 * This triggers JavaFX's hidden initialization on start. The jar will not
		 * launch without this.
		 */
		@SuppressWarnings("unused")
		final JFXPanel fxPanel = new JFXPanel();

		driver = new Driver("res", "res//backup");

		/* CourseSchedule */
		CourseScheduleController scheduleController = new CourseScheduleController(driver.active);
		CourseSchedule schedule = new CourseSchedule(scheduleController);
		driver.planner.addView(schedule);

		/* TermCalendar */
		TermCalendarController calendarController = new TermCalendarController(driver.active);
		TermCalendar calendar = new TermCalendar(calendarController);
		driver.planner.addView(calendar);

		/* Grades */
		GradesController gradesController = new GradesController(driver.active);
		Grades grades = new Grades(gradesController);
		driver.planner.addView(grades);

		/* GradesPlot */
		GradesPlotController gradesPlotController = new GradesPlotController(driver.active);
		GradesPlot gradesPlot = new GradesPlot(gradesPlotController);
		driver.planner.addView(gradesPlot);

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
		driver.active.save();
		System.exit(0);
	}

}
