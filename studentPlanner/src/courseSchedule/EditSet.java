package courseSchedule;

import core.Driver;
import core.Style;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class EditSet {

	private boolean editSet;

	public EditSet() {

	}

	public boolean display() {

		editSet = false;

		/* Window set-up */
		Stage window = new Stage();
		window.initModality(Modality.APPLICATION_MODAL);
		window.setTitle("Edit set or instance?");
		window.getIcons().add(new Image(Driver.class.getResourceAsStream("icon.png")));

		Button editInstance = new Button("Edit this instance");
		Style.setButtonStyle(editInstance);

		Button editAll = new Button("Edit all instances");
		Style.setButtonStyle(editAll);

		HBox options = new HBox(20);
		options.getChildren().addAll(editInstance, editAll);

		editInstance.setOnAction(e -> {
			window.close();
		});

		editAll.setOnAction(e -> {
			editSet = true;
			window.close();
		});

		window.setScene(new Scene(options));
		window.showAndWait();

		return editSet;
	}
}
