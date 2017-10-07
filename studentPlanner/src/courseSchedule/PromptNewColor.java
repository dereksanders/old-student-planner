package courseSchedule;

import core.Driver;
import core.Style;
import javafx.scene.Scene;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class PromptNewColor {

	private String color;

	public PromptNewColor(String color) {

		this.color = color;
	}

	public String display() {

		Stage window = new Stage();
		window.initModality(Modality.APPLICATION_MODAL);
		window.setTitle("Choose New Color");
		window.getIcons().add(new Image(Driver.class.getResourceAsStream("icon.png")));

		VBox elements = new VBox(10);

		Label instructions = new Label("Please select a new color:");

		ColorPicker cp = new ColorPicker();
		cp.setValue(Color.web(this.color));
		cp.setOnAction(e -> {
			color = Style.colorToHex(cp.getValue());
			window.close();
		});

		elements.getChildren().addAll(instructions, cp);
		Style.addPadding(elements);
		Scene scene = new Scene(elements);
		window.setScene(scene);
		window.showAndWait();
		return color;
	}
}
