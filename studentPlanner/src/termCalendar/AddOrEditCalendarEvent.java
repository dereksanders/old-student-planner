package termCalendar;

import java.time.LocalDate;

import core.Driver;
import core.Style;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * The Class AddOrEditCalendarEvent.
 */
public class AddOrEditCalendarEvent {

	private LocalDate date;
	private TermCalendarController controller;

	/**
	 * Instantiates a new adds the or edit calendar event.
	 *
	 * @param date
	 *            the date
	 * @param controller
	 *            the controller
	 */
	public AddOrEditCalendarEvent(LocalDate date, TermCalendarController controller) {
		this.date = date;
		this.controller = controller;
		display();
	}

	/**
	 * Display.
	 */
	private void display() {

		Stage window = new Stage();
		window.initModality(Modality.APPLICATION_MODAL);
		window.setTitle("Term Calendar");
		window.getIcons().add(new Image(Driver.class.getResourceAsStream("icon.png")));

		Label text = new Label("Would you like to add a new event or edit an existing one?");

		Button add = new Button("Add Event");
		Style.setButtonStyle(add);

		Button edit = new Button("Edit Event");
		Style.setButtonStyle(edit);

		add.setOnAction(e -> {
			new AddCalendarEvent(date, controller);
			window.close();
		});
		edit.setOnAction(e -> {
			new EditCalendarEvent(date, controller);
			window.close();
		});
		HBox options = new HBox(20);
		options.getChildren().addAll(add, edit);
		VBox box = new VBox(20);
		box.getChildren().addAll(text, options);
		Style.addPadding(box);
		Scene scene = new Scene(box);
		window.setScene(scene);
		window.showAndWait();
	}
}
