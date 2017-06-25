package courseSchedule;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

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
import javafx.scene.control.DatePicker;
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
import model.MeetingSet;

public class EditInstance {

	private Course meetingCourse;
	private Meeting selected;
	private MeetingSet selectedSet;
	private CourseScheduleController pc;

	/* GUI elements */
	private ComboBox<Time> startTime;
	private ComboBox<Time> endTime;

	public EditInstance(Course meetingCourse, Meeting selected, MeetingSet selectedSet, CourseScheduleController pc) {
		this.meetingCourse = meetingCourse;
		this.selected = selected;
		this.selectedSet = selectedSet;
		this.pc = pc;
		display();
	}

	private void display() {

		/* Window set-up */
		Stage window = new Stage();
		window.initModality(Modality.APPLICATION_MODAL);
		window.setTitle("Edit Meeting");
		window.getIcons().add(new Image(Driver.class.getResourceAsStream("icon.png")));

		ObservableList<String> types = FXCollections.observableArrayList();
		types.addAll(Meeting.TYPES);
		ObservableList<Time> times = FXCollections.observableArrayList();
		for (int i = 0; i < 24; i++) {
			for (int j = 0; j < 31; j += 30) {
				times.add(new Time(i, j));
			}
		}

		Label typeLabel = new Label("Type:");
		ChoiceBox<String> meetingType = new ChoiceBox<>(types);
		Style.setChoiceBoxStyle(meetingType);
		meetingType.setValue(selected.meetingType);

		Label startDateLabel = new Label("Start Date:");
		DatePicker startDate = new DatePicker();
		startDate.setValue(selectedSet.getStart());

		HBox selectTimes = new HBox();
		startTime = new ComboBox<>(times);
		Style.setComboBoxStyle(startTime);
		Label min = new Label(" - ");
		endTime = new ComboBox<>();
		Style.setComboBoxStyle(endTime);
		selectTimes.getChildren().addAll(startTime, min, endTime);

		startTime.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number old, Number current) {
				selectTimes.getChildren().remove(endTime);
				ObservableList<Time> newEndTimes = FXCollections.observableArrayList();
				newEndTimes.addAll(times.subList(current.intValue() + 1, times.size()));
				endTime = new ComboBox<>(newEndTimes);
				Style.setComboBoxStyle(endTime);
				selectTimes.getChildren().add(endTime);
			}
		});

		startTime.setValue(times.get(
				Time.getDistance(new Time(0, 0), new Time(selected.start.getHour(), selected.start.getMinute()), 30)));
		endTime.setValue(times
				.get(Time.getDistance(new Time(0, 0), new Time(selected.end.getHour(), selected.end.getMinute()), 30)));

		Label header = new Label("Enter Meeting Info");
		Style.setTitleStyle(header);
		Label hour = new Label("Time:");
		Label loc = new Label("Location:");
		TextField locField = new TextField();
		Label error = new Label();
		Button confirm = new Button("Confirm changes");
		Style.setButtonStyle(confirm);
		Button delete = new Button("Delete Meeting");
		Style.setButtonStyle(delete);
		Button cancel = new Button("Cancel");
		Style.setButtonStyle(cancel);

		confirm.setOnAction(e -> {
			/* Confirm changes to all instances in the MeetingSet. */
			if (meetingType.getValue() != null && startTime.getValue() != null && endTime.getValue() != null) {
				confirmChanges(startDate.getValue(), meetingType.getValue(), startTime.getValue(), endTime.getValue(),
						locField.getText());
				window.close();
			} else {
				error.setText("Error: You must fill out all fields.");
				error.setTextFill(Color.RED);
			}
		});

		delete.setOnAction(e -> {
			deleteSelectedInstance();
			window.close();
		});

		cancel.setOnAction(e -> {
			window.close();
		});

		VBox options = new VBox(20);
		options.getChildren().addAll(header, typeLabel, meetingType, startDateLabel, startDate, hour, selectTimes, loc,
				locField, confirm, delete, cancel, error);
		Style.addPadding(options);
		Scene scene = new Scene(options);
		window.setScene(scene);
		window.showAndWait();
	}

	private void confirmChanges(LocalDate startDate, String type, Time start, Time end, String loc) {

		deleteSelectedInstance();

		MeetingSet ms = new MeetingSet();

		ms.addMeeting(new Meeting(type, startDate, LocalTime.of(start.hour, start.minute),
				LocalTime.of(end.hour, end.minute), loc));

		pc.addMeetingSet(this.meetingCourse, ms, MeetingSet.NO_REPEAT);
	}

	private void deleteSelectedInstance() {

		this.selectedSet.getMeetings().remove(selected);
		/*
		 * Create a new MeetingSet containing the single meeting and delete
		 * that.
		 */
		MeetingSet deleted = new MeetingSet(new ArrayList<Meeting>());
		deleted.addMeeting(selected);
		pc.deleteMeetingSet(deleted);
	}
}
