package courseSchedule;

import core.Driver;
import core.ProfileController;
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
import model.Term;

/**
 * The Class EditTerm.
 */
public class EditTerm {

	private ProfileController pc;
	private Term currentlySelected;
	private TextField name;
	private DatePicker start;
	private DatePicker end;

	/**
	 * Instantiates a new edits the term.
	 *
	 * @param pc
	 *            the pc
	 */
	public EditTerm(ProfileController pc) {
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
		window.setTitle("Edit Term");
		window.getIcons().add(new Image(Driver.class.getResourceAsStream("icon.png")));

		ObservableList<Term> termChoices = FXCollections.observableArrayList(pc.active.terms);
		ChoiceBox<Term> chooseTerm = new ChoiceBox<>(termChoices);

		chooseTerm.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldIndex, Number newIndex) {
				updateTermSelected(pc.active.terms.get(newIndex.intValue()));
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
				this.pc.editTerm(currentlySelected, new Term(name.getText(), start.getValue(), end.getValue()));
				window.close();
			} else {
				error.setText("Error: Start date must come before end date.");
				error.setTextFill(Color.RED);
			}
		});

		dates.getChildren().addAll(startBox, endBox);
		options.getChildren().addAll(chooseTerm, dates, confirmChanges, error);
		chooseTerm.setValue(pc.active.currentlySelectedTerm);
		Scene scene = new Scene(options, 500, 200);
		window.setScene(scene);
		window.showAndWait();
	}

	/**
	 * Update term selected.
	 *
	 * @param term
	 *            the term
	 */
	private void updateTermSelected(Term term) {
		currentlySelected = term;
		name.setText(currentlySelected.name);
		start.setValue(currentlySelected.start);
		end.setValue(currentlySelected.end);
	}
}
