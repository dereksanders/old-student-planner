package grades;

import java.util.Observable;
import java.util.Observer;

import core.Style;
import core.View;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import model.Course;
import model.CourseEvent;
import model.Profile;
import model.Term;

/**
 * The Class Grades.
 */
public class Grades extends View implements Observer {

	private Observable observable;
	private GradesController controller;

	private HBox selectedDisplay;
	private VBox displayGrades;
	private HBox classGrades;
	private ComboBox<Course> chooseCourse;
	private ComboBox<Term> chooseTerm;
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
	private BorderPane initLayout() {

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
		displayGrades
				.setStyle("-fx-border-width: 1; -fx-border-color: #000; -fx-padding: 10; -fx-background-color: #fff");

		body.getChildren().addAll(chooseTerm, classGrades, selectedDisplay, displayGrades);

		header.getChildren().addAll(title);
		gbp.setTop(header);
		gbp.setCenter(body);

		chooseTerm.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldIndex, Number newIndex) {
				if (newIndex.intValue() != -1) {
					controller.setCurrentlySelectedTerm(controller.active.terms.get(newIndex.intValue()));
				}
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

		gbp.setStyle("-fx-padding: 10;");

		return gbp;
	}

	/**
	 * List grades.
	 *
	 * @param selected
	 *            the selected
	 */
	private void listGrades(Term selected) {

		Label termSummaryTitle = new Label("Term Summary - " + selected.name + " (" + selected.start.getYear() + ")");
		Style.setTitleStyle(termSummaryTitle);

		Label termSummary = new Label("");

		/* Term summary */

		Label courseCol = new Label("Course");
		courseCol.setStyle("-fx-font-weight: bold;");
		Label gradeSoFarCol = new Label("Grade (so far)");
		gradeSoFarCol.setStyle("-fx-font-weight: bold;");
		Label cumulativeGradeCol = new Label("Grade (cumulative)");
		cumulativeGradeCol.setStyle("-fx-font-weight: bold;");

		GridPane courseGrid = new GridPane();

		courseGrid.add(courseCol, 0, 0);
		GridPane.setValignment(courseCol, VPos.TOP);
		courseGrid.add(gradeSoFarCol, 1, 0);
		GridPane.setValignment(gradeSoFarCol, VPos.TOP);
		courseGrid.add(cumulativeGradeCol, 2, 0);
		GridPane.setValignment(cumulativeGradeCol, VPos.TOP);
		courseGrid.getColumnConstraints().add(new ColumnConstraints(200));
		courseGrid.getColumnConstraints().add(new ColumnConstraints(200));
		courseGrid.getColumnConstraints().add(new ColumnConstraints(200));
		courseGrid.getRowConstraints().add(new RowConstraints(30));

		for (int i = 0; i < selected.courses.size(); i++) {

			Course sel = selected.courses.get(i);

			HBox courseListing = new HBox(5);

			Rectangle courseIcon = new Rectangle(20, 20);
			courseIcon.setFill(Color.web(sel.colour));

			Label courseDesc = new Label(sel.toString());

			courseListing.getChildren().addAll(courseIcon, courseDesc);

			Label gradeSoFarDesc = new Label(sel.gradeSoFar + "%");
			Label cumulativeGradeDesc = new Label(sel.cumulativeGrade + "%");

			courseGrid.add(courseListing, 0, i + 1);
			courseGrid.add(gradeSoFarDesc, 1, i + 1);
			GridPane.setValignment(gradeSoFarDesc, VPos.TOP);
			courseGrid.add(cumulativeGradeDesc, 2, i + 1);
			GridPane.setValignment(cumulativeGradeDesc, VPos.TOP);
			courseGrid.getRowConstraints().add(new RowConstraints(30));
		}

		selected.calcGrades();

		HBox gradeSoFarBox = new HBox();
		Label gradeSoFarTitle = new Label("Average so far: ");
		gradeSoFarTitle.setStyle("-fx-font-weight: bold;");
		Label gradeSoFar = new Label("" + selected.avgSoFar + "%");
		gradeSoFarBox.getChildren().addAll(gradeSoFarTitle, gradeSoFar);

		HBox cumulativeGradeBox = new HBox();
		Label cumulativeGradeTitle = new Label("Cumulative average: ");
		cumulativeGradeTitle.setStyle("-fx-font-weight: bold;");
		Label cumulativeGrade = new Label("" + selected.avg + "%");
		cumulativeGradeBox.getChildren().addAll(cumulativeGradeTitle, cumulativeGrade);

		displayGrades.getChildren().addAll(termSummaryTitle, courseGrid, gradeSoFarBox, cumulativeGradeBox);
	}

