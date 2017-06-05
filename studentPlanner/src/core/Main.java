package core;

import controllers.CourseScheduleController;
import controllers.TermCalendarController;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import views.CourseSchedule;
import views.TermCalendar;

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
		CourseSchedule schedule = new CourseSchedule(driver.planner, driver.active, scheduleController);
		driver.planner.addView(schedule);

		/* TODO: Instantiate Calendar View & Controller */
		TermCalendarController calendarController = new TermCalendarController(driver.active, driver.planner);
		TermCalendar calendar = new TermCalendar(driver.planner, driver.active, calendarController);
		driver.planner.addView(calendar);

		/* TODO: Instantiate Grades View & Controller */

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
		// JSONParser.saveProfile(driver.active);
	}

}
