package planner;

import core.Style;
import core.Driver;
import core.ProfileController;
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
import model.Course;
import model.Meeting;
import model.Term;

/**
 * The Class EditCourse.
 */
public class EditCourse {

	private ProfileController pc;
	private Course currentlySelected;
	private ChoiceBox<Term> chooseTerm;
	private ObservableList<Course> termsCourses;
	private ChoiceBox<Course> chooseCourse;
	private TextField department;
	private TextField code;
	private TextField title;
	private ColorPicker cPicker;
	private Label meetings;
	private ChoiceBox<Meeting> chooseMeeting;
	private Label error;
	private HBox ch;

	/**
	 * Instantiates a new edits the course.
	 *
	 * @param pc
	 *            the pc
	 */
	public EditCourse(ProfileController pc) {
		this.pc = pc;
		display();
	}

	/**
	 * Display.
	 */
	private void display() {

		currentlySelected = null;

		/* Basic window set-up */
		Stage window = new Stage();
		window.initModality(Modality.APPLICATION_MODAL);
		window.setTitle("Edit Course");
		window.getIcons().add(new Image(Driver.class.getResourceAsStream("icon.png")));

		ObservableList<Term> termChoices = FXCollections.observableArrayList(pc.active.terms);
		chooseTerm = new ChoiceBox<>(termChoices);
		Style.setChoiceBoxStyle(chooseTerm);

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
		Style.setChoiceBoxStyle(chooseCourse);
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
				if (current.intValue() > -1) {
					updateCurrentlySelected(termsCourses.get(current.intValue()));
				}
			}
		});

		/* Add meetings */
		Button addMeeting = new Button("Add Meeting");
		Style.setButtonStyle(addMeeting);

		addMeeting.setOnAction(e -> {
			Meeting m = new AddMeeting().display();
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
		Style.setButtonStyle(deleteMeeting);

		deleteMeeting.setOnAction(e -> {
			pc.deleteMeeting(chooseTerm.getValue(), chooseMeeting.getValue());
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
		Style.setButtonStyle(delete);

		delete.setOnAction(e -> {
			/* If deleting the currently selected course is confirmed */
			if (new DeleteCourse(currentlySelected).display()) {
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
		Style.setButtonStyle(confirm);
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
	}

	/**
	 * Update currently selected.
	 *
	 * @param c
	 *            the c
	 */
	private void updateCurrentlySelected(Course c) {

		chooseCourse.setValue(termsCourses.get(0));
		currentlySelected = c;
		updateChooseMeeting();
		department.setText(currentlySelected.departmentID);
		code.setText("" + currentlySelected.code);
		title.setText(currentlySelected.name);
		cPicker.setValue(Color.web(currentlySelected.colour));
		meetings.setText("Weekly Meetings: " + currentlySelected.meetings.size());
	}

	/**
	 * Update choose meeting.
	 */
	private void updateChooseMeeting() {

		ch.getChildren().remove(chooseMeeting);
		ObservableList<Meeting> chooseMeet = FXCollections.observableArrayList(currentlySelected.meetings);
		chooseMeeting = new ChoiceBox<>(chooseMeet);
		Style.setChoiceBoxStyle(chooseMeeting);
		ch.getChildren().add(chooseMeeting);
		if (chooseMeet.size() > 0) {
			chooseMeeting.setValue(chooseMeet.get(0));
		}
	}

	/**
	 * Confirm changes.
	 *
	 * @return true, if successful
	 */
	private boolean confirmChanges() {
		try {
			Course changes = currentlySelected.clone();
			changes.name = title.getText();
			changes.departmentID = department.getText();
			changes.code = Integer.parseInt(code.getText());
			changes.colour = Style.colorToHex(cPicker.getValue());
			this.pc.editCourse(currentlySelected, changes);
			return true;
		} catch (NumberFormatException e) {
			error.setText("Invalid course code. Cannot save changes.");
			error.setTextFill(Color.RED);
			return false;
		}
	}
}
