package courseSchedule;

import core.Course;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class DeleteCourse {

	public static boolean confirm;

	public static boolean display(Course selected) {
		Stage window = new Stage();
		window.initModality(Modality.APPLICATION_MODAL);
		window.setTitle("Delete Course");
		Label l = new Label("Are you sure you want to delete " + selected + "?");
		Button c = new Button("Yes");
		c.setOnAction(e -> {
			confirm = true;
			window.close();
		});
		Button d = new Button("No");
		d.setOnAction(e -> {
			confirm = false;
			window.close();
		});
		VBox layout = new VBox();
		layout.getChildren().addAll(l, c, d);
		Scene scene = new Scene(layout);
		window.setScene(scene);
		window.showAndWait();
		return confirm;
	}
}
