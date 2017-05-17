package grades;

import java.util.PriorityQueue;

import core.Course;
import core.Deliverable;
import core.Planner;
import core.Term;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class Grades {

	public static Label thisCoursesAssignments;
	public static Label thisCoursesTests;
	public static HBox selectedDisplay;
	public static VBox displayGrades;
	public static ChoiceBox<Course> gChooseCourse;
	public static ChoiceBox<Term> gChooseTerm;
	private static PriorityQueue<Course> selectedTermsCourses;
	private static ObservableList<Course> coursesToDisplay;

	public static BorderPane init() {

		BorderPane gbp = new BorderPane();

		Label gTitle = new Label("Enter Grades");
		gTitle.setFont(core.Planner.h1);
		gTitle.setTextFill(core.Planner.appBlue);
		HBox gHeader = new HBox(50);
		VBox gBody = new VBox(20);

		gChooseTerm = new ChoiceBox<>(Planner.terms);

		if (Planner.currentlySelectedTerm != null) {
			gChooseTerm.setValue(Planner.currentlySelectedTerm);
		}

		selectedTermsCourses = Planner.termCourses.get(Planner.currentlySelectedTerm);
		coursesToDisplay = FXCollections.observableArrayList(selectedTermsCourses);

		gChooseCourse = new ChoiceBox<>(coursesToDisplay);

		Label gSelectedLabel = new Label("Currently selected: ");
		HBox classGrades = new HBox(50);
		classGrades.getChildren().addAll(gSelectedLabel, gChooseCourse);
		selectedDisplay = new HBox(50);
		displayGrades = new VBox(10);
		gBody.getChildren().addAll(gChooseTerm, classGrades, selectedDisplay, displayGrades);

		gHeader.getChildren().addAll(gTitle);
		gbp.setTop(gHeader);
		gbp.setCenter(gBody);

		gChooseTerm.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldIndex, Number newIndex) {
				selectedTermsCourses = Planner.termCourses.get(Planner.terms.get(newIndex.intValue()));
				coursesToDisplay.clear();
				coursesToDisplay.addAll(selectedTermsCourses);
				updateChooseCourse();
			}
		});

		gChooseCourse.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldIndex, Number newIndex) {
				listGrades(coursesToDisplay.get(newIndex.intValue()));
			}
		});

		return gbp;
	}

	private static void updateChooseCourse() {
		gChooseCourse = new ChoiceBox<>(coursesToDisplay);
		gChooseCourse.setValue(coursesToDisplay.get(0));
		listGrades(gChooseCourse.getValue());
	}

	private static void listGrades(Course selected) {

		Planner.t.update();

		TextField grade = new TextField();
		grade.setPromptText("Enter Grade %");
		TextField weight = new TextField();
		weight.setPromptText("Enter Weight %");
		Button confirmChanges = new Button("Confirm");

		ObservableList<Deliverable> deliverables = FXCollections.observableArrayList();
		deliverables.addAll(selected.deliverables);
		ChoiceBox<Deliverable> chooseEvent = new ChoiceBox<>(deliverables);

		if (chooseEvent.getValue() == null) {
			grade.setDisable(true);
			weight.setDisable(true);
		}

		chooseEvent.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldIndex, Number newIndex) {
				grade.setDisable(false);
				weight.setDisable(false);
				grade.setText("" + ((Deliverable) deliverables.get(newIndex.intValue())).grade);
				weight.setText("" + deliverables.get(newIndex.intValue()).weight);
			}
		});

		confirmChanges.setOnAction(e -> {
			try {
				chooseEvent.getValue().weight = Double.parseDouble(weight.getText());
			} catch (NumberFormatException e1) {
			}
			try {
				chooseEvent.getValue().grade = Double.parseDouble(grade.getText());
			} catch (NumberFormatException e1) {
			}

			listGrades(selected);
		});

		selectedDisplay.getChildren().clear();
		selectedDisplay.getChildren().addAll(chooseEvent, grade, weight, confirmChanges);

		displayGrades.getChildren().clear();

		Label courseSummary = new Label("");
		Label termSummary = new Label("");

		/* Course summary */

		double percentDone = 0;
		double cumulative = 0;
		double gradeSoFar = 0;

		for (Deliverable d : selected.deliverables) {

			courseSummary.setText(
					courseSummary.getText() + d.toString() + ", Grade: " + d.grade + "%, Worth: " + d.weight + "%\n");

			if (d.due.isBefore(core.Planner.t.current)
					|| d.due.toLocalDate().isEqual(core.Planner.t.current.toLocalDate())) {
				cumulative += (d.weight) / (d.grade);
				percentDone += (d.weight);
				gradeSoFar += d.weight * (d.grade / 100);
			}
		}

		if (percentDone != 0) {
			gradeSoFar = gradeSoFar / percentDone;
		}

		courseSummary.setText(courseSummary.getText() + "Grade so far: " + gradeSoFar + "%, with " + percentDone
				+ "% of the course completed.");
		courseSummary.setText(courseSummary.getText() + "\n" + "Total grade: " + cumulative + "%.");

		/* Term summary */

		termSummary.setText("Grades for " + gChooseTerm.getValue() + ":\n");

		double average = 0;

		for (Course c : Planner.termCourses.get(gChooseTerm.getValue())) {

			percentDone = 0;
			cumulative = 0;
			gradeSoFar = 0;

			for (Deliverable d : c.deliverables) {
				if (d.due.isBefore(core.Planner.t.current)) {
					cumulative += (d.weight) / (d.grade);
					percentDone += (d.weight);
					gradeSoFar += d.weight * (d.grade / 100);
				}
			}

			if (percentDone != 0) {
				gradeSoFar = gradeSoFar / percentDone;
			}

			average += gradeSoFar;

			termSummary.setText(termSummary.getText() + c + ": " + gradeSoFar + "%, with " + percentDone
					+ "% of the course completed. Total grade: " + cumulative + "%.\n");
		}

		average = average / Planner.termCourses.get(gChooseTerm.getValue()).size();

		termSummary.setText(termSummary.getText() + "Term average (so far): " + average + "%.");

		displayGrades.getChildren().addAll(courseSummary, termSummary);
	}
}
