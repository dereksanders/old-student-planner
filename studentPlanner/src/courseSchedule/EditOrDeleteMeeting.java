package courseSchedule;

import java.time.LocalDateTime;
import java.time.LocalTime;

import core.Driver;
import core.Style;
import core.Time;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Course;
import model.Meeting;

/**
 * The Class EditOrDeleteMeeting.
 */
public class EditOrDeleteMeeting {

	private CourseScheduleController pc;
	private Meeting selected;
	private Course meetingCourse;
	private ComboBox<Time> startTime;
	private ComboBox<Time> endTime;

	/**
	 * Instantiates a new edits the or delete meeting.
	 *
	 * @param cell
	 *            the cell
	 * @param pc
	 *            the pc
	 */
	public EditOrDeleteMeeting(LocalDateTime cell, CourseScheduleController pc) {

		this.pc = pc;
		this.selected = pc.getMeetingAtTime(cell);
		System.out.println(this.selected);
		System.out.println(this.selected.colour);
		this.meetingCourse = pc.getCourseFromColor(Color.web(selected.colour));
		display();
	}

	/**
	 * Display.
	 */
	private void display() {

		/* Window set-up */
		Stage window = new Stage();
		window.initModality(Modality.APPLICATION_MODAL);
		window.setTitle("Edit Meeting");
		window.getIcons().add(new Image(Driver.class.getResourceAsStream("icon.png")));

		ObservableList<String> days = FXCollections.observableArrayList();
		days.addAll("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday");
		ObservableList<String> types = FXCollections.observableArrayList();
		types.addAll("Lecture", "Tutorial", "Lab", "Seminar");
		ObservableList<Time> times = FXCollections.observableArrayList();
		for (int i = 0; i < 24; i++) {
			for (int j = 0; j < 31; j += 30) {
				times.add(new Time(i, j));
			}
		}

		ChoiceBox<String> meetingType = new ChoiceBox<>(types);
		meetingType.setValue(selected.meetingType);

		ChoiceBox<String> daysChoose = new ChoiceBox<>(days);
		daysChoose.setValue(selected.dayOfWeek);

		HBox selectTimes = new HBox();
		startTime = new ComboBox<>(times);
		Label min = new Label(" - ");
		endTime = new ComboBox<>();
		selectTimes.getChildren().addAll(startTime, min, endTime);

		startTime.setValue(times.get(
				Time.getDistance(new Time(0, 0), new Time(selected.start.getHour(), selected.start.getMinute()), 30)));
		endTime.setValue(times
				.get(Time.getDistance(new Time(0, 0), new Time(selected.end.getHour(), selected.end.getMinute()), 30)));

		startTime.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number old, Number current) {
				selectTimes.getChildren().remove(endTime);
				ObservableList<Time> newEndTimes = FXCollections.observableArrayList();
				newEndTimes.addAll(times.subList(current.intValue() + 1, times.size()));
				endTime = new ComboBox<>(newEndTimes);
				selectTimes.getChildren().add(endTime);
			}
		});

		Label header = new Label("Enter Meeting Info");
		Style.setTitleStyle(header);
		Label day = new Label("Day:");
		Label hour = new Label("Time:");
		Label loc = new Label("Location:");
		TextField locField = new TextField();
		Label error = new Label();
		Button confirm = new Button("Confirm changes");
		Button delete = new Button("Delete Meeting");
		Button cancel = new Button("Cancel");

		confirm.setOnAction(e -> {
			if (meetingType.getValue() != null && daysChoose.getValue() != null && startTime.getValue() != null
					&& endTime.getValue() != null) {
				confirmChanges(meetingType.getValue(), daysChoose.getValue(), startTime.getValue(), endTime.getValue(),
						locField.getText());
				window.close();
			} else {
				error.setText("Error: You must fill out all fields.");
				error.setTextFill(Color.RED);
			}
		});

		delete.setOnAction(e -> {
			pc.deleteMeeting(this.meetingCourse, this.selected);
			window.close();
		});

		cancel.setOnAction(e -> {
			window.close();
		});

		VBox options = new VBox(20);
		options.getChildren().addAll(header, meetingType, day, daysChoose, hour, selectTimes, loc, locField, confirm,
				delete, cancel, error);
		Scene scene = new Scene(options);
		window.setScene(scene);
		window.showAndWait();
	}

	/**
	 * Confirm changes.
	 *
	 * @param type
	 *            the type
	 * @param day
	 *            the day
	 * @param start
	 *            the start
	 * @param end
	 *            the end
	 * @param loc
	 *            the loc
	 */
	private void confirmChanges(String type, String day, Time start, Time end, String loc) {
		pc.deleteMeeting(this.meetingCourse, selected);
		Meeting add = new Meeting(type, LocalTime.of(start.hour, start.minute), LocalTime.of(end.hour, end.minute), day,
				loc);
		add.colour = selected.colour;
		pc.addMeeting(this.meetingCourse, add);
	}
}
