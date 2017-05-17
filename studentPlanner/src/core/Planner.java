package core;

import java.time.LocalDate;
import java.util.HashMap;

import courseSchedule.CourseSchedule;
import grades.Grades;
import grades.GradesPlot;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import json.Parser;
import termCalendar.TermCalendar;
import utility.GenericLinkedHashTable;

public class Planner extends Application {

	public static int width = 1753;
	public static int height = 925;

	public static BorderPane scbp;
	public static BorderPane tcbp;
	public static BorderPane csbp;
	public static BorderPane gbp;

	// Student profile & define data structures
	public static String saveDir = "res";
	public static TimeDateThread t;
	public static int today;
	public static int todaysDate;
	public static Term currentlySelectedTerm;
	public static LocalDate currentlySelectedDate;
	public static ObservableList<Term> terms;
	public static ObservableList<Course> courses;
	public static HashMap<Color, Course> courseColors;
	public static String[] save;
	public static String activeProfile;
	public static int showWithinThreshold = 14;

	public static GenericLinkedHashTable<Term, Course> termCourses;
	public static GenericLinkedHashTable<String, Meeting> dayMeetings;
	public static GenericLinkedHashTable<LocalDate, CalendarEvent> dateEvents;

	// Border Styles
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

	public static BorderStroke noBottomBorderStroke = new BorderStroke(Color.BLACK, Color.BLACK, Color.BLACK,
			Color.BLACK, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, BorderStrokeStyle.NONE,
			BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderStroke.THIN, Insets.EMPTY);

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

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage window) throws Exception {

		// Initialize data structures and load student profile
		selectableColors = FXCollections.observableArrayList();
		selectableColors.addAll(appBlue, appRed, appYellow, appGreen);
		terms = FXCollections.observableArrayList();
		courses = FXCollections.observableArrayList();
		courseColors = new HashMap<>();
		dayMeetings = new GenericLinkedHashTable<>(7, false);
		termCourses = new GenericLinkedHashTable<>(100, false);
		dateEvents = new GenericLinkedHashTable<>(366, true);
		activeProfile = "default";

		initTimeThread();

		scbp = new BorderPane();

		BorderPane csbp = CourseSchedule.init();

		Parser.loadAll(activeProfile);

		currentlySelectedTerm = findTerm(currentlySelectedDate);

		if (currentlySelectedTerm != null) {
			CourseSchedule.drawSchedule(currentlySelectedTerm, currentlySelectedDate);
			TermCalendar.redrawCalendars();
		} else {
			CourseSchedule.drawSchedule(currentlySelectedDate);
		}

		HBox options = new HBox(20);
		Button goToGrades = new Button("Grades");
		options.getChildren().add(goToGrades);

		scbp.setTop(options);
		scbp.setCenter(csbp);

		BorderPane gradeEntry = Grades.init();
		BorderPane gradePlot = GradesPlot.init();

		BorderPane gbp = new BorderPane();
		gbp.setLeft(gradeEntry);
		gbp.setRight(gradePlot);

		Scene scheduleAndCalendar = new Scene(scbp, width, height);
		Scene gradeEntryAndPlot = new Scene(gbp, width, height);

		goToGrades.setOnAction(e -> {
			window.setScene(gradeEntryAndPlot);
		});

		/* Application window */
		window.setTitle("Student Planner");
		// TODO: ADD WINDOW ICONS
		// window.getIcons().add(new Image("res//s.png"));
		window.setScene(scheduleAndCalendar);
		window.setResizable(true);
		window.show();
	}

	public static void initTermCalendar() {
		if (tcbp == null) {
			tcbp = TermCalendar.init();
			TermCalendar.redrawCalendars();
			scbp.setRight(tcbp);
		}
	}

	public void initTimeThread() {

		t = new TimeDateThread();
		t.update();
		today = t.current.getDayOfWeek().getValue();
		todaysDate = t.current.getDayOfMonth();
		currentlySelectedDate = t.current.toLocalDate();
	}

	@Override
	public void stop() throws Exception {
		System.out.println("Saving and exiting..");
		saveProfile(activeProfile);
	}

	public static Term findTerm(LocalDate d) {
		Term result = null;
		for (Term t : terms) {
			if ((t.start.isBefore(d) || t.start.equals(d)) && (t.end.isAfter(d) || t.end.equals(d))) {
				result = t;
				break;
			}
		}
		return result;
	}

	public static Color getNextColor() {
		for (Color c : selectableColors) {
			if (!courseColors.containsKey(c)) {
				return c;
			}
		}
		return Color.WHITE;
	}

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

	public static void saveProfile(String profileName) {

		for (Course c : Planner.courses) {

			Parser.saveJSONObject(profileName, c);
		}
	}

	public static void loadProfile(String profileName) {

		Parser.loadAll(profileName);
	}

}