package grades;

import java.util.ArrayList;
import core.Course;
import core.Deliverable;
import core.Planner;
import core.Term;
import gradesPlot.GradesPlot;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
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
	public static ComboBox<Course> gChooseCourse;
	public static ComboBox<Term> gChooseTerm;
	private static ArrayList<Course> selectedTermsCourses;
	private static ObservableList<Course> coursesToDisplay;

	public static BorderPane init() {

		BorderPane gbp = new BorderPane();

		Label gTitle = new Label("Enter Grades");
		gTitle.setFont(core.Planner.h1);
		gTitle.setTextFill(core.Planner.appBlue);
		HBox gHeader = new HBox(50);
		VBox gBody = new VBox(20);

		ObservableList<Term> termChoices = FXCollections.observableArrayList(Planner.active.terms);
		gChooseTerm = new ComboBox<>(termChoices);

		if (Planner.active.currentlySelectedTerm != null) {
			selectedTermsCourses = Planner.active.currentlySelectedTerm.courses;
			coursesToDisplay = FXCollections.observableArrayList(selectedTermsCourses);
		}

		gChooseCourse = new ComboBox<>(coursesToDisplay);

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
				selectedTermsCourses = Planner.active.terms.get(newIndex.intValue()).courses;
				coursesToDisplay = FXCollections.observableArrayList(selectedTermsCourses);
				System.out.println(coursesToDisplay);
			}
		});

		gChooseCourse.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldIndex, Number newIndex) {
				if (newIndex.intValue() < 1) {
					listGrades(coursesToDisplay.get(0));
				} else {
					listGrades(coursesToDisplay.get(newIndex.intValue()));
				}
			}
		});

		return gbp;

	}

	private static void listGrades(Term selected) {

		Label termSummaryTitle = new Label("Term Summary");
		termSummaryTitle.setFont(Planner.h1);
		termSummaryTitle.setTextFill(Planner.appBlue);

		Label termSummary = new Label("");

		/* Term summary */

		termSummary.setText("Grades for " + gChooseTerm.getValue() + ":\n");

		double avgSoFar = 0;
		double avg = 0;

		for (Course c : selected.courses) {

			double percentDone = 0;
			double cumulative = 0;
			double gradeSoFar = 0;

			for (Deliverable d : c.deliverables) {
				if (d.due.isBefore(core.Planner.t.current)) {
					if (d.grade != 0) {
						cumulative += d.grade * (d.weight / 100);
					}
					percentDone += (d.weight);
					gradeSoFar += d.weight * (d.grade);
				}
			}

			if (percentDone != 0) {
				gradeSoFar = gradeSoFar / percentDone;
			}

			c.grade = cumulative;

			avg += cumulative;
			avgSoFar += gradeSoFar;

			termSummary.setText(termSummary.getText() + c + ": " + gradeSoFar + "%, with " + percentDone
					+ "% of the course completed. Total grade: " + cumulative + "%.\n");
		}

		System.out.println(avgSoFar);

		int numCourses = selected.courses.size();

		avg = avg / numCourses;
		selected.grade = avg;

		avgSoFar = avgSoFar / numCourses;

		termSummary.setText(termSummary.getText() + "Term average (so far): " + avgSoFar + "%.\nTerm average (total): "
				+ avg + "%");

		displayGrades.getChildren().addAll(termSummaryTitle, termSummary);
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
			GradesPlot.update();
		});

		selectedDisplay.getChildren().clear();
		selectedDisplay.getChildren().addAll(chooseEvent, grade, weight, confirmChanges);

		displayGrades.getChildren().clear();

		Label courseSummaryTitle = new Label("Course Summary");
		courseSummaryTitle.setFont(Planner.h1);
		courseSummaryTitle.setTextFill(Planner.appBlue);

		Label courseSummary = new Label("");

		/* Course summary */

		double percentDone = 0;
		double cumulative = 0;
		double gradeSoFar = 0;

		for (Deliverable d : selected.deliverables) {

			courseSummary.setText(
					courseSummary.getText() + d.toString() + ", Grade: " + d.grade + "%, Worth: " + d.weight + "%\n");

			if (d.due.isBefore(core.Planner.t.current)
					|| d.due.toLocalDate().isEqual(core.Planner.t.current.toLocalDate())) {
				if (d.grade != 0) {
					cumulative += d.grade * (d.weight / 100);
				}
				percentDone += d.weight;
				gradeSoFar += d.weight * d.grade;
			}
		}

		if (percentDone != 0) {
			gradeSoFar = gradeSoFar / percentDone;
		}

		courseSummary.setText(courseSummary.getText() + "Grade so far: " + gradeSoFar + "%, with " + percentDone
				+ "% of the course completed.");
		courseSummary.setText(courseSummary.getText() + "\n" + "Total grade: " + cumulative + "%.");

		displayGrades.getChildren().addAll(courseSummaryTitle, courseSummary);

		for (Term t : Planner.active.courseTerms.get(selected)) {
			listGrades(t);
		}
	}
}
