package courseSchedule;

import java.util.ArrayList;

import core.Course;
import core.Deliverable;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import termCalendar.TermCalendar;
import utility.IOManager;

public class EditCourse {

	public static Course currentlySelected;
	public static ChoiceBox<Term> chooseTerm;
	public static TextField department;
	public static TextField code;
	public static TextField title;
	public static ColorPicker cPicker;
	public static Label meetings;
	public static ChoiceBox<Meeting> chooseMet;
	public static Label error;
	public static HBox ch;

	public static Course display() {
		currentlySelected = null;
		Stage window = new Stage();
		window.initModality(Modality.APPLICATION_MODAL);
		window.setTitle("Edit Course");
		chooseTerm = new ChoiceBox<>(Planner.terms);
		department = new TextField();
		code = new TextField();
		title = new TextField();
		title.setPromptText("Course Title");
		meetings = new Label();
		cPicker = new ColorPicker();
		error = new Label();
		ch = new HBox();
		chooseMet = new ChoiceBox<>();
		chooseMet.setVisible(false);
		ch.getChildren().add(chooseMet);
		ChoiceBox<Course> chooseCourse = new ChoiceBox<>();
		ObservableList<Course> termsCourses = FXCollections.observableArrayList();
		chooseTerm.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldIndex, Number newIndex) {
				termsCourses.clear();
				if (Planner.termCourses.get(Planner.terms.get(newIndex.intValue())) != null) {
					for (Course c : Planner.termCourses.get(Planner.terms.get(newIndex.intValue()))) {
						termsCourses.add(c);
					}
				}
				chooseCourse.setItems(termsCourses);
				if (termsCourses.size() > 0) {
					chooseCourse.setValue(termsCourses.get(0));
					updateCurrentlySelected(termsCourses.get(0));
				}
			}
		});
		chooseTerm.setValue(Planner.currentlySelectedTerm);
		chooseCourse.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number old, Number current) {
				System.out.println("Changed course selection.");
				updateCurrentlySelected(termsCourses.get(current.intValue()));
			}
		});
		Button addMeeting = new Button("Add Meeting");
		addMeeting.setOnAction(e -> {
			Meeting m = AddMeeting.display();
			if (m != null) {
				boolean confirmAddMeetings = true;
				m.colour = currentlySelected.colour;
				ArrayList<Meeting> outerConflict = m.conflictsWith(Planner.courses);
				if (outerConflict.size() > 0) {
					confirmAddMeetings = HandleConflict.display(m, outerConflict);
				}
				if (confirmAddMeetings) {
					ArrayList<Meeting> allConflicts = new ArrayList<>();
					allConflicts.addAll(outerConflict);
					Meeting.deleteMeetings(allConflicts);
					currentlySelected.meetings.add(m);
					Planner.dayMeetings.put(m.dayOfWeek, m);
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
		Button deleteMeeting = new Button("Delete Meeting");
		deleteMeeting.setOnAction(e -> {
			Meeting m = chooseMet.getValue();
			if (m != null) {
				currentlySelected.meetings.remove(m);
				for (Term t : currentlySelected.terms) {
					CourseSchedule.removeFromSchedule(m, t);
				}
				Planner.dayMeetings.del(m.dayOfWeek, m);
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
		Button delete = new Button("Delete Course");
		delete.setOnAction(e -> {
			if (DeleteCourse.display(currentlySelected)) {
				Course selectedCopy = currentlySelected.clone();
				for (Term t : currentlySelected.terms) {
					Planner.termCourses.del(t, currentlySelected);
				}
				Planner.courses.remove(currentlySelected);
				for (Meeting m : selectedCopy.meetings) {
					for (Term t : currentlySelected.terms) {
						CourseSchedule.removeFromSchedule(m, t);
					}
					Planner.dayMeetings.del(m.dayOfWeek, m);
				}
				CourseSchedule.setTodaysMeetings();
				for (Deliverable d : selectedCopy.deliverables) {
					Planner.dateEvents.del(d.due.toLocalDate(), d);
				}
				if (Planner.courses.size() == 0) {
					currentlySelected = null;
					CourseSchedule.editCourse.setDisable(true);
					window.close();
				} else {
					chooseCourse.setValue(Planner.courses.get(0));
					updateCourseSelected(0);
				}
				CourseSchedule.drawSchedule(Planner.currentlySelectedDate);
				TermCalendar.redrawCalendars();
				IOManager.deleteFile(Planner.saveDir + "//" + Planner.activeProfile + "//" + selectedCopy.departmentID
						+ selectedCopy.code + ".json");
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

		currentlySelected = c;
		updateChooseMeeting();
		department.setText(currentlySelected.departmentID);
		code.setText("" + currentlySelected.code);
		title.setText(currentlySelected.name);
		cPicker.setValue(Color.web(currentlySelected.colour));
		meetings.setText("Weekly Meetings: " + currentlySelected.meetings.size());
	}

	private static void updateChooseMeeting() {
		ch.getChildren().remove(chooseMet);
		ObservableList<Meeting> chooseMeet = FXCollections.observableArrayList(currentlySelected.meetings);
		chooseMet = new ChoiceBox<>(chooseMeet);
		ch.getChildren().add(chooseMet);
		if (chooseMeet.size() > 0) {
			chooseMet.setValue(chooseMeet.get(0));
		}
	}

	private static void updateCourseSelected(int index) {
		try {
			currentlySelected = Planner.courses.get(index);

		} catch (IndexOutOfBoundsException e) {
			currentlySelected = null;
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
