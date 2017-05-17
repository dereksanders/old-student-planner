package courseSchedule;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
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
import utility.Pretty;

public class CourseSchedule {

	public static Label title;
	public static Button addCourse;
	public static Button editCourse;
	public static Button editTerm;
	public static Label todaysMeetings;
	public static GridPane scheduleLayout;
	public static Label[] daysOfTheWeek = { new Label("Monday"), new Label("Tuesday"), new Label("Wednesday"),
			new Label("Thursday"), new Label("Friday"), new Label("Saturday"), new Label("Sunday") };
	public static Label[] times;
	public static Button[][] meetingButtons;
	public static int daysInSchedule;
	public static int timesInSchedule;
	public static int timesOffset;

	public static BorderPane init() {

		BorderPane csbp = new BorderPane();

		times = new Label[48];
		int hour = 0;
		for (int i = 0; i < 48; i += 2) {
			times[i] = new Label(hour + ":00");
			times[i + 1] = new Label(hour + ":30");
			hour++;
		}

		Label time = new Label(Planner.t.current.format(DateTimeFormatter.ISO_LOCAL_TIME));

		scheduleLayout = new GridPane();
		title = new Label("Course Schedule");
		title.setTextFill(Planner.appBlue);
		title.setFont(Planner.h1);

		todaysMeetings = new Label("Today's Meetings:");
		setTodaysMeetings();

		/* SELECT WEEK */

		CheckBox showCurrentWeek = new CheckBox("Show current week");
		showCurrentWeek.setSelected(true);
		DatePicker selectWeek = new DatePicker();
		selectWeek.valueProperty().addListener(new ChangeListener<LocalDate>() {
			@Override
			public void changed(ObservableValue<? extends LocalDate> observable, LocalDate oldDate, LocalDate newDate) {
				Planner.currentlySelectedDate = newDate;
				if (Planner.findTerm(Planner.currentlySelectedDate) != null) {
					Planner.currentlySelectedTerm = Planner.findTerm(Planner.currentlySelectedDate);
					CourseSchedule.drawSchedule(Planner.findTerm(Planner.currentlySelectedDate),
							Planner.currentlySelectedDate);
					TermCalendar.redrawCalendars();
				} else {
					CourseSchedule.drawSchedule(Planner.currentlySelectedDate);
				}
			}
		});
		selectWeek.setVisible(false);
		showCurrentWeek.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldVal, Boolean newVal) {
				if (newVal) {
					selectWeek.setVisible(false);
					Planner.currentlySelectedDate = Planner.t.current.toLocalDate();
					selectWeek.setValue(Planner.currentlySelectedDate);
					if (Planner.findTerm(Planner.currentlySelectedDate) != null) {
						Planner.currentlySelectedTerm = Planner.findTerm(Planner.currentlySelectedDate);
						CourseSchedule.drawSchedule(Planner.findTerm(Planner.currentlySelectedDate),
								Planner.currentlySelectedDate);
						TermCalendar.redrawCalendars();
					} else {
						CourseSchedule.drawSchedule(Planner.currentlySelectedDate);
					}
				} else {
					selectWeek.setVisible(true);
					selectWeek.requestFocus();
				}
			}
		});

		/* ADD COURSE */

		addCourse = new Button("Add Course");
		if (Planner.terms.size() == 0) {
			addCourse.setDisable(true);
		}
		addCourse.setOnAction(e -> {
			Course add = AddCourse.display();
			if (add != null) {
				courseAddStuff(add);
				Planner.saveProfile(Planner.activeProfile);
			}
		});

		/* EDIT COURSE */

		editCourse = new Button("Edit Course");
		if (Planner.courses.size() == 0) {
			editCourse.setDisable(true);
		}
		editCourse.setOnAction(e -> {
			Course edited = EditCourse.display();
			if (edited != null) {

				for (Meeting m : edited.meetings) {
					for (Term t : edited.terms) {
						t.updateParams(m);
					}
				}

				if (Planner.findTerm(Planner.currentlySelectedDate) != null) {
					CourseSchedule.drawSchedule(Planner.findTerm(Planner.currentlySelectedDate),
							Planner.currentlySelectedDate);
				} else {
					CourseSchedule.drawSchedule(Planner.currentlySelectedDate);
				}

				TermCalendar.redrawCalendars();
			}
			Planner.saveProfile(Planner.activeProfile);
		});

		/* EDIT TERM */

		editTerm = new Button("Edit Term");
		if (Planner.terms.size() == 0) {
			editTerm.setDisable(true);
		}
		editTerm.setOnAction(e -> {
			EditTerm.display();
		});

		/* ADD TERM */

		Button addTerm = new Button("Add Term");
		addTerm.setOnAction(e -> {
			Term add = AddTerm.display();
			if (add != null) {
				termAddStuff(add);
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

	public static void courseAddStuff(Course add) {
		for (Term t : add.terms) {
			Planner.termCourses.put(t, add);
		}
		Planner.courses.add(add);
		Planner.courseColors.put(Color.web(add.colour), add);
		for (Meeting m : add.meetings) {
			for (Term t : add.terms) {
				t.updateParams(m);
			}
			if (Planner.findTerm(Planner.currentlySelectedDate) != null) {
				CourseSchedule.drawSchedule(Planner.findTerm(Planner.currentlySelectedDate),
						Planner.currentlySelectedDate);
			} else {
				CourseSchedule.drawSchedule(Planner.currentlySelectedDate);
			}
			Planner.dayMeetings.put(m.dayOfWeek, m);
			CourseSchedule.setTodaysMeetings();
		}
		if (Planner.courses.size() > 0) {
			editCourse.setDisable(false);
		}
	}

	public static void termAddStuff(Term add) {
		Planner.terms.add(add);
		Planner.currentlySelectedTerm = add;
		if (Planner.terms.size() == 1) {
			addCourse.setDisable(false);
			editTerm.setDisable(false);
			Planner.initTermCalendar();
		}
	}

	public static void setTodaysMeetings() {
		String desc = "";
		PriorityQueue<Meeting> td = Planner.dayMeetings.get(Planner.t.current.getDayOfWeek().toString());
		if (td != null && td.size() > 0) {
			for (Meeting m : td) {
				desc += m.toString() + "\n";
			}
			desc = desc.substring(0, desc.lastIndexOf("\n"));
		}
		todaysMeetings.setText("Today's Meetings:\n" + desc);
	}

	public static void drawSchedule(LocalDate d) {

		drawSchedule(null, d);
	}

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

		/* Finds the first day of the week to be displayed. */
		LocalDate firstOfWeek = LocalDate.of(d.getYear(), d.getMonthValue(), d.getDayOfMonth())
				.minusDays(dayOfWeek - 1);

		for (int i = 0; i < daysInSchedule; i++) {

			Label dayLabel = new Label(daysOfTheWeek[i].getText() + "\n" + firstOfWeek.plusDays(i).getDayOfMonth()
					+ Pretty.getDateEnding(Integer.toString(firstOfWeek.plusDays(i).getDayOfMonth())));

			/* The current day is italicized in blue */
			if (firstOfWeek.plusDays(i).isEqual(Planner.t.current.toLocalDate())) {

				dayLabel.setFont(Font.font("Verdana", FontPosture.ITALIC, 14));
				dayLabel.setTextFill(Planner.appBlue);
			}

			scheduleLayout.add(dayLabel, i + 1, 0);
			GridPane.setHalignment(dayLabel, HPos.CENTER);
		}

		/* Times along the top of the course schedule. */
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
					meetingButtons[i][j].setBackground(
							new Background(new BackgroundFill(Planner.appGrey, CornerRadii.EMPTY, Insets.EMPTY)));
				} else {
					meetingButtons[i][j].setBackground(
							new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
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
			PriorityQueue<Course> coursesOnSchedule = Planner.termCourses.get(term);
			if (coursesOnSchedule != null) {
				for (Course c : coursesOnSchedule) {
					for (Meeting m : c.meetings) {
						addToSchedule(c, m, term);
					}
				}
			}
		}
	}

	private static void addToSchedule(Course c, Meeting m, Term term) {

		int mDay = m.dayOfWeekInt;

		int timeDist = Time.getDistance(new Time(term.minStart.getHour(), term.minStart.getMinute()),
				new Time(m.start.getHour(), m.start.getMinute()), 30);

		Time meetingStart = new Time(m.start.getHour(), m.start.getMinute());
		Time meetingEnd = new Time(m.end.getHour(), m.end.getMinute());

		int length = meetingEnd.compareTo(meetingStart);

		int p = 0;

		for (int i = 0; i < length; i += 30) {

			Button mButton = meetingButtons[mDay - 1][timeDist + p];

			if (i == 0) {

				mButton.setText(c.toString() + "\n" + m.meetingType);
				mButton.setFont(Planner.scheduleFont);

				/*
				 * Decide if text is white or black based on brightness of
				 * background colour.
				 */
				if (Color.web(c.colour).getBrightness() < 0.7) {
					mButton.setTextFill(Color.WHITE);
				} else {
					mButton.setTextFill(Color.BLACK);
				}
			}

			if (i + 30 < length) {

				if ((mDay - 1) == 0 && (timeDist + p) == 0) {

					mButton.setBorder(new Border(Planner.noBottomBorderStroke));

				} else if ((mDay - 1) == 0) {

					mButton.setBorder(new Border(Planner.noBottomBorderStroke));

				} else if ((timeDist + p) == 0) {

					mButton.setBorder(new Border(Planner.noBottomBorderStroke));

				} else {

					mButton.setBorder(new Border(Planner.noBottomBorderStroke));
				}
			}

			Background backg = new Background(new BackgroundFill(Color.web(c.colour), CornerRadii.EMPTY, Insets.EMPTY));
			mButton.setBackground(backg);

			Tooltip tp = new Tooltip(c.name + "\n" + m.location);
			mButton.setTooltip(tp);
			p++;
		}
	}

	public static void removeFromSchedule(Meeting m, Term term) {
		int mDay = m.dayOfWeekInt;

		int timeDist = Time.getDistance(new Time(term.minStart.getHour(), term.minStart.getMinute()),
				new Time(m.start.getHour(), m.start.getMinute()), 30);

		int length = m.end.compareTo(m.start);
		int p = 0;
		for (int i = 0; i < length; i += 30) {
			Button mButton = meetingButtons[mDay - 1][timeDist + p];
			if (timeDist + p == 0 & mDay == 0) {
				mButton.setBorder(new Border(Planner.fullBorderStroke));
			} else if (timeDist + p == 0) {
				mButton.setBorder(new Border(Planner.noTopBorderStroke));
			} else if (mDay == 0) {
				mButton.setBorder(new Border(Planner.noLeftBorderStroke));
			} else {
				mButton.setBorder(new Border(Planner.noTopLeftBorderStroke));
			}
			mButton.setText("");
			Background backg = new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY));
			mButton.setBackground(backg);
			p++;
		}

		if (term.maxDay == m.dayOfWeekInt) {
			term.resetParams();
		}
		if (term.minStart.equals(m.start)) {
			term.resetParams();
		}
		if (term.maxEnd.equals(m.end)) {
			term.resetParams();
		}

		if (Planner.findTerm(Planner.currentlySelectedDate) != null) {
			drawSchedule(Planner.findTerm(Planner.currentlySelectedDate), Planner.currentlySelectedDate);
		} else {
			drawSchedule(Planner.currentlySelectedDate);
		}
	}
}
