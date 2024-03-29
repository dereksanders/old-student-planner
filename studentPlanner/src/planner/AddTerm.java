package planner;

import java.time.LocalDate;

import core.Driver;
import core.ProfileController;
import core.Style;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Term;

/**
 * The Class AddTerm.
 */
public class AddTerm {

	private ProfileController pc;

	/**
	 * Instantiates a new adds the term.
	 *
	 * @param pc
	 *            the pc
	 */
	public AddTerm(ProfileController pc) {
		this.pc = pc;
		display();
	}

	/**
	 * Display.
	 */
	private void display() {

		/* Window set-up */
		Stage window = new Stage();
		window.initModality(Modality.APPLICATION_MODAL);
		window.setTitle("Add Term");
		window.getIcons().add(new Image(Driver.class.getResourceAsStream("icon.png")));

		/* GUI elements */
		TextField name = new TextField();
		name.setPromptText("Term Name (e.g. Fall)");
		Label startTitle = new Label("Start: ");
		Label endTitle = new Label("End: ");
		DatePicker startDate = new DatePicker();
		DatePicker endDate = new DatePicker();
		VBox start = new VBox(10);
		VBox end = new VBox(10);
		Button add = new Button("Add Term");
		Style.setButtonStyle(add);
		Label error = new Label();

		startDate.valueProperty().addListener(new ChangeListener<LocalDate>() {
			@Override
			public void changed(ObservableValue<? extends LocalDate> observable, LocalDate oldDate, LocalDate newDate) {

				if (endDate.getValue() == null || endDate.getValue().isBefore(newDate)) {
					endDate.setValue(newDate);
				}
			}
		});

		add.setOnAction(e -> {
			if (startDate.getValue() == null || endDate.getValue() == null) {
				error.setText("Error: Select start and end date of term.");
				error.setTextFill(Color.RED);
			} else if (startDate.getValue().isBefore(endDate.getValue())) {
				confirm(name.getText(), startDate.getValue(), endDate.getValue());
				window.close();
			} else {
				error.setText("Error: Start date must be before end date.");
				error.setTextFill(Color.RED);
			}
		});

		start.getChildren().addAll(startTitle, startDate);
		end.getChildren().addAll(endTitle, endDate);
		BorderPane bp = new BorderPane();
		HBox body = new HBox(20);
		body.getChildren().addAll(start, end);
		VBox options = new VBox(20);
		options.getChildren().addAll(add, error);
		bp.setTop(name);
		bp.setCenter(body);
		bp.setBottom(options);
		Style.addPadding(bp);
		Scene scene = new Scene(bp, 500, 200);
		add.requestFocus();
		window.setScene(scene);
		window.showAndWait();
	}

	/**
	 * Confirm.
	 *
	 * @param name
	 *            the name
	 * @param start
	 *            the start
	 * @param end
	 *            the end
	 */
	private void confirm(String name, LocalDate start, LocalDate end) {
		Term add = new Term(name, start, end);
		this.pc.addTerm(add);
	}
}
