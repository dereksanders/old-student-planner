package courseSchedule;

import java.time.LocalDateTime;
import core.Driver;
import core.Style;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Course;
import model.Meeting;
import model.MeetingSet;

/**
 * The Class EditOrDeleteMeeting.
 */
public class EditInstanceOrSet {

	private CourseScheduleController pc;
	private Meeting selected;
	private MeetingSet selectedSet;
	private Course meetingCourse;

	/**
	 * Instantiates a new edits the or delete meeting.
	 *
	 * @param cell
	 *            the cell
	 * @param pc
	 *            the pc
	 */
	public EditInstanceOrSet(LocalDateTime cell, CourseScheduleController pc) {

		this.pc = pc;
		this.selected = pc.getMeetingAtTime(cell);
		this.selectedSet = pc.getMeetingSet(cell);
		this.meetingCourse = pc.getCourseFromColor(Color.web(selected.colour));
		System.out.println(selectedSet);
		display();
	}

	public void display() {

		/* Window set-up */
		Stage window = new Stage();
		window.initModality(Modality.APPLICATION_MODAL);
		window.setTitle("Edit Meeting Options");
		window.getIcons().add(new Image(Driver.class.getResourceAsStream("icon.png")));

		Button editInstance = new Button("Edit this instance");
		Style.setButtonStyle(editInstance);

		Button editAll = new Button("Edit all instances");
		Style.setButtonStyle(editAll);

		Button editThisAndFuture = new Button("Edit this and future instances");
		Style.setButtonStyle(editThisAndFuture);

		VBox elements = new VBox(10);
		Style.addPadding(elements);

		Label listing = new Label("Meetings in this set:");
		Style.setTitleStyle(listing);
		VBox meetingListings = new VBox(5);
		meetingListings.getChildren().add(listing);

		for (Meeting m : selectedSet.getMeetings()) {

			Label current = new Label(
					meetingCourse + " " + m.meetingType + ": " + m.start + " - " + m.end + " (" + m.date + ")");

			if (m.equals(selected)) {
				current.setStyle(
						"-fx-font-style: italic;" + "-fx-text-fill: #" + Style.colorToHex(Style.appBlue) + ";");
			}

			meetingListings.getChildren().add(current);
		}

		HBox options = new HBox(20);
		options.getChildren().addAll(editInstance, editAll, editThisAndFuture);

		elements.getChildren().addAll(options, meetingListings);

		editInstance.setOnAction(e -> {
			new EditInstance(meetingCourse, selected, selectedSet, pc);
			window.close();
		});

		editAll.setOnAction(e -> {
			new EditSet(meetingCourse, selected, selectedSet, pc, false);
			window.close();
		});

		editThisAndFuture.setOnAction(e -> {
			new EditSet(meetingCourse, selected, selectedSet, pc, true);
			window.close();
		});

		window.setScene(new Scene(elements));
		window.showAndWait();
	}
}
