package views;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Observable;
import java.util.Observer;

import controllers.CourseScheduleController;
import core.Style;
import core.Course;
import core.Meeting;
import core.Planner;
import core.Driver;
import core.Profile;
import core.Term;
import core.Time;
import core.View;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import utility.Pretty;

/**
 * The Class CourseSchedule.
 */
public class CourseSchedule extends View implements Observer {

	/* The Planner this view belongs to. */
	public Planner planner;

	/* Observable & Controller */
	public Observable observable;
	public CourseScheduleController controller;

	/* GUI elements */
	public Label todaysMeetings;
	public GridPane scheduleGrid;
	public Label[] daysOfTheWeek = { new Label("Monday"), new Label("Tuesday"), new Label("Wednesday"),
			new Label("Thursday"), new Label("Friday"), new Label("Saturday"), new Label("Sunday") };
	public Label[] times;
	public Button[][] meetingButtons;
	public CheckBox showCurrentWeek;
	public DatePicker selectWeek;

	/* Dependent on selected Term's parameters */
	public int daysInSchedule;
	public int timesInSchedule;
	public int timesOffset;

	/**
	 * Instantiates a new Course Schedule.
	 *
	 * @param planner
	 *            the planner
	 * @param observable
	 *            the observable
	 * @param controller
	 *            the controller
	 */
	public CourseSchedule(Planner planner, Observable observable, CourseScheduleController controller) {

		this.planner = planner;

		this.observable = observable;
		observable.addObserver(this);

		this.controller = controller;
		controller.schedule = this;

		this.mainLayout = initLayout();
		this.controller.setCurrentlySelectedDate(Driver.t.current.toLocalDate());
	}

	/**
	 * Initializes the Course Schedule.
	 *
	 * @return the border pane
	 */
	public BorderPane initLayout() {

		/* GridPane containing the entire Course Schedule view */
		BorderPane csbp = new BorderPane();

		this.scheduleGrid = new GridPane();

		/* Initialize time labels along the axis of the schedule. */
		this.times = new Label[48];
		int hour = 0;
		for (int i = 0; i < 48; i += 2) {
			times[i] = new Label(hour + ":00");
			times[i + 1] = new Label(hour + ":30");
			hour++;
		}

		Label title = new Label("Course Schedule");
		Style.setTitleStyle(title);

		todaysMeetings = new Label("Today's Meetings:");
		setTodaysMeetings();

		/* Select week */

		/*
		 * Initially, the present week is shown when the user starts the
		 * application
		 */
		showCurrentWeek = new CheckBox("Show current week");
		showCurrentWeek.setSelected(true);

		selectWeek = new DatePicker();
		selectWeek.valueProperty().addListener(new ChangeListener<LocalDate>() {
			@Override
			public void changed(ObservableValue<? extends LocalDate> observable, LocalDate oldDate, LocalDate newDate) {
				controller.updateWeekSelected(newDate);
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
				controller.showCurrentWeek(newVal);
			}
		});

		HBox headerLayout = new HBox(20);
		headerLayout.getChildren().addAll(title, showCurrentWeek, selectWeek);

		HBox optionsLayout = new HBox(20);
		optionsLayout.getChildren().addAll(todaysMeetings);

		csbp.setTop(headerLayout);
		csbp.setCenter(scheduleGrid);
		csbp.setBottom(optionsLayout);

		return csbp;
	}

	/**
	 * Sets the todays meetings.
	 */
	public void setTodaysMeetings() {
		String desc = "";
		// PriorityQueue<Meeting> td =
		// Planner.active.dayMeetings.get(Planner.t.current.getDayOfWeek().getValue()
		// - 1);
		// if (td != null && td.size() > 0) {
		// for (Meeting m : td) {
		// desc += m.toString() + "\n";
		// }
		// desc = desc.substring(0, desc.lastIndexOf("\n"));
		// }
		todaysMeetings.setText("Today's Meetings:\n" + desc);
	}

	/**
	 * Draw schedule.
	 *
	 * @param d
	 *            the d
	 */
	public void drawSchedule(LocalDate d) {

		drawSchedule(null, d);
	}

