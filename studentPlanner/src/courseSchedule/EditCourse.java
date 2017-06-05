package courseSchedule;

import core.Course;
import core.Meeting;
import core.ProfileController;
import core.Style;
import core.Driver;
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

	public static Course display(ProfileController pc) {

		currentlySelected = null;

		/* Basic window set-up */
		Stage window = new Stage();
		window.initModality(Modality.APPLICATION_MODAL);
		window.setTitle("Edit Course");
		window.getIcons().add(new Image(Driver.class.getResourceAsStream("icon.png")));

		ObservableList<Term> termChoices = FXCollections.observableArrayList(pc.active.terms);
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
		chooseTerm.setValue(pc.active.currentlySelectedTerm);

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
			pc.addMeeting(currentlySelected, m);
			updateChooseMeeting();
			meetings.setText("Weekly Meetings: " + currentlySelected.meetings.size());
			for (int i = 0; i < currentlySelected.meetings.size(); i++) {
				meetings.setText(
						meetings.getText() + "\n Meeting " + (i + 1) + ": " + currentlySelected.meetings.get(i));
				window.setHeight(window.getHeight() + 10);
			}
		});

		/* Delete meetings */
		Button deleteMeeting = new Button("Delete Meeting");

		deleteMeeting.setOnAction(e -> {
			pc.deleteMeeting(currentlySelected, chooseMeeting.getValue());
			updateChooseMeeting();
			meetings.setText("Weekly Meeting: " + currentlySelected.meetings.size());
			for (int i = 0; i < currentlySelected.meetings.size(); i++) {
				meetings.setText(
						meetings.getText() + "\n Meeting " + (i + 1) + ": " + currentlySelected.meetings.get(i));
				window.setHeight(window.getHeight() + 10);
			}
		});

		/* Delete course */
		Button delete = new Button("Delete Course");

		delete.setOnAction(e -> {
			/* If deleting the currently selected course is confirmed */
			if (DeleteCourse.display(currentlySelected)) {
				pc.deleteCourse(currentlySelected);
				/* If no more courses exist, close the "Edit Course" window */
				if (!pc.active.coursesExist()) {

					/* TODO: Why set this to null? */
					currentlySelected = null;
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
			currentlySelected.colour = Style.colorToHex(cPicker.getValue());
			return true;
		} catch (NumberFormatException e) {
			error.setText("Invalid course code. Cannot save changes.");
			error.setTextFill(Color.RED);
			return false;
		}
	}
}
