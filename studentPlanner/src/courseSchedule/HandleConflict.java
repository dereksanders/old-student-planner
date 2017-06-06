package courseSchedule;

import java.util.ArrayList;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Meeting;

public class HandleConflict {

	public static boolean confirm;

	public static boolean display(Meeting m, ArrayList<Meeting> conflicts) {
		Stage window = new Stage();
		window.initModality(Modality.APPLICATION_MODAL);
		window.setTitle("Handle Conflict");
		Label instructions = new Label(
				"A conflict exists between the new and old meetings below. Please choose which to delete.");
		Label newMeeting = new Label(m.toString());
		String s = "";
		for (Meeting c : conflicts) {
			s += c.toString() + "\n";
		}
		s = s.substring(0, s.lastIndexOf("\n"));
		Label oldMeeting = new Label(s);
		GridPane grid = new GridPane();
		BorderPane border = new BorderPane();
		Button deleteNew = new Button("Delete New Meeting");
		deleteNew.setOnAction(e -> {
			confirm = false;
			window.close();
		});
		Button deleteOld = new Button("Delete Old Meetings");
		deleteOld.setOnAction(e -> {
			confirm = true;
			window.close();
		});
		grid.add(newMeeting, 0, 0);
		grid.add(oldMeeting, 1, 0);
		grid.add(deleteNew, 0, 1);
		grid.add(deleteOld, 1, 1);
		HBox top = new HBox();
		top.getChildren().add(instructions);
		border.setTop(top);
		border.setCenter(grid);
		Scene scene = new Scene(border);
		window.setScene(scene);
		window.showAndWait();
		return confirm;
	}
}