	/**
	 * Draw schedule. TODO: Buttons on schedule should have functionality.
	 *
	 * @param term
	 *            the term
	 * @param d
	 *            the d
	 */
	public void drawSchedule(Term term, LocalDate d) {

		Driver.t.update();
		scheduleGrid.getChildren().clear();

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

			Label dayLabel = new Label(
					daysOfTheWeek[i].getText() + "\n" + firstOfWeek.plusDays(i).getMonth().toString().substring(0, 1)
							+ firstOfWeek.plusDays(i).getMonth().toString().substring(1).toLowerCase() + " "
							+ firstOfWeek.plusDays(i).getDayOfMonth()
							+ Pretty.getDateEnding(Integer.toString(firstOfWeek.plusDays(i).getDayOfMonth())));

			/*
			 * The current day is italicized in blue
			 */
			if (firstOfWeek.plusDays(i).isEqual(Driver.t.current.toLocalDate())) {

				dayLabel.setStyle(dayLabel.getStyle() + "-fx-font-family: Verdana;" + "-fx-font-style: italic;"
						+ "-fx-text-fill: #" + Style.colorToHex(Style.appBlue) + ";");
			}

			scheduleGrid.add(dayLabel, i + 1, 0);
			GridPane.setHalignment(dayLabel, HPos.CENTER);
		}

		/*
		 * Times along the top of the course schedule.
		 */
		int p = 0;
		for (int j = timesOffset; j < timesInSchedule + timesOffset; j++) {

			scheduleGrid.add(times[j], 0, p + 1);
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
				if (Driver.t.current.isAfter(cell)) {
					meetingButtons[i][j].setStyle(meetingButtons[i][j].getStyle() + "-fx-background-color: #"
							+ Style.colorToHex(Style.appGrey) + ";");
				} else {
					meetingButtons[i][j].setStyle(meetingButtons[i][j].getStyle() + "-fx-background-color: #fff;");
				}

				if (i == 0 && j == 0) {
					meetingButtons[i][j].setBorder(new Border(Style.fullBorderStroke));
				} else if (i == 0) {
					meetingButtons[i][j].setBorder(new Border(Style.noTopBorderStroke));
				} else if (j == 0) {
					meetingButtons[i][j].setBorder(new Border(Style.noLeftBorderStroke));
				} else {
					meetingButtons[i][j].setBorder(new Border(Style.noTopLeftBorderStroke));
				}

				scheduleGrid.add(meetingButtons[i][j], i + 1, j + 1);
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
	private void addToSchedule(Course course, Meeting meeting, Term term) {

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
			 * Border styling for all cells except for last.
			 */
			if (i + 30 < length) {

				/* Border styling for cell in top left corner. */
				if ((mDay - 1) == 0 && (timeDist + cellNumber) == 0) {
					mButton.setBorder(new Border(Style.noBottomBorderStroke));
				}
				/*
				 * Border styling for cells along far left column (except for
				 * top left).
				 */
				else if ((mDay - 1) == 0) {
					mButton.setBorder(new Border(Style.noTopBottomBorderStroke));
				}
				/* Border styling for cells along top row. */
				else if ((timeDist + cellNumber) == 0) {
					mButton.setBorder(new Border(Style.noBottomLeftBorderStroke));
				}
				/* Border styling for all other cells. */
				else {
					mButton.setBorder(new Border(Style.noTopBottomLeftBorderStroke));
				}
			}

			/* Background styling for meeting cells */
			mButton.setStyle(
					mButton.getStyle() + "-fx-background-color: #" + course.colour + "; -fx-background-radius: 0.0;");

			/* Define tooltip when meeting is hovered over. */
			Tooltip tp = new Tooltip(course.name + "\n" + meeting.location);
			mButton.setTooltip(tp);

			cellNumber++;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	@Override
	public void update(Observable arg0, Object arg1) {
		System.out.println("CourseSchedule receiving notification..");
		if (arg0 instanceof Profile) {
			drawSchedule(((Profile) arg0).currentlySelectedTerm, ((Profile) arg0).currentlySelectedDate);

			/*
			 * TODO: Needs to actually test if the selected date is within the
			 * current week.
			 */
			if (!((Profile) arg0).currentlySelectedDate.isEqual(Driver.t.current.toLocalDate())) {
				showCurrentWeek.selectedProperty().set(false);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see core.View#refresh()
	 */
	@Override
	public void refresh() {
		update(this.observable, null);
	}
}