package courseSchedule;

import java.util.ArrayList;

import core.Driver;
import core.ProfileController;
import core.Style;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Meeting;

public class HandleConflict {

	private Meeting m;
	private ArrayList<Meeting> conflicts;
	private ProfileController pc;
	private boolean confirm;

	public HandleConflict(Meeting m, ArrayList<Meeting> conflicts, ProfileController pc) {
		this.m = m;
		this.conflicts = conflicts;
		this.pc = pc;
	}

	public boolean display() {
		Stage window = new Stage();
		window.initModality(Modality.APPLICATION_MODAL);
		window.setTitle("Handle Conflict");
		window.getIcons().add(new Image(Driver.class.getResourceAsStream("icon.png")));

		Label instructions = new Label("A conflict exists. Please choose which meeting(s) to delete.");

		VBox elements = new VBox(15);

		HBox info = new HBox(15);

		Label newTitle = new Label("New Meeting:");
		Style.setTitleStyle(newTitle);
		Label oldTitle = new Label("Conflicts:");
		Style.setTitleStyle(oldTitle);

		VBox oldListing = new VBox(5);
		VBox newListing = new VBox(5);

		Label newMeetingDesc = new Label(m.meetingType + ": " + m.start + " - " + m.end + " (" + m.date + ")");

		newListing.getChildren().addAll(newTitle, newMeetingDesc);
		oldListing.getChildren().add(oldTitle);

		for (Meeting m : conflicts) {

			oldListing.getChildren().add(new Label(pc.active.currentlySelectedTerm.courseColors.get(Color.web(m.colour))
					+ " " + m.meetingType + ": " + m.start + " - " + m.end + " (" + m.date + ")"));
		}

		info.getChildren().addAll(newListing, oldListing);

		Button deleteNew = new Button("Delete New Meeting");
		Style.setButtonStyle(deleteNew);
		deleteNew.setOnAction(e -> {
			confirm = false;
			window.close();
		});

		Button deleteOld = new Button("Delete Conflicts");
		Style.setButtonStyle(deleteOld);
		deleteOld.setOnAction(e -> {
			confirm = true;
			window.close();
		});

		HBox buttons = new HBox(10);
		buttons.getChildren().addAll(deleteNew, deleteOld);

		elements.getChildren().addAll(instructions, info, buttons);
		Style.addPadding(elements);
		Scene scene = new Scene(elements);
		window.setScene(scene);
		window.showAndWait();
		return confirm;
	}
}
