package courseSchedule;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.PriorityQueue;

import core.Course;
import core.Meeting;
import core.Planner;
import core.Term;
import core.Time;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import termCalendar.TermCalendar;
import utility.JSONParser;
import utility.Pretty;

/**
 * The Class CourseSchedule.
 */
public class CourseSchedule {

	public static Label todaysMeetings;
	public static Button editCourse;

	/* Visual schedule elements */
	public static GridPane scheduleLayout;
	public static Label[] daysOfTheWeek = { new Label("Monday"), new Label("Tuesday"), new Label("Wednesday"),
			new Label("Thursday"), new Label("Friday"), new Label("Saturday"), new Label("Sunday") };
	public static Label[] times;
	public static Button[][] meetingButtons;

	/* Dependent on selected Term's parameters */
	public static int daysInSchedule;
	public static int timesInSchedule;
	public static int timesOffset;

	/**
	 * Initializes the Course Schedule.
	 *
	 * @return the border pane
	 */
	public static BorderPane init() {

		/* GridPane containing the entire Course Schedule view */
		BorderPane csbp = new BorderPane();

		/* GridPane containing the main part of the Course Schedule */
		scheduleLayout = new GridPane();

		/* Initialize time labels along the axis of the schedule. */
		times = new Label[48];
		int hour = 0;
		for (int i = 0; i < 48; i += 2) {
			times[i] = new Label(hour + ":00");
			times[i + 1] = new Label(hour + ":30");
			hour++;
		}

		/*
		 * TODO: This label needs to be updated every time the time thread is.
		 */
		Label time = new Label(Pretty.prettyShortDate(Planner.t.current.toLocalDate()));

		Label title = new Label("Course Schedule");
		Planner.setTitleStyle(title);

		todaysMeetings = new Label("Today's Meetings:");
		setTodaysMeetings();

		/* Select week */

		/*
		 * Initially, the present week is shown when the user starts the
		 * application
		 */
		CheckBox showCurrentWeek = new CheckBox("Show current week");
		showCurrentWeek.setSelected(true);

		DatePicker selectWeek = new DatePicker();
		selectWeek.valueProperty().addListener(new ChangeListener<LocalDate>() {
			@Override
			public void changed(ObservableValue<? extends LocalDate> observable, LocalDate oldDate, LocalDate newDate) {
				updateWeekSelected(newDate);
			}
		});

		/*
		 * Since the checkbox to show the current week is active on startup,
		 * this should initially be disabled.
		 */
		selectWeek.setVisible(false);

		showCurrentWeek.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldVal, Boolean newVal) {

				/*
				 * Actions to perform when the checkbox to show the current week
				 * is toggled.
				 */

				/* If the new value is to show the current week. */
				if (newVal) {

					/* Disable selecting a different week. */
					selectWeek.setVisible(false);

					LocalDate present = Planner.t.current.toLocalDate();
					selectWeek.setValue(present);
					updateWeekSelected(present);
				}

				/* If the new value is to allow choosing a different week. */
				else {
					selectWeek.setVisible(true);
					selectWeek.requestFocus();
				}
			}
		});

		/* Edit course */
		Button editCourse = new Button("Edit Course");
		Planner.setButtonStyle(editCourse);

		if (!Planner.active.coursesExist()) {

			/*
			 * If no courses exist, the "Edit Course" button should be disabled.
			 */
			editCourse.setDisable(true);
		}

		editCourse.setOnAction(e -> {

			/* Open Edit Course window */
			Course edited = EditCourse.display();

			/* If the user has confirmed changes to an existing course */
			if (edited != null) {

				Planner.active.resetTermParams(edited.start, edited.end);

				/*
				 * If the edited course belongs to the currently selected term,
				 * redraw schedule and calendar.
				 */
				if (Planner.active.currentlySelectedTerm.courses.contains(edited)) {

					updateWeekSelected(Planner.active.currentlySelectedDate);
				}

				/*
				 * TODO: What about CalendarEvents belonging to the edited
				 * course?
				 */
				TermCalendar.redrawCalendars();

				/* Save changes to Profile */
				JSONParser.saveProfile(Planner.active);
			}
		});

		/* Add course */
		Button addCourse = new Button("Add Course");
		Planner.setButtonStyle(addCourse);

		if (Planner.active.terms.size() == 0) {

			/*
			 * Only allow user to add courses if they have at least one term in
			 * their profile.
			 */
			addCourse.setDisable(true);
		}

		addCourse.setOnAction(e -> {

			/* Open Add Course window */
			Course addedCourse = AddCourse.display();

			/* If the user has defined a course to add. */
			if (addedCourse != null) {
				Planner.active.addCourse(addedCourse);

				/*
				 * Redraw calendar if added course is in selected term and it
				 * has meetings.
				 */
				if (Planner.active.courseTerms.get(addedCourse).contains(Planner.active.currentlySelectedTerm)
						&& addedCourse.meetings.size() > 0) {

					System.out.println("Redrawing schedule after course addition..");
					drawSchedule(Planner.active.currentlySelectedTerm, Planner.active.currentlySelectedDate);
				}

				/*
				 * Enable Edit Course button in case this is the first course
				 * added.
				 */
				editCourse.setDisable(false);
			}
		});

		/* Edit Term */
		Button editTerm = new Button("Edit Term");
		Planner.setButtonStyle(editTerm);

		if (Planner.active.terms.size() == 0) {

			/*
			 * If no terms exist, the "Edit Term" button should be disabled.
			 */
			editTerm.setDisable(true);
		}

		editTerm.setOnAction(e -> {

			/* Open "Edit Term" window */
			EditTerm.display();
		});

		/* Add Term */
		Button addTerm = new Button("Add Term");
		Planner.setButtonStyle(addTerm);

		addTerm.setOnAction(e -> {

			/* Open Add Term window */
			Term add = AddTerm.display();

			/* If the user has defined a Term to add. */
			if (add != null) {

				Planner.active.addTerm(add);

				/*
				 * If the profile previously had zero Terms:
				 * 
				 * 1) Enable the Edit Term and Add Course buttons.
				 * 
				 * 2) Select the new Term & its start date
				 * 
				 * 3) Initialize the Term Calendar.
				 */
				if (Planner.active.terms.size() == 1) {
					editTerm.setDisable(false);
					addCourse.setDisable(false);

					LocalDate selected = Planner.active.currentlySelectedDate;

					/* If the currently selected date is within the new Term. */
					if (((selected.isEqual(add.start) || selected.isAfter(add.start))
							&& (selected.isEqual(add.end) || selected.isBefore(add.end)))) {

						updateWeekSelected(selected);
					} else {

						updateWeekSelected(Planner.active.terms.get(0).start);
						showCurrentWeek.selectedProperty().set(false);
					}

					Planner.initTermCalendar();
				}
			}
		});

		HBox headerLayout = new HBox(20);
		headerLayout.getChildren().addAll(title, showCurrentWeek, selectWeek, time);

		HBox optionsLayout = new HBox(20);
		optionsLayout.getChildren().addAll(addTerm, editTerm, addCourse, editCourse, todaysMeetings);

		csbp.setTop(headerLayout);
		csbp.setCenter(scheduleLayout);
		csbp.setBottom(optionsLayout);

		return csbp;
	}

	/**
	 * Update week selected.
	 *
	 * @param newDate
	 *            the new date
	 */
	private static void updateWeekSelected(LocalDate newDate) {

		Planner.active.currentlySelectedDate = newDate;
		Term termOfNewDate = Planner.findTerm(newDate);

		/*
		 * If a term exists for the selected date, draw its schedule.
		 */
		if (termOfNewDate != null) {

			Planner.active.currentlySelectedTerm = termOfNewDate;
			CourseSchedule.drawSchedule(termOfNewDate, newDate);

			/*
			 * If a term exists, also initialize or draw its calendar.
			 */
			if (Planner.tc == null) {
				Planner.initTermCalendar();
			} else {
				TermCalendar.redrawCalendars();
			}
		}

		/*
		 * If no term exists, draw an empty schedule for that date.
		 */
		else {
			CourseSchedule.drawSchedule(newDate);

			/*
			 * TODO: Do we need to stop a previous term calendar from still
			 * being displayed?
			 */
		}
	}

	/**
	 * Sets the todays meetings.
	 */
	public static void setTodaysMeetings() {
		String desc = "";
		PriorityQueue<Meeting> td = Planner.active.dayMeetings.get(Planner.t.current.getDayOfWeek().toString());
		if (td != null && td.size() > 0) {
			for (Meeting m : td) {
				desc += m.toString() + "\n";
			}
			desc = desc.substring(0, desc.lastIndexOf("\n"));
		}
		todaysMeetings.setText("Today's Meetings:\n" + desc);
	}

	/**
	 * Draw schedule.
	 *
	 * @param d
	 *            the d
	 */
	public static void drawSchedule(LocalDate d) {

		drawSchedule(null, d);
	}

	/**
	 * Draw schedule.
	 *
	 * @param term
	 *            the term
	 * @param d
	 *            the d
	 */
	public static void drawSchedule(Term term, LocalDate d) {

		Planner.t.update();
		scheduleLayout.getChildren().clear();

		if (term != null) {

			/*
			 * The # of days in the schedule should be equal to the int value of
			 * dayOfTheWeek of the meeting latest in the week (Mon - Sun, 1 to
			 * 7)
			 */
			daysInSchedule = term.maxDay;

			timesInSchedule = Time.getDistance(new Time(term.minStart.getHour(), term.minStart.getMinute()),
					new Time(term.maxEnd.getHour(), term.maxEnd.getMinute()), 30);

			/*
			 * Calculates how many time labels we are to skip based on the
			 * earliest start time.
			 */
			timesOffset = Time.getDistance(new Time(0, 0), new Time(term.minStart.getHour(), term.minStart.getMinute()),
					30);

		} else {
			daysInSchedule = 5;
			timesInSchedule = Time.getDistance(new Time(8, 30), new Time(15, 00), 30);
			timesOffset = Time.getDistance(new Time(0, 0), new Time(8, 30), 30);
		}

		int dayOfWeek = d.getDayOfWeek().getValue();

		/*
		 * Finds the first day of the week to be displayed.
		 */
		LocalDate firstOfWeek = LocalDate.of(d.getYear(), d.getMonthValue(), d.getDayOfMonth())
				.minusDays(dayOfWeek - 1);

		for (int i = 0; i < daysInSchedule; i++) {

			Label dayLabel = new Label(daysOfTheWeek[i].getText() + "\n" + firstOfWeek.plusDays(i).getDayOfMonth()
					+ Pretty.getDateEnding(Integer.toString(firstOfWeek.plusDays(i).getDayOfMonth())));

			/*
			 * The current day is italicized in blue
			 */
			if (firstOfWeek.plusDays(i).isEqual(Planner.t.current.toLocalDate())) {

				dayLabel.setStyle(dayLabel.getStyle() + "-fx-font-family: Verdana;" + "-fx-font-style: italic;"
						+ "-fx-font-fill: " + Planner.colorToHex(Planner.appBlue) + ";");
			}

			scheduleLayout.add(dayLabel, i + 1, 0);
			GridPane.setHalignment(dayLabel, HPos.CENTER);
		}

		/*
		 * Times along the top of the course schedule.
		 */
		int p = 0;
		for (int j = timesOffset; j < timesInSchedule + timesOffset; j++) {

			scheduleLayout.add(times[j], 0, p + 1);
			GridPane.setValignment(times[j], VPos.TOP);
			p++;
		}

		meetingButtons = new Button[daysInSchedule][timesInSchedule];

		for (int i = 0; i < meetingButtons.length; i++) {

			for (int j = 0; j < meetingButtons[0].length; j++) {

				meetingButtons[i][j] = new Button();
				meetingButtons[i][j].setMinWidth(120);
				meetingButtons[i][j].setMaxWidth(120);
				meetingButtons[i][j].setMinHeight(48);
				meetingButtons[i][j].setMaxHeight(48);

				LocalDateTime cell = LocalDateTime.of(firstOfWeek.plusDays(i),
						LocalTime.of((j + timesOffset) / 2, ((j + timesOffset) % 2) * 30));

				/*
				 * Times past the current time will be grey, future times white.
				 */
				if (Planner.t.current.isAfter(cell)) {
					meetingButtons[i][j].setStyle(meetingButtons[i][j].getStyle() + "-fx-background-color: #"
							+ Planner.colorToHex(Planner.appGrey) + ";");
				} else {
					meetingButtons[i][j].setStyle(meetingButtons[i][j].getStyle() + "-fx-background-color: #fff;");
				}

				if (i == 0 && j == 0) {
					meetingButtons[i][j].setBorder(new Border(Planner.fullBorderStroke));
				} else if (i == 0) {
					meetingButtons[i][j].setBorder(new Border(Planner.noTopBorderStroke));
				} else if (j == 0) {
					meetingButtons[i][j].setBorder(new Border(Planner.noLeftBorderStroke));
				} else {
					meetingButtons[i][j].setBorder(new Border(Planner.noTopLeftBorderStroke));
				}

				scheduleLayout.add(meetingButtons[i][j], i + 1, j + 1);
			}
		}

		/* Add all of the selected term's courses to the schedule. */
		if (term != null) {
			for (Course c : term.courses) {
				for (Meeting m : c.meetings) {
					addToSchedule(c, m, term);
				}
			}
		}
	}

	/**
	 * Adds the meeting to the Course Schedule.
	 *
	 * @param course
	 *            the course
	 * @param meeting
	 *            the meeting
	 * @param term
	 *            the term
	 */
	private static void addToSchedule(Course course, Meeting meeting, Term term) {

		int mDay = meeting.dayOfWeekInt;

		/*
		 * Get the distance from the first time in the Course Schedule to the
		 * start time of the meeting.
		 */
		int timeDist = Time.getDistance(new Time(term.minStart.getHour(), term.minStart.getMinute()),
				new Time(meeting.start.getHour(), meeting.start.getMinute()), 30);

		/*
		 * Convert LocalDateTime meeting start & end to Time objects for easier
		 * comparison.
		 */
		Time meetingStart = new Time(meeting.start.getHour(), meeting.start.getMinute());
		Time meetingEnd = new Time(meeting.end.getHour(), meeting.end.getMinute());

		/* Determine how long the meeting is. */
		int length = meetingEnd.compareTo(meetingStart);

		/* Counter for each cell being added. */
		int cellNumber = 0;

		for (int i = 0; i < length; i += 30) {

			/* Find next cell of meeting. */
			Button mButton = meetingButtons[mDay - 1][timeDist + cellNumber];

			/*
			 * Styling for first cell of meeting which contains text describing
			 * it.
			 */
			if (i == 0) {

				mButton.setText(course.toString() + "\n" + meeting.meetingType);
				mButton.setStyle(mButton.getStyle() + "-fx-font-size: 10.5pt;");

				/*
				 * Decide if text is white or black based on brightness of
				 * background colour.
				 */
				if (Color.web(course.colour).getBrightness() < 0.7) {
					mButton.setStyle(mButton.getStyle() + "-fx-text-fill: #fff;");
				} else {
					mButton.setStyle(mButton.getStyle() + "-fx-text-fill: #000;");
				}
			}

			/*
			 * Border styling for all cells except for last. TODO: What about
			 * border styling for last cell? TODO: These are wrong.
			 */
			if (i + 30 < length) {

				/* Border styling for cell in top left corner. */
				if ((mDay - 1) == 0 && (timeDist + cellNumber) == 0) {
					mButton.setBorder(new Border(Planner.noBottomBorderStroke));
				}
				/*
				 * Border styling for cells along far left column (except for
				 * top left).
				 */
				else if ((mDay - 1) == 0) {
					mButton.setBorder(new Border(Planner.noTopBottomBorderStroke));
				}
				/* Border styling for cells along top row. */
				else if ((timeDist + cellNumber) == 0) {
					mButton.setBorder(new Border(Planner.noBottomLeftBorderStroke));
				}
				/* Border styling for all other cells. */
				else {
					mButton.setBorder(new Border(Planner.noTopBottomLeftBorderStroke));
				}
			}

			/* Background styling for meeting cells */
			mButton.setStyle(mButton.getStyle() + "-fx-background-color: #" + course.colour + ";");

			/* Define tooltip when meeting is hovered over. */
			Tooltip tp = new Tooltip(course.name + "\n" + meeting.location);
			mButton.setTooltip(tp);

			cellNumber++;
		}
	}

	/**
	 * Removes the meeting from the Course Schedule.
	 *
	 * @param meeting
	 *            the meeting
	 * @param term
	 *            the term
	 */
	public static void removeFromSchedule(Meeting meeting, Term term) {

		int mDay = meeting.dayOfWeekInt;

		/*
		 * Get the distance from the first time in the Course Schedule to the
		 * start time of the meeting.
		 */
		int timeDist = Time.getDistance(new Time(term.minStart.getHour(), term.minStart.getMinute()),
				new Time(meeting.start.getHour(), meeting.start.getMinute()), 30);

		int length = meeting.end.compareTo(meeting.start);
		int cellNumber = 0;

		/*
		 * For each cell occupied in the Course Schedule by the removed meeting.
		 */
		for (int i = 0; i < length; i += 30) {

			Button mButton = meetingButtons[mDay - 1][timeDist + cellNumber];

			/*
			 * Reset border to how it should be for an empty cell in the Course
			 * Schedule (based on position in the grid).
			 */

			/* Border styling for top left cell. */
			if (timeDist + cellNumber == 0 & mDay == 0) {
				mButton.setBorder(new Border(Planner.fullBorderStroke));
			}

			/* Border styling for cells in top row. */
			else if (timeDist + cellNumber == 0) {
				mButton.setBorder(new Border(Planner.noTopBorderStroke));
			}

			/* Border styling for cells in left column */
			else if (mDay == 0) {
				mButton.setBorder(new Border(Planner.noLeftBorderStroke));
			}

			/* Border styling for other cells. */
			else {
				mButton.setBorder(new Border(Planner.noTopLeftBorderStroke));
			}

			/* More default styling for empty cell */
			mButton.setText("");
			Background backg = new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY));
			mButton.setBackground(backg);

			cellNumber++;
		}

		/* Reset Term params if necessary. */
		if (term.maxDay == meeting.dayOfWeekInt) {
			term.resetParams();
		}
		if (term.minStart.equals(meeting.start)) {
			term.resetParams();
		}
		if (term.maxEnd.equals(meeting.end)) {
			term.resetParams();
		}

		/*
		 * Redraw schedule in case meeting removed is in the currently selected
		 * Term.
		 */
		if (term.equals(Planner.active.currentlySelectedTerm)) {
			updateWeekSelected(Planner.active.currentlySelectedDate);
		}
	}
}
