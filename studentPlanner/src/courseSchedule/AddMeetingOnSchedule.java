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

public class AddMeetingOnSchedule {

	private CourseScheduleController pc;
	LocalDateTime selected;
	private ComboBox<Time> startTime;
	private ComboBox<Time> endTime;

	public AddMeetingOnSchedule(LocalDateTime cell, CourseScheduleController pc) {
		this.selected = cell;
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

		ChoiceBox<Course> chooseCourse = new ChoiceBox<>(
				FXCollections.observableArrayList(pc.active.currentlySelectedTerm.courses));

		ChoiceBox<String> meetingType = new ChoiceBox<>(types);
		meetingType.setValue(types.get(0));

		ChoiceBox<String> daysChoose = new ChoiceBox<>(days);
		daysChoose.setValue(days.get(selected.getDayOfWeek().getValue() - 1));

		HBox selectTimes = new HBox();
		startTime = new ComboBox<>(times);
		Label min = new Label(" - ");
		endTime = new ComboBox<>();
		endTime.setVisible(false);
		selectTimes.getChildren().addAll(startTime, min, endTime);

		startTime.setValue(
				times.get(Time.getDistance(new Time(0, 0), new Time(selected.getHour(), selected.getMinute()), 30)));
		endTime.setValue(times.get(
				Time.getDistance(new Time(0, 0), new Time((selected.getHour() + 1) % 12, selected.getMinute()), 30)));

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
		Button cancel = new Button("Cancel");

		confirm.setOnAction(e -> {
			if (meetingType.getValue() != null && daysChoose.getValue() != null && startTime.getValue() != null
					&& endTime.getValue() != null) {
				confirm(chooseCourse.getValue(), meetingType.getValue(), daysChoose.getValue(), startTime.getValue(),
						endTime.getValue(), locField.getText());
				window.close();
			} else {
				error.setText("Error: You must fill out all fields.");
				error.setTextFill(Color.RED);
			}
		});

		cancel.setOnAction(e -> {
			window.close();
		});

		VBox options = new VBox(20);
		options.getChildren().addAll(header, chooseCourse, meetingType, day, daysChoose, hour, selectTimes, loc,
				locField, confirm, cancel, error);
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
	private void confirm(Course course, String type, String day, Time start, Time end, String loc) {
		Meeting add = new Meeting(type, LocalTime.of(start.hour, start.minute), LocalTime.of(end.hour, end.minute), day,
				loc);
		pc.addMeeting(course, add);
	}
}
