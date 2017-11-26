package core;

import java.time.LocalDate;

import courseSchedule.CourseSchedule;
import courseSchedule.CourseScheduleController;
import dashboard.Dashboard;
import dashboard.DashboardController;
import grades.Grades;
import grades.GradesController;
import gradesPlot.GradesPlot;
import gradesPlot.GradesPlotController;
import javafx.application.Application;
import javafx.embed.swing.JFXPanel;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import planner.Planner.VIEW_INDEX;
import termCalendar.TermCalendar;
import termCalendar.TermCalendarController;

public class Main extends Application {

	public static Driver driver;

	public static String saveDir = "res";
	public static String backupDir = "res//backup";
	public static String saveExtension = ".pro";

	public static String logFile = "res//log.txt";
	public static Logger logger;

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

		driver = new Driver();
		logger = new Logger(logFile);

		/* Dashboard */
		DashboardController dashboardController = new DashboardController(driver.active);
		Dashboard dashboard = new Dashboard(dashboardController);
		driver.planner.addView(dashboard);

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

		// The program should launch with the dashboard showing.
		driver.planner.viewPane.setCenter(driver.planner.views.get(VIEW_INDEX.DASHBOARD.val).mainLayout);

		// There was no term in progress at lastQuit, but there should be now.
		if (driver.active.termInProgress == null && driver.active.lastQuit.isBefore(LocalDate.now())) {

			driver.pc.updateTermInProgress();
		}

		// Refresh all views.
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
		driver.active.lastQuit = LocalDate.now();
		System.exit(0);
	}

}
