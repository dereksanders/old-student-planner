package termCalendar;

import java.time.LocalDate;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AddOrEditCalendarEvent {

	public static void display(LocalDate d) {

		Stage window = new Stage();
		window.initModality(Modality.APPLICATION_MODAL);
		window.setTitle("Term Calendar");
		Label text = new Label("Would you like to add a new event or edit an existing one?");
		Button add = new Button("Add Event");
		Button edit = new Button("Edit Event");
		add.setOnAction(e -> {
			AddCalendarEvent.display(d);
			window.close();
		});
		edit.setOnAction(e -> {
			EditCalendarEvent.display(d);
			window.close();
		});
		HBox options = new HBox(20);
		options.getChildren().addAll(add, edit);
		VBox box = new VBox(20);
		box.getChildren().addAll(text, options);
		Scene scene = new Scene(box);
		window.setScene(scene);
		window.showAndWait();
	}
}
