package planner;

import java.util.ArrayList;

import core.Style;
import core.Driver;
import core.IllegalCourseException;
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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Course;
import model.CourseEvent;
import model.MeetingSet;
import model.Term;

/**
 * The Class AddCourse.
 */
public class AddCourse {

	private ProfileController pc;
	private boolean legalCode;

	/**
	 * Instantiates a new adds the course.
	 *
	 * @param pc
	 *            the pc
	 */
	public AddCourse(ProfileController pc) {
		this.pc = pc;
		display();
	}

	/**
	 * Display Add Course window.
	 *
	 * @return the course to be added
	 */
	private void display() {

		/* Basic window set-up */
		Stage window = new Stage();
		window.initModality(Modality.APPLICATION_MODAL);
		window.setTitle("Add Course");
		window.getIcons().add(new Image(Driver.class.getResourceAsStream("icon.png")));

		Label header = new Label("Enter Course Info");
		Style.setTitleStyle(header);

		/* Buttons for adding and removing terms. */
		Button plus = new Button("+");
		Button minus = new Button("-");
		minus.setVisible(false);
		plus.setMinSize(35, 35);
		plus.setMaxSize(35, 35);
		minus.setMinSize(35, 35);
		minus.setMaxSize(35, 35);
		HBox termsControls = new HBox(10);
		termsControls.getChildren().addAll(plus, minus);

		/* List of terms to choose from. */
		ArrayList<ChoiceBox<Term>> termChoices = new ArrayList<>();
		ObservableList<Term> profileTerms = FXCollections.observableArrayList(pc.active.terms);
		termChoices.add(new ChoiceBox<>(profileTerms));

		/* Term options */
		Label startTerm = new Label("Term(s):");
		VBox terms = new VBox(10);
		terms.getChildren().add(startTerm);
		terms.getChildren().addAll(termChoices);
		terms.getChildren().addAll(termsControls);
		if (pc.active.currentlySelectedTerm != null) {
			termChoices.get(0).setValue(pc.active.currentlySelectedTerm);
		}

		/* When the "+" or "-" Term buttons are pressed. */
		plus.setOnAction(e -> {
			termChoices.add(new ChoiceBox<>(profileTerms));
			terms.getChildren().clear();
			terms.getChildren().add(startTerm);
			terms.getChildren().addAll(termChoices);
			terms.getChildren().addAll(termsControls);
			minus.setVisible(true);
		});

		minus.setOnAction(e -> {
			termChoices.remove(termChoices.size() - 1);
			terms.getChildren().clear();
			terms.getChildren().add(startTerm);
			terms.getChildren().addAll(termChoices);
			terms.getChildren().addAll(termsControls);
			if (termChoices.size() == 1) {
				minus.setVisible(false);
			}
		});

		TextField name = new TextField();
		name.setPromptText("Course Title");

		TextField department = new TextField();
		department.setPromptText("Department (e.g. PSYC)");

		/* Used to test if the course code is a valid integer value */
		legalCode = false;
		Label error = new Label();

		TextField code = new TextField();
		code.setPromptText("Course Code (e.g. 101)");
		code.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String old, String current) {
				try {
					Integer.parseInt(code.getText());
					legalCode = true;
				} catch (NumberFormatException e) {
					legalCode = false;
				}
			}
		});

		/* Select by default the next unused application color. */
		Color selected = pc.planner.getNextColor();
		ColorPicker cPicker = new ColorPicker(selected);

		Button add = new Button("Add Course");
		Style.setButtonStyle(add);
		ArrayList<Term> termsArray = new ArrayList<>();
		add.setOnAction(e -> {
			if (legalCode) {
				boolean legalTerms = true;
				for (ChoiceBox<Term> t : termChoices) {
					if (t.getValue() != null && !termsArray.contains(t.getValue())) {
						termsArray.add(t.getValue());
					} else {
						legalTerms = false;
						break;
					}
				}
				if (legalTerms) {
					confirmAdd(termsArray, department.getText(), Integer.parseInt(code.getText()), name.getText(),
							cPicker.getValue());
					window.close();
				} else {
					error.setText("Invalid term entries.");
				}
			} else {
				error.setText("Invalid course code. Must be an integer.");
				error.setTextFill(Color.RED);
			}
		});

		BorderPane bp = new BorderPane();
		HBox top = new HBox(20);
		VBox layout = new VBox(20);
		top.getChildren().addAll(header);
		layout.getChildren().addAll(terms, department, code, name, cPicker, add, error);
		bp.setTop(top);
		bp.setBottom(layout);
		Scene scene = new Scene(bp);
		add.requestFocus();
		window.setScene(scene);
		window.showAndWait();
	}

	/**
	 * Confirm add.
	 *
	 * @param terms
	 *            the terms
	 * @param departmentID
	 *            the department ID
	 * @param code
	 *            the code
	 * @param name
	 *            the name
	 * @param meetings
	 *            the meetings
	 * @param selected
	 *            the selected
	 */
	private void confirmAdd(ArrayList<Term> terms, String departmentID, int code, String name, Color selected) {

		Course c = new Course(name, departmentID, code, terms.get(0).start, terms.get(terms.size() - 1).end,
				new ArrayList<MeetingSet>(), new ArrayList<CourseEvent>(), Style.colorToHex(selected));

		try {

			this.pc.addCourse(c);

		} catch (IllegalCourseException e) {

			e.printStackTrace();
		}
	}
}
