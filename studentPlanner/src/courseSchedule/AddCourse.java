package courseSchedule;

import java.util.ArrayList;

import core.Course;
import core.Meeting;
import core.Planner;
import core.Term;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AddCourse {

	public static boolean legalCode;
	public static Course addCourse;

	public static Course display() {

		legalCode = false;
		addCourse = null;
		ArrayList<Meeting> addMeetings = new ArrayList<>();
		Label meetings = new Label("Weekly Meetings: " + addMeetings.size());
		Stage window = new Stage();
		window.initModality(Modality.APPLICATION_MODAL);
		window.setTitle("Add Course");
		Label header = new Label("Enter Course Info");
		header.setFont(Planner.h1);
		Label startTerm = new Label("Term(s):");
		ArrayList<ChoiceBox<Term>> termChoices = new ArrayList<>();
		termChoices.add(new ChoiceBox<>(Planner.terms));
		Button plus = new Button("+");
		Button minus = new Button("-");
		plus.setMinSize(35, 35);
		plus.setMaxSize(35, 35);
		minus.setMinSize(35, 35);
		minus.setMaxSize(35, 35);
		HBox termsControls = new HBox(10);
		termsControls.getChildren().addAll(plus, minus);
		minus.setVisible(false);
		VBox terms = new VBox(10);
		terms.getChildren().add(startTerm);
		terms.getChildren().addAll(termChoices);
		terms.getChildren().addAll(termsControls);
		if (Planner.terms.size() > 0) {
			termChoices.get(0).setValue(Planner.terms.get(0));
		}
		plus.setOnAction(e -> {
			termChoices.add(new ChoiceBox<>(Planner.terms));
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
		Label error = new Label();
		TextField department = new TextField();
		department.setPromptText("Department (e.g. PSYC)");
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
		TextField name = new TextField();
		name.setPromptText("Course Title");
		Color selected = Planner.getNextColor();
		ColorPicker cPicker = new ColorPicker(selected);
		Button add = new Button("Add Course");
		ArrayList<Term> termsArray = new ArrayList<>();
		add.setOnAction(e -> {
			if (legalCode) {
				boolean legalTerms = true;
				for (ChoiceBox<Term> c : termChoices) {
					if (c.getValue() != null && !termsArray.contains(c.getValue())) {
						termsArray.add(c.getValue());
					} else {
						legalTerms = false;
						break;
					}
				}
				if (legalTerms) {
					confirmAdd(termsArray, department.getText(), Integer.parseInt(code.getText()), name.getText(),
							addMeetings, cPicker.getValue());
					window.close();
				} else {
					error.setText("Invalid term entries.");
				}
			} else {
				error.setText("Invalid course code. Must be an integer.");
				error.setTextFill(Color.RED);
			}
		});
		Button addMeeting = new Button("Add Meeting");
		addMeeting.setOnAction(e -> {
			Meeting m = AddMeeting.display();
			if (m != null) {
				boolean confirm = true;
				ArrayList<Meeting> innerConflict = m.conflictsWith(addMeetings);
				ArrayList<Meeting> outerConflict = m.conflictsWith(Planner.courses);
				if (innerConflict.size() > 0) {
					confirm = HandleConflict.display(m, innerConflict);
				}
				if (outerConflict.size() > 0) {
					confirm = HandleConflict.display(m, outerConflict);
				}
				if (confirm) {
					ArrayList<Meeting> allConflicts = new ArrayList<>();
					allConflicts.addAll(innerConflict);
					allConflicts.addAll(outerConflict);
					Meeting.deleteMeetings(allConflicts);
					addMeetings.add(m);
					meetings.setText("Weekly Meetings: " + addMeetings.size());
					for (int i = 0; i < addMeetings.size(); i++) {
						meetings.setText(meetings.getText() + "\n Meeting " + (i + 1) + ": " + addMeetings.get(i));
						window.setHeight(window.getHeight() + 10);
					}
				}
			}
		});
		BorderPane bp = new BorderPane();
		HBox top = new HBox(20);
		VBox layout = new VBox(20);
		top.getChildren().addAll(header);
		layout.getChildren().addAll(terms, department, code, name, cPicker, meetings, addMeeting, add, error);
		bp.setTop(top);
		bp.setBottom(layout);
		Scene scene = new Scene(bp);
		add.requestFocus();
		window.setScene(scene);
		window.showAndWait();
		return addCourse;
	}

	private static void confirmAdd(ArrayList<Term> terms, String departmentID, int code, String name,
			ArrayList<Meeting> meetings, Color selected) {
		Course c = new Course(name, departmentID, code, terms, meetings, Planner.colorToHex(selected));
		addCourse = c;
	}
}
