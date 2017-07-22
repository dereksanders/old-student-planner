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
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Course;
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
	private Label error;

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

		ObservableList<Term> termChoices = FXCollections.observableArrayList();
		for (Term t : pc.profile.terms) {
			if (t.courses.size() > 0) {
				termChoices.add(t);
			}
		}

		chooseTerm = new ChoiceBox<>(termChoices);
		Style.setChoiceBoxStyle(chooseTerm);

		title = new TextField();
		title.setPromptText("Course Title");
		department = new TextField();
		code = new TextField();
		cPicker = new ColorPicker();
		error = new Label();

		chooseCourse = new ChoiceBox<>();
		Style.setChoiceBoxStyle(chooseCourse);
		termsCourses = FXCollections.observableArrayList();

		chooseTerm.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldIndex, Number newIndex) {

				/*
				 * Clear the course selection box and add those belonging to the newly selected
				 * term.
				 */
				termsCourses.clear();
				for (Course c : termChoices.get(newIndex.intValue()).courses) {
					termsCourses.add(c);
				}

				chooseCourse.setItems(termsCourses);
				updateCurrentlySelected(termsCourses.get(0));
			}
		});

		if (termChoices.contains(pc.profile.currentlySelectedTerm)) {
			chooseTerm.setValue(pc.profile.currentlySelectedTerm);
		} else {
			chooseTerm.setValue(termChoices.get(0));
		}

		chooseCourse.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number old, Number current) {
				if (current.intValue() > -1) {
					updateCurrentlySelected(termsCourses.get(current.intValue()));
				}
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
				if (!pc.profile.coursesExist()) {

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
		layout.getChildren().addAll(chooseTerm, chooseCourse, department, code, title, cPicker, delete, confirm, error);
		Style.addPadding(layout);
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
		department.setText(currentlySelected.departmentID);
		code.setText("" + currentlySelected.code);
		title.setText(currentlySelected.name);
		cPicker.setValue(Color.web(currentlySelected.color));
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
			changes.color = Style.colorToHex(cPicker.getValue());
			this.pc.editCourse(currentlySelected, changes);
			return true;
		} catch (NumberFormatException e) {
			error.setText("Invalid course code. Cannot save changes.");
			error.setTextFill(Color.RED);
			return false;
		}
	}
}
