package courseSchedule;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
import javafx.scene.control.CheckBox;
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

/**
 * The Class AddMeetingOnSchedule.
 */
public class AddMeetingOnSchedule {

	private CourseScheduleController pc;
	LocalDateTime selected;
	private ComboBox<Time> startTime;
	private ComboBox<Time> endTime;
	private Label error;

	/**
	 * Instantiates a new adds the meeting on schedule.
	 *
	 * @param cell
	 *            the cell
	 * @param pc
	 *            the pc
	 */
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
		window.setTitle("Add Meeting");
		window.getIcons().add(new Image(Driver.class.getResourceAsStream("icon.png")));

		ObservableList<String> days = FXCollections.observableArrayList();
		days.addAll("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday");
		ObservableList<String> types = FXCollections.observableArrayList();
		types.addAll(Meeting.TYPES);
		ObservableList<Time> times = FXCollections.observableArrayList();
		for (int i = 0; i < 24; i++) {
			for (int j = 0; j < 31; j += 30) {
				times.add(new Time(i, j));
			}
		}

		ChoiceBox<Course> chooseCourse = new ChoiceBox<>(
				FXCollections.observableArrayList(pc.active.currentlySelectedTerm.courses));
		Style.setChoiceBoxStyle(chooseCourse);

		if (pc.active.currentlySelectedTerm.courses.size() > 0) {
			chooseCourse.setValue(pc.active.currentlySelectedTerm.courses.get(0));
		}

		Label typeLabel = new Label("Type:");
		ChoiceBox<String> meetingType = new ChoiceBox<>(types);
		Style.setChoiceBoxStyle(meetingType);
		meetingType.setValue(types.get(0));

		Label startDateLabel = new Label("Start Date:");
		DatePicker startDate = new DatePicker();
		startDate.setValue(selected.toLocalDate());

		Label endDateLabel = new Label("End Date:");
		CheckBox toEndOfTerm = new CheckBox();
		toEndOfTerm.setSelected(true);
		toEndOfTerm.setText("To End of Term");

		DatePicker endDate = new DatePicker();
		endDate.setValue(pc.active.currentlySelectedTerm.end);
		endDate.setVisible(false);

		toEndOfTerm.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldVal, Boolean newVal) {
				if (newVal) {
					endDate.setVisible(false);
					endDate.setValue(pc.active.currentlySelectedTerm.end);
				} else {
					endDate.setVisible(true);
				}
			}
		});

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

		startTime.setValue(
				times.get(Time.getDistance(new Time(0, 0), new Time(selected.getHour(), selected.getMinute()), 30)));

		Label header = new Label("Enter Meeting Info");
		Style.setTitleStyle(header);
		Label hour = new Label("Time:");
		Label loc = new Label("Location:");
		TextField locField = new TextField();

		Label rep = new Label("Repeat:");
		ObservableList<String> repeatOptions = FXCollections.observableArrayList(MeetingSet.WEEKLY_REPEAT,
				MeetingSet.BIWEEKLY_REPEAT, MeetingSet.MONTHLY_REPEAT, MeetingSet.NO_REPEAT);
		ChoiceBox<String> chooseRepeat = new ChoiceBox<>(repeatOptions);
		Style.setChoiceBoxStyle(chooseRepeat);
		chooseRepeat.setValue(MeetingSet.WEEKLY_REPEAT);

		chooseRepeat.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number old, Number current) {
				if (repeatOptions.get(current.intValue()).equals(MeetingSet.NO_REPEAT)) {
					toEndOfTerm.setVisible(false);
					endDateLabel.setVisible(false);
				}
			}
		});

		error = new Label();
		error.setTextFill(Color.RED);

		Button confirm = new Button("Confirm changes");
		Style.setButtonStyle(confirm);
		Button cancel = new Button("Cancel");
		Style.setButtonStyle(cancel);

		confirm.setOnAction(e -> {
			if (meetingType.getValue() != null && startTime.getValue() != null && endTime.getValue() != null) {

				if (endDate.getValue().isAfter(startDate.getValue())
						|| endDate.getValue().equals(startDate.getValue())) {

					confirm(chooseCourse.getValue(), startDate.getValue(), meetingType.getValue(), startTime.getValue(),
							endTime.getValue(), locField.getText(), endDate.getValue(), chooseRepeat.getValue());

					window.close();

				} else {
					error.setText("Error: End date must be after or equal to start date.");
				}

			} else {
				error.setText("Error: You must fill out all fields.");
			}
		});

		cancel.setOnAction(e -> {
			window.close();
		});

		VBox options = new VBox(20);
		options.getChildren().addAll(header, chooseCourse, typeLabel, meetingType, startDateLabel, startDate,
				endDateLabel, toEndOfTerm, endDate, hour, selectTimes, loc, locField, rep, chooseRepeat, confirm,
				cancel, error);
		Style.addPadding(options);
		Scene scene = new Scene(options);
		window.setScene(scene);
		window.showAndWait();
	}

	/**
	 * Confirm changes.
	 *
	 * @param course
	 *            the course
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
	private void confirm(Course course, LocalDate startDate, String type, Time start, Time end, String loc,
			LocalDate endDate, String repeat) {

		MeetingSet meetingSet = new MeetingSet(new ArrayList<>());

		LocalDate cur = startDate;

		while (cur.isBefore(endDate) || cur.equals(endDate)) {

			Meeting m = new Meeting(type, cur, LocalTime.of(start.hour, start.minute),
					LocalTime.of(end.hour, end.minute), loc);

			meetingSet.addMeeting(m);

			if (repeat.equals(MeetingSet.NO_REPEAT)) {

				/* Ensure that the while loop above is broken out of. */
				cur = endDate.plusDays(1);

			} else if (repeat.equals(MeetingSet.WEEKLY_REPEAT)) {
				cur = cur.plusDays(7);

			} else if (repeat.equals(MeetingSet.BIWEEKLY_REPEAT)) {
				cur = cur.plusDays(14);

			} else if (repeat.equals(MeetingSet.MONTHLY_REPEAT)) {
				cur = cur.plusDays(28);
			}
		}

		pc.addMeetingSet(course, meetingSet, repeat);
	}
}