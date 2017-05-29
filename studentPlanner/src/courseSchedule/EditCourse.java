package courseSchedule;

import java.util.ArrayList;

import core.CalendarEvent;
import core.Course;
import core.Meeting;
import core.Planner;
import core.Term;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import termCalendar.TermCalendar;

public class EditCourse {

	public static Course currentlySelected;
	public static ChoiceBox<Term> chooseTerm;
	public static ObservableList<Course> termsCourses;
	public static ChoiceBox<Course> chooseCourse;
	public static TextField department;
	public static TextField code;
	public static TextField title;
	public static ColorPicker cPicker;
	public static Label meetings;
	public static ChoiceBox<Meeting> chooseMeeting;
	public static Label error;
	public static HBox ch;

	public static Course display() {

		currentlySelected = null;

		/* Basic window set-up */
		Stage window = new Stage();
		window.initModality(Modality.APPLICATION_MODAL);
		window.setTitle("Edit Course");
		window.getIcons().add(new Image(Planner.class.getResourceAsStream("icon.png")));

		ObservableList<Term> termChoices = FXCollections.observableArrayList(Planner.active.terms);
		chooseTerm = new ChoiceBox<>(termChoices);

		title = new TextField();
		title.setPromptText("Course Title");
		department = new TextField();
		code = new TextField();
		meetings = new Label();
		cPicker = new ColorPicker();
		error = new Label();
		ch = new HBox();
		chooseMeeting = new ChoiceBox<>();
		chooseMeeting.setVisible(false);
		ch.getChildren().add(chooseMeeting);

		chooseCourse = new ChoiceBox<>();
		termsCourses = FXCollections.observableArrayList();

		chooseTerm.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldIndex, Number newIndex) {

				/*
				 * Clear the course selection box and add those belonging to the
				 * newly selected term.
				 */
				termsCourses.clear();
				for (Course c : termChoices.get(newIndex.intValue()).courses) {
					termsCourses.add(c);
				}

				chooseCourse.setItems(termsCourses);

				/*
				 * If courses exist for the newly selected term, set the default
				 * selected course.
				 */
				if (termsCourses.size() > 0) {
					updateCurrentlySelected(termsCourses.get(0));
				}
			}
		});
		chooseTerm.setValue(Planner.active.currentlySelectedTerm);

		chooseCourse.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number old, Number current) {
				System.out.println("Changed course selection.");
				if (current.intValue() > -1) {
					updateCurrentlySelected(termsCourses.get(current.intValue()));
				}
			}
		});

		/* Add meetings */
		Button addMeeting = new Button("Add Meeting");
		addMeeting.setOnAction(e -> {

			Meeting m = AddMeeting.display();

			if (m != null) {

				boolean confirmAddMeetings = true;
				m.colour = currentlySelected.colour;
				ArrayList<Meeting> outerConflict = m.conflictsWithCourses(chooseTerm.getValue().courses);

				if (outerConflict.size() > 0) {
					confirmAddMeetings = HandleConflict.display(m, outerConflict);
				}

				if (confirmAddMeetings) {
					ArrayList<Meeting> allConflicts = new ArrayList<>();
					allConflicts.addAll(outerConflict);
					Meeting.deleteMeetings(allConflicts);
					currentlySelected.meetings.add(m);
					Planner.active.dayMeetings.put(m.dayOfWeek, m);
					CourseSchedule.setTodaysMeetings();
					updateChooseMeeting();
					meetings.setText("Weekly Meetings: " + currentlySelected.meetings.size());
					for (int i = 0; i < currentlySelected.meetings.size(); i++) {
						meetings.setText(meetings.getText() + "\n Meeting " + (i + 1) + ": "
								+ currentlySelected.meetings.get(i));
						window.setHeight(window.getHeight() + 10);
					}
				}
			}
		});

		/* Delete meetings */
		Button deleteMeeting = new Button("Delete Meeting");

		deleteMeeting.setOnAction(e -> {

			Meeting m = chooseMeeting.getValue();

			if (m != null) {

				currentlySelected.meetings.remove(m);
				for (Term t : Planner.active.courseTerms.get(currentlySelected)) {
					CourseSchedule.removeFromSchedule(m, t);
				}

				Planner.active.dayMeetings.del(m.dayOfWeek, m);
				CourseSchedule.setTodaysMeetings();
				updateChooseMeeting();
				meetings.setText("Weekly Meeting: " + currentlySelected.meetings.size());
				for (int i = 0; i < currentlySelected.meetings.size(); i++) {
					meetings.setText(
							meetings.getText() + "\n Meeting " + (i + 1) + ": " + currentlySelected.meetings.get(i));
					window.setHeight(window.getHeight() + 10);
				}
			}
		});

		/* Delete course */
		Button delete = new Button("Delete Course");

		delete.setOnAction(e -> {
			/* If deleting the currently selected course is confirmed */
			if (DeleteCourse.display(currentlySelected)) {

				for (Meeting m : currentlySelected.meetings) {
					for (Term t : Planner.active.courseTerms.get(currentlySelected)) {
						CourseSchedule.removeFromSchedule(m, t);
					}
					Planner.active.dayMeetings.del(m.dayOfWeek, m);
				}
				CourseSchedule.setTodaysMeetings();

				for (CalendarEvent d : currentlySelected.deliverables) {
					Planner.active.dateEvents.del(d.start.toLocalDate(), d);
				}

				/*
				 * If the deleted course belongs to the currently selected term,
				 * redraw schedule (if it had more one meeting) and calendar (if
				 * it had more than one deliverable).
				 */
				if (Planner.active.currentlySelectedTerm.courses.contains(currentlySelected)) {

					if (currentlySelected.meetings.size() > 0) {
						CourseSchedule.drawSchedule(Planner.active.currentlySelectedTerm,
								Planner.active.currentlySelectedDate);
					}

					if (currentlySelected.deliverables.size() > 0) {
						TermCalendar.redrawCalendars();
					}
				}

				Planner.active.deleteCourse(currentlySelected);

				/* If no more courses exist, close the "Edit Course" window */
				if (!Planner.active.coursesExist()) {

					currentlySelected = null;
					CourseSchedule.editCourse.setDisable(true);
					window.close();

				} else {

					/* Update course choices */
					termsCourses.clear();
					for (Course c : chooseTerm.getValue().courses) {
						termsCourses.add(c);
					}

					chooseCourse.setItems(termsCourses);

					if (termsCourses.size() > 0) {
						updateCurrentlySelected(termsCourses.get(0));
					}
				}
			}
		});

		Button confirm = new Button("Confirm changes");
		confirm.setOnAction(e -> {
			if (confirmChanges()) {
				window.close();
			}
		});

		VBox layout = new VBox(20);
		layout.getChildren().addAll(chooseTerm, chooseCourse, department, code, title, cPicker, meetings, ch,
				addMeeting, deleteMeeting, delete, confirm, error);
		Scene scene = new Scene(layout);
		window.setScene(scene);
		window.showAndWait();
		return currentlySelected;
	}

	private static void updateCurrentlySelected(Course c) {

		chooseCourse.setValue(termsCourses.get(0));
		currentlySelected = c;
		updateChooseMeeting();
		department.setText(currentlySelected.departmentID);
		code.setText("" + currentlySelected.code);
		title.setText(currentlySelected.name);
		cPicker.setValue(Color.web(currentlySelected.colour));
		meetings.setText("Weekly Meetings: " + currentlySelected.meetings.size());
	}

	private static void updateChooseMeeting() {

		ch.getChildren().remove(chooseMeeting);
		ObservableList<Meeting> chooseMeet = FXCollections.observableArrayList(currentlySelected.meetings);
		chooseMeeting = new ChoiceBox<>(chooseMeet);
		ch.getChildren().add(chooseMeeting);
		if (chooseMeet.size() > 0) {
			chooseMeeting.setValue(chooseMeet.get(0));
		}
	}

	private static boolean confirmChanges() {

		try {
			currentlySelected.departmentID = department.getText();
			currentlySelected.code = Integer.parseInt(code.getText());
			currentlySelected.name = title.getText();
			currentlySelected.colour = Planner.colorToHex(cPicker.getValue());
			return true;
		} catch (NumberFormatException e) {
			error.setText("Invalid course code. Cannot save changes.");
			error.setTextFill(Color.RED);
			return false;
		}
	}
}