	/**
	 * List grades.
	 *
	 * @param selected
	 *            the selected
	 */
	private void listGrades(Course selected) {

		TextField grade = new TextField();
		grade.setPromptText("Enter Grade %");
		TextField weight = new TextField();
		weight.setPromptText("Enter Weight %");
		Button confirmChanges = new Button("Confirm");

		ObservableList<CourseEvent> events = FXCollections.observableArrayList();
		events.addAll(selected.events);
		ChoiceBox<CourseEvent> chooseEvent = new ChoiceBox<>(events);

		if (chooseEvent.getValue() == null) {
			grade.setDisable(true);
			weight.setDisable(true);
		}

		chooseEvent.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldIndex, Number newIndex) {
				grade.setDisable(false);
				weight.setDisable(false);
				grade.setText("" + events.get(newIndex.intValue()).grade);
				weight.setText("" + events.get(newIndex.intValue()).weight);
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

			chooseEvent.getValue().gradeEntered = true;

			listGrades(selected);
		});

		selectedDisplay.getChildren().clear();
		selectedDisplay.getChildren().addAll(chooseEvent, grade, weight, confirmChanges);

		displayGrades.getChildren().clear();

		Label courseSummaryTitle = new Label("Course Summary - " + selected.toString());
		Style.setTitleStyle(courseSummaryTitle);

		Label deliverableCol = new Label("Deliverable");
		deliverableCol.setStyle("-fx-font-weight: bold;");
		Label gradeCol = new Label("Grade");
		gradeCol.setStyle("-fx-font-weight: bold;");
		Label weightCol = new Label("Weight");
		weightCol.setStyle("-fx-font-weight: bold;");

		displayGrades.getChildren().addAll(courseSummaryTitle);

		GridPane eventGrid = new GridPane();

		eventGrid.add(deliverableCol, 0, 0);
		eventGrid.add(gradeCol, 1, 0);
		eventGrid.add(weightCol, 2, 0);
		eventGrid.getColumnConstraints().add(new ColumnConstraints(200));
		eventGrid.getColumnConstraints().add(new ColumnConstraints(200));
		eventGrid.getColumnConstraints().add(new ColumnConstraints(200));
		eventGrid.getRowConstraints().add(new RowConstraints(30));

		for (int i = 0; i < selected.events.size(); i++) {

			CourseEvent sel = selected.events.get(i);

			Label eventDesc = new Label(sel.name);
			Label gradeDesc = new Label("" + sel.grade + "%");
			Label worthDesc = new Label("" + sel.weight + "%");

			eventGrid.add(eventDesc, 0, i + 1);
			eventGrid.add(gradeDesc, 1, i + 1);
			eventGrid.add(worthDesc, 2, i + 1);
			eventGrid.getRowConstraints().add(new RowConstraints(30));
		}

		selected.calcGrades();

		HBox gradeSoFarBox = new HBox();
		Label gradeSoFarTitle = new Label("Grade so far: ");
		gradeSoFarTitle.setStyle("-fx-font-weight: bold;");
		Label gradeSoFar = new Label("" + selected.gradeSoFar + "%");
		gradeSoFarBox.getChildren().addAll(gradeSoFarTitle, gradeSoFar);

		HBox cumulativeGradeBox = new HBox();
		Label cumulativeGradeTitle = new Label("Cumulative grade: ");
		cumulativeGradeTitle.setStyle("-fx-font-weight: bold;");
		Label cumulativeGrade = new Label("" + selected.cumulativeGrade + "%");
		cumulativeGradeBox.getChildren().addAll(cumulativeGradeTitle, cumulativeGrade);

		displayGrades.getChildren().addAll(eventGrid, gradeSoFarBox, cumulativeGradeBox);

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
			Term currentlySelected = ((Profile) o).currentlySelectedTerm;

			if (currentlySelected != null) {
				chooseTerm.setValue(currentlySelected);
				coursesToDisplay = FXCollections.observableArrayList(currentlySelected.courses);
				chooseCourse.setItems(FXCollections.observableArrayList(coursesToDisplay));

				if (currentlySelected.courses.size() > 0) {

					chooseCourse.setValue(currentlySelected.courses.get(0));
					listGrades(chooseCourse.getValue());
				}
			}
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

	@Override
	public String toString() {
		return "Grades";
	}
}
