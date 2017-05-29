package core;

import java.io.File;
import java.time.LocalDate;
import courseSchedule.CourseSchedule;
import grades.Grades;
import gradesPlot.GradesPlot;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import termCalendar.TermCalendar;
import utility.IOManager;
import utility.JSONParser;

/**
 * The Class Planner.
 */
public class Planner extends Application {

	public static Scene scheduleAndCalendar;
	public static int scheduleAndCalendarWidth = 1575;
	public static int scheduleAndCalendarHeight = 775;

	public static int gradeEntryAndPlotWidth = 1575;
	public static int gradeEntryAndPlotHeight = 775;

	public static BorderPane tc;
	public static BorderPane cstc;

	public static String saveDir = "res";
	public static String backupDir = "res//backup";
	public static TimeDateThread t;
	public static int today;
	public static int todaysDate;
	public static Profile active;

	/* All styles of border strokes that are used in the application */
	public static BorderStroke fullBorderStroke = new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID,
			CornerRadii.EMPTY, BorderStroke.THIN);

	public static BorderStroke noRightBorderStroke = new BorderStroke(Color.BLACK, Color.BLACK, Color.BLACK,
			Color.BLACK, BorderStrokeStyle.SOLID, BorderStrokeStyle.NONE, BorderStrokeStyle.SOLID,
			BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderStroke.THIN, Insets.EMPTY);

	public static BorderStroke noLeftBorderStroke = new BorderStroke(Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK,
			BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, BorderStrokeStyle.NONE,
			CornerRadii.EMPTY, BorderStroke.THIN, Insets.EMPTY);

	public static BorderStroke noTopLeftBorderStroke = new BorderStroke(Color.BLACK, Color.BLACK, Color.BLACK,
			Color.BLACK, BorderStrokeStyle.NONE, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID,
			BorderStrokeStyle.NONE, CornerRadii.EMPTY, BorderStroke.THIN, Insets.EMPTY);

	public static BorderStroke noTopBorderStroke = new BorderStroke(Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK,
			BorderStrokeStyle.NONE, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID,
			CornerRadii.EMPTY, BorderStroke.THIN, Insets.EMPTY);

	public static BorderStroke noTopRightBorderStroke = new BorderStroke(Color.BLACK, Color.BLACK, Color.BLACK,
			Color.BLACK, BorderStrokeStyle.NONE, BorderStrokeStyle.NONE, BorderStrokeStyle.SOLID,
			BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderStroke.THIN, Insets.EMPTY);

	public static BorderStroke noTopRightLeftBorderStroke = new BorderStroke(Color.BLACK, Color.BLACK, Color.BLACK,
			Color.BLACK, BorderStrokeStyle.NONE, BorderStrokeStyle.NONE, BorderStrokeStyle.SOLID,
			BorderStrokeStyle.NONE, CornerRadii.EMPTY, BorderStroke.THIN, Insets.EMPTY);

	public static BorderStroke noTopBottomBorderStroke = new BorderStroke(Color.BLACK, Color.BLACK, Color.BLACK,
			Color.BLACK, BorderStrokeStyle.NONE, BorderStrokeStyle.SOLID, BorderStrokeStyle.NONE,
			BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderStroke.THIN, Insets.EMPTY);

	public static BorderStroke noTopBottomLeftBorderStroke = new BorderStroke(Color.BLACK, Color.BLACK, Color.BLACK,
			Color.BLACK, BorderStrokeStyle.NONE, BorderStrokeStyle.SOLID, BorderStrokeStyle.NONE,
			BorderStrokeStyle.NONE, CornerRadii.EMPTY, BorderStroke.THIN, Insets.EMPTY);

	public static BorderStroke noBottomBorderStroke = new BorderStroke(Color.BLACK, Color.BLACK, Color.BLACK,
			Color.BLACK, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, BorderStrokeStyle.NONE,
			BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderStroke.THIN, Insets.EMPTY);

	public static BorderStroke noBottomLeftBorderStroke = new BorderStroke(Color.BLACK, Color.BLACK, Color.BLACK,
			Color.BLACK, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, BorderStrokeStyle.NONE,
			BorderStrokeStyle.NONE, CornerRadii.EMPTY, BorderStroke.THIN, Insets.EMPTY);

	public static BorderStroke noRightLeftBorderStroke = new BorderStroke(Color.BLACK, Color.BLACK, Color.BLACK,
			Color.BLACK, BorderStrokeStyle.SOLID, BorderStrokeStyle.NONE, BorderStrokeStyle.SOLID,
			BorderStrokeStyle.NONE, CornerRadii.EMPTY, BorderStroke.THIN, Insets.EMPTY);

	// Application Colors
	public static ObservableList<Color> selectableColors;
	public static Color appGrey = Color.web("0xdddddd");
	public static Color appBlue = Color.web("0x6680e6");
	public static Color appRed = Color.web("0xff6666");
	public static Color appYellow = Color.web("0xffcc66");
	public static Color appGreen = Color.web("0x669966");

	// Application Fonts
	public static Font scheduleFont = new Font(14);
	public static Font h1 = new Font(24);
	public static Font b1 = new Font(14);

	/**
	 * The main method.
	 *
	 * @param args
	 *            the arguments
	 */
	public static void main(String[] args) {
		launch(args);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javafx.application.Application#start(javafx.stage.Stage)
	 */
	@Override
	public void start(Stage window) throws Exception {

		/* Initialize preset colours used in the application */
		selectableColors = FXCollections.observableArrayList();
		selectableColors.addAll(appBlue, appRed, appYellow, appGreen);

		initTimeThread();

		/*
		 * Find the last modified .json file. If it does not exist, returns
		 * null.
		 */
		File lastModified = IOManager.lastModifiedJSON(saveDir);

		/* If no profiles exist, create a new one. */
		if (lastModified == null) {
			active = new Profile("default");
			JSONParser.saveProfile(active);
		}
		/* Load the most recently modified profile. */
		else {
			active = JSONParser.loadProfile(lastModified);
		}

		/* Select today's date. */
		active.currentlySelectedDate = t.current.toLocalDate();

		/* Course Schedule & Term Calendar Layout */
		cstc = new BorderPane();
		HBox cstcOptions = new HBox(20);
		Button goToGrades = new Button("Grades");
		Planner.setButtonStyle(goToGrades);
		cstcOptions.getChildren().add(goToGrades);

		/* Initialize Course Schedule */
		BorderPane cs = CourseSchedule.init();
		CourseSchedule.drawSchedule(active.currentlySelectedTerm, active.currentlySelectedDate);
		CourseSchedule.setTodaysMeetings();

		cstc.setTop(cstcOptions);
		cstc.setCenter(cs);

		/* Initialize Course Schedule & Term Calendar Scene */
		scheduleAndCalendar = new Scene(cstc, scheduleAndCalendarWidth, scheduleAndCalendarHeight);
		scheduleAndCalendar.getStylesheets().add(getClass().getResource("core.css").toExternalForm());

		/*
		 * Only draw the Term Calendar if the selected Term is not null, which
		 * should only occur when the Profile has no Terms.
		 */
		if (active.currentlySelectedTerm != null) {
			System.out.println("Initializing Term Calendar..");
			initTermCalendar();
		}

		/* Grades & Grades Plot Layout */
		BorderPane ggpLayout = new BorderPane();
		BorderPane gradeEntry = Grades.init();
		BorderPane gradePlot = GradesPlot.init();

		HBox gradeOptions = new HBox(20);
		Button goToSchedule = new Button("Schedule");
		gradeOptions.getChildren().add(goToSchedule);

		ggpLayout.setTop(gradeOptions);
		ggpLayout.setLeft(gradeEntry);
		ggpLayout.setRight(gradePlot);

		/* Initialize Grades & Grades Plot Scene */
		Scene gradesAndPlot = new Scene(ggpLayout, gradeEntryAndPlotWidth, gradeEntryAndPlotHeight);
		gradesAndPlot.getStylesheets().add(getClass().getResource("core.css").toExternalForm());

		/*
		 * Scene select options. These are placed below the scene declarations
		 * as they need access to them.
		 */
		goToSchedule.setOnAction(e -> {
			window.setScene(scheduleAndCalendar);
		});

		goToGrades.setOnAction(e -> {
			window.setScene(gradesAndPlot);
		});

		/* Application window */
		window.setTitle("Student Planner");
		window.getIcons().add(new Image(getClass().getResourceAsStream("icon.png")));
		window.setScene(scheduleAndCalendar);
		window.setResizable(true);
		window.show();
	}

	/**
	 * Inits the term calendar.
	 */
	public static void initTermCalendar() {

		if (tc == null) {
			System.out.println("Term Calendar is null. Constructing Term Calendar..");
			tc = TermCalendar.init();
			TermCalendar.redrawCalendars();
			cstc.setRight(tc);
		}
	}

	/**
	 * Inits the time thread.
	 */
	public void initTimeThread() {

		t = new TimeDateThread();
		t.update();
		today = t.current.getDayOfWeek().getValue();
		todaysDate = t.current.getDayOfMonth();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javafx.application.Application#stop()
	 */
	@Override
	public void stop() throws Exception {
		System.out.println("Saving and exiting..");
		JSONParser.saveProfile(Planner.active);
	}

	/**
	 * Find the term for the specified date.
	 *
	 * @param date
	 *            the date
	 * @return the term
	 */
	public static Term findTerm(LocalDate date) {
		Term result = null;
		for (Term t : Planner.active.terms) {
			if ((t.start.isBefore(date) || t.start.equals(date)) && (t.end.isAfter(date) || t.end.equals(date))) {
				result = t;
				break;
			}
		}
		return result;
	}

	/**
	 * Gets the next color in the sequence of application colors.
	 *
	 * @return the next color
	 */
	public static Color getNextColor() {

		for (Color c : selectableColors) {
			if (!Planner.active.courseColors.contains(c)) {
				return c;
			}
		}
		return Color.WHITE;
	}

	/**
	 * Color to hex.
	 *
	 * @param color
	 *            the color
	 * @return the string
	 */
	public static String colorToHex(Color color) {
		String hex1;
		String hex2;

		hex1 = Integer.toHexString(color.hashCode()).toUpperCase();

		switch (hex1.length()) {
		case 2:
			hex2 = "000000";
			break;
		case 3:
			hex2 = String.format("00000%s", hex1.substring(0, 1));
			break;
		case 4:
			hex2 = String.format("0000%s", hex1.substring(0, 2));
			break;
		case 5:
			hex2 = String.format("000%s", hex1.substring(0, 3));
			break;
		case 6:
			hex2 = String.format("00%s", hex1.substring(0, 4));
			break;
		case 7:
			hex2 = String.format("0%s", hex1.substring(0, 5));
			break;
		default:
			hex2 = hex1.substring(0, 6);
		}
		return hex2;
	}

	public static void setButtonStyle(Button button) {
		button.setStyle("-fx-font-size: 11.0pt;" + "-fx-text-fill: #" + Planner.colorToHex(Planner.appBlue) + ";"
				+ "-fx-font-weight: bold;"
				+ "-fx-background-color: linear-gradient(from 25.0% 25.0% to 100.0% 100.0%, #fff, #ddd);"
				+ "-fx-border-color: #ccc");
	}

	public static void setTitleStyle(Label title) {
		title.setStyle("-fx-font-size: 18.0pt;" + "-fx-text-fill: #" + Planner.colorToHex(Planner.appBlue) + ";"
				+ "-fx-font-weight: bold;");
	}
}