package courseSchedule;

import core.Planner;
import core.Term;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import termCalendar.TermCalendar;

public class EditTerm {

	private static Term currentlySelected;
	private static TextField name;
	private static DatePicker start;
	private static DatePicker end;

	public static void display() {

		currentlySelected = null;

		/* Basic window set-up */
		Stage window = new Stage();
		window.initModality(Modality.APPLICATION_MODAL);
		window.setTitle("Edit Term");
		window.getIcons().add(new Image(Planner.class.getResourceAsStream("icon.png")));

		ObservableList<Term> termChoices = FXCollections.observableArrayList(Planner.active.terms);
		ChoiceBox<Term> chooseTerm = new ChoiceBox<>(termChoices);

		chooseTerm.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldIndex, Number newIndex) {
				updateTermSelected(Planner.active.terms.get(newIndex.intValue()));
			}
		});

		name = new TextField();
		name.setPromptText("Term Name");
		Label startLabel = new Label("Start: ");
		Label endLabel = new Label("End: ");
		Label error = new Label();
		start = new DatePicker();
		end = new DatePicker();
		VBox startBox = new VBox(10);
		startBox.getChildren().addAll(startLabel, start);
		VBox endBox = new VBox(10);
		endBox.getChildren().addAll(endLabel, end);
		VBox options = new VBox(20);
		HBox dates = new HBox(20);
		Button confirmChanges = new Button("Confirm Changes");

		confirmChanges.setOnAction(e -> {
			if (start.getValue().isBefore(end.getValue())) {

				currentlySelected.name = name.getText();
				currentlySelected.start = start.getValue();
				currentlySelected.end = end.getValue();

				/* Only redraw Term Calendar if it has been initialized. */
				if (Planner.tc != null) {
					TermCalendar.redrawCalendars();
				}
				window.close();
			} else {
				error.setText("Error: Start date must come before end date.");
				error.setTextFill(Color.RED);
			}
		});
		dates.getChildren().addAll(startBox, endBox);
		options.getChildren().addAll(chooseTerm, dates, confirmChanges, error);
		chooseTerm.setValue(Planner.active.currentlySelectedTerm);
		Scene scene = new Scene(options, 500, 200);
		window.setScene(scene);
		window.showAndWait();
	}

	private static void updateTermSelected(Term term) {
		currentlySelected = term;
		name.setText(currentlySelected.name);
		start.setValue(currentlySelected.start);
		end.setValue(currentlySelected.end);
	}
}
