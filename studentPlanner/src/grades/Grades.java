package grades;

import java.util.ArrayList;

import core.CalendarEvent;
import core.Course;
import core.Driver;
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

/**
 * The Class Grades.
 */
public class Grades {

	public static Label thisCoursesAssignments;
	public static Label thisCoursesTests;
	public static HBox selectedDisplay;
	public static VBox displayGrades;
	public static HBox classGrades;
	public static ComboBox<Course> chooseCourse;
	public static ComboBox<Term> chooseTerm;
	private static ArrayList<Course> selectedTermsCourses;
	private static ObservableList<Course> coursesToDisplay;

	/**
	 * Initializes the Grades layout.
	 *
	 * @return the border pane
	 */
	public static BorderPane init() {

		BorderPane gbp = new BorderPane();

		Label title = new Label("Enter Grades");
		Driver.setTitleStyle(title);
		HBox header = new HBox(50);
		VBox body = new VBox(20);

		ObservableList<Term> termChoices = FXCollections.observableArrayList(Driver.active.terms);
		chooseTerm = new ComboBox<>(termChoices);
		chooseCourse = new ComboBox<>(coursesToDisplay);

		Label gSelectedLabel = new Label("Currently selected: ");
		classGrades = new HBox(50);
		classGrades.getChildren().addAll(gSelectedLabel, chooseCourse);

		selectedDisplay = new HBox(50);
		displayGrades = new VBox(10);
		body.getChildren().addAll(chooseTerm, classGrades, selectedDisplay, displayGrades);

		header.getChildren().addAll(title);
		gbp.setTop(header);
		gbp.setCenter(body);

		updateTermChosen(Driver.active.currentlySelectedTerm);

		chooseTerm.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldIndex, Number newIndex) {
				selectedTermsCourses = Driver.active.terms.get(newIndex.intValue()).courses;
				coursesToDisplay = FXCollections.observableArrayList(selectedTermsCourses);
			}
		});

		chooseCourse.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
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

	private static void updateTermChosen(Term term) {
		chooseTerm.setValue(term);
		coursesToDisplay = FXCollections.observableArrayList(term.courses);
		chooseCourse = new ComboBox<>(coursesToDisplay);

		if (term.courses.size() > 0) {
			updateCourseChosen(term.courses.get(0));
		}
	}

	private static void updateCourseChosen(Course course) {
		chooseCourse.setValue(course);
		listGrades(course);
	}

	/**
	 * List grades.
	 *
	 * @param selected
	 *            the selected
	 */
	private static void listGrades(Term selected) {

		Label termSummaryTitle = new Label("Term Summary");
		Driver.setTitleStyle(termSummaryTitle);

		Label termSummary = new Label("");

		/* Term summary */

		termSummary.setText("Grades for " + chooseTerm.getValue() + ":\n");

		double avgSoFar = 0;
		double avg = 0;

		for (Course c : selected.courses) {

			double percentDone = 0;
			double cumulative = 0;
			double gradeSoFar = 0;

			for (CalendarEvent d : c.events) {
				if (d.start.isBefore(core.Driver.t.current)) {
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

	/**
	 * List grades.
	 *
	 * @param selected
	 *            the selected
	 */
	private static void listGrades(Course selected) {

		Driver.t.update();

		TextField grade = new TextField();
		grade.setPromptText("Enter Grade %");
		TextField weight = new TextField();
		weight.setPromptText("Enter Weight %");
		Button confirmChanges = new Button("Confirm");

		ObservableList<CalendarEvent> deliverables = FXCollections.observableArrayList();
		deliverables.addAll(selected.events);
		ChoiceBox<CalendarEvent> chooseEvent = new ChoiceBox<>(deliverables);

		if (chooseEvent.getValue() == null) {
			grade.setDisable(true);
			weight.setDisable(true);
		}

		chooseEvent.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldIndex, Number newIndex) {
				grade.setDisable(false);
				weight.setDisable(false);
				grade.setText("" + deliverables.get(newIndex.intValue()).grade);
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
		Driver.setTitleStyle(courseSummaryTitle);

		Label courseSummary = new Label("");

		/* Course summary */

		double percentDone = 0;
		double cumulative = 0;
		double gradeSoFar = 0;

		for (CalendarEvent d : selected.events) {

			courseSummary.setText(
					courseSummary.getText() + d.toString() + ", Grade: " + d.grade + "%, Worth: " + d.weight + "%\n");

			if (d.start.isBefore(core.Driver.t.current)
					|| d.start.toLocalDate().isEqual(core.Driver.t.current.toLocalDate())) {
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

		for (Term t : Driver.active.courseTerms.get(selected)) {
			listGrades(t);
		}
	}
}
