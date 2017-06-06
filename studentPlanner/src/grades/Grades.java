package grades;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import core.Driver;
import core.Style;
import core.View;
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
import model.CalendarEvent;
import model.Course;
import model.CourseEvent;
import model.Profile;
import model.Term;

/**
 * The Class Grades.
 */
public class Grades extends View implements Observer {

	public Observable observable;
	public GradesController controller;

	public Label thisCoursesAssignments;
	public Label thisCoursesTests;
	public HBox selectedDisplay;
	public VBox displayGrades;
	public HBox classGrades;
	public ComboBox<Course> chooseCourse;
	public ComboBox<Term> chooseTerm;
	private ArrayList<Course> selectedTermsCourses;
	private ObservableList<Course> coursesToDisplay;

	/**
	 * Instantiates a new grades.
	 *
	 * @param observable
	 *            the observable
	 * @param controller
	 *            the controller
	 */
	public Grades(Observable observable, GradesController controller) {

		this.observable = observable;
		observable.addObserver(this);

		this.controller = controller;
		controller.grades = this;

		this.mainLayout = initLayout();
	}

	/**
	 * Initializes the Grades layout.
	 *
	 * @return the border pane
	 */
	public BorderPane initLayout() {

		BorderPane gbp = new BorderPane();

		Label title = new Label("Enter Grades");
		Style.setTitleStyle(title);
		HBox header = new HBox(50);
		VBox body = new VBox(20);

		ObservableList<Term> termChoices = FXCollections.observableArrayList(controller.active.terms);
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

		updateTermChosen(controller.active.currentlySelectedTerm);

		chooseTerm.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldIndex, Number newIndex) {
				selectedTermsCourses = controller.active.terms.get(newIndex.intValue()).courses;
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

	/**
	 * Update term chosen.
	 *
	 * @param term
	 *            the term
	 */
	private void updateTermChosen(Term term) {
		chooseTerm.setValue(term);
		coursesToDisplay = FXCollections.observableArrayList(term.courses);
		chooseCourse = new ComboBox<>(coursesToDisplay);

		if (term.courses.size() > 0) {
			updateCourseChosen(term.courses.get(0));
		}
	}

	/**
	 * Update course chosen.
	 *
	 * @param course
	 *            the course
	 */
	private void updateCourseChosen(Course course) {
		chooseCourse.setValue(course);
		listGrades(course);
	}

	/**
	 * List grades.
	 *
	 * @param selected
	 *            the selected
	 */
	private void listGrades(Term selected) {

		Label termSummaryTitle = new Label("Term Summary");
		Style.setTitleStyle(termSummaryTitle);

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
				if (d instanceof CourseEvent) {
					if (d.start.isBefore(core.Driver.t.current)) {
						if (((CourseEvent) d).grade != 0) {
							cumulative += ((CourseEvent) d).grade * (d.weight / 100);
						}
						percentDone += (d.weight);
						gradeSoFar += d.weight * (((CourseEvent) d).grade);
					}
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
	private void listGrades(Course selected) {

		Driver.t.update();

		TextField grade = new TextField();
		grade.setPromptText("Enter Grade %");
		TextField weight = new TextField();
		weight.setPromptText("Enter Weight %");
		Button confirmChanges = new Button("Confirm");

		ObservableList<CalendarEvent> events = FXCollections.observableArrayList();
		events.addAll(selected.events);
		ChoiceBox<CalendarEvent> chooseEvent = new ChoiceBox<>(events);

		if (chooseEvent.getValue() == null) {
			grade.setDisable(true);
			weight.setDisable(true);
		}

		chooseEvent.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldIndex, Number newIndex) {
				grade.setDisable(false);
				weight.setDisable(false);
				grade.setText("" + ((CourseEvent) events.get(newIndex.intValue())).grade);
				weight.setText("" + events.get(newIndex.intValue()).weight);
			}
		});

		confirmChanges.setOnAction(e -> {
			try {
				chooseEvent.getValue().weight = Double.parseDouble(weight.getText());
			} catch (NumberFormatException e1) {
			}
			try {
				((CourseEvent) chooseEvent.getValue()).grade = Double.parseDouble(grade.getText());
			} catch (NumberFormatException e1) {
			}

			listGrades(selected);
		});

		selectedDisplay.getChildren().clear();
		selectedDisplay.getChildren().addAll(chooseEvent, grade, weight, confirmChanges);

		displayGrades.getChildren().clear();

		Label courseSummaryTitle = new Label("Course Summary");
		Style.setTitleStyle(courseSummaryTitle);

		Label courseSummary = new Label("");

		/* Course summary */

		double percentDone = 0;
		double cumulative = 0;
		double gradeSoFar = 0;

		for (CalendarEvent d : selected.events) {

			if (d instanceof CourseEvent) {
				courseSummary.setText(courseSummary.getText() + d.toString() + ", Grade: " + ((CourseEvent) d).grade
						+ "%, Worth: " + d.weight + "%\n");

				if (d.start.isBefore(core.Driver.t.current)
						|| d.start.toLocalDate().isEqual(core.Driver.t.current.toLocalDate())) {
					if (((CourseEvent) d).grade != 0) {
						cumulative += ((CourseEvent) d).grade * (d.weight / 100);
					}
					percentDone += d.weight;
					gradeSoFar += d.weight * ((CourseEvent) d).grade;

				}
			}
		}

		if (percentDone != 0) {
			gradeSoFar = gradeSoFar / percentDone;
		}

		courseSummary.setText(courseSummary.getText() + "Grade so far: " + gradeSoFar + "%, with " + percentDone
				+ "% of the course completed.");
		courseSummary.setText(courseSummary.getText() + "\n" + "Total grade: " + cumulative + "%.");

		displayGrades.getChildren().addAll(courseSummaryTitle, courseSummary);

		for (Term t : controller.findTermsBetween(selected.start, selected.end)) {
			listGrades(t);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	@Override
	public void update(Observable o, Object arg) {
		if (o instanceof Profile) {

		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see core.View#refresh()
	 */
	@Override
	public void refresh() {
		update(this.observable, null);
	}
}
