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
 * The Class EditOrDeleteMeeting.
 */
public class EditOrDeleteMeeting {

	private CourseScheduleController pc;
	private Meeting selected;
	private MeetingSet selectedSet;
	private Course meetingCourse;
	private ComboBox<Time> startTime;
	private ComboBox<Time> endTime;
	private boolean editSet;

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
		this.selectedSet = pc.getMeetingSet(cell);
		System.out.println(selectedSet);
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
		this.editSet = new EditSet().display();

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

		Label startDateLabel = new Label("Start Date:");
		DatePicker startDate = new DatePicker();
		startDate.setValue(selectedSet.getStart());

		Label endDateLabel = new Label("End Date:");
		CheckBox toEndOfTerm = new CheckBox();
		toEndOfTerm.setText("To End of Term");
		toEndOfTerm.setSelected(false);
		if (!editSet) {
			toEndOfTerm.setVisible(false);
			endDateLabel.setVisible(false);
		}

		DatePicker endDate = new DatePicker();
		endDate.setValue(selectedSet.getEnd());
		endDate.setVisible(true);

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

		ObservableList<String> repeatOptions = FXCollections.observableArrayList("Weekly", "Bi-Weekly", "Monthly",
				"Never");
		ChoiceBox<String> chooseRepeat = new ChoiceBox<>(repeatOptions);
		Style.setChoiceBoxStyle(chooseRepeat);
		chooseRepeat.setValue(selectedSet.repeat);
		if (!editSet) {
			chooseRepeat.setVisible(false);
		}

		HBox selectTimes = new HBox();
		startTime = new ComboBox<>(times);
		Label min = new Label(" - ");
		endTime = new ComboBox<>();
		selectTimes.getChildren().addAll(startTime, min, endTime);

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

		startTime.setValue(times.get(
				Time.getDistance(new Time(0, 0), new Time(selected.start.getHour(), selected.start.getMinute()), 30)));
		endTime.setValue(times
				.get(Time.getDistance(new Time(0, 0), new Time(selected.end.getHour(), selected.end.getMinute()), 30)));

		Label header = new Label("Enter Meeting Info");
		Style.setTitleStyle(header);
		Label hour = new Label("Time:");
		Label rep = new Label("Repeat:");
		Label loc = new Label("Location:");
		TextField locField = new TextField();
		Label error = new Label();
		Button confirm = new Button("Confirm changes");
		Button delete = new Button("Delete Meeting");
		Button cancel = new Button("Cancel");

		confirm.setOnAction(e -> {
			/* Confirm changes to all instances in the MeetingSet. */
			if (editSet && meetingType.getValue() != null && startTime.getValue() != null
					&& endTime.getValue() != null) {

				if (endDate.getValue().isAfter(startDate.getValue())
						|| endDate.getValue().equals(startDate.getValue())) {

					confirmChanges(startDate.getValue(), meetingType.getValue(), startTime.getValue(),
							endTime.getValue(), locField.getText(), endDate.getValue(), chooseRepeat.getValue());

					window.close();
				}

			} /* Confirm changes to the single instance of the meeting. */
			else if (meetingType.getValue() != null && startTime.getValue() != null && endTime.getValue() != null) {
				confirmChanges(startDate.getValue(), meetingType.getValue(), startTime.getValue(), endTime.getValue(),
						locField.getText());
				window.close();
			} else {
				error.setText("Error: You must fill out all fields.");
				error.setTextFill(Color.RED);
			}
		});

		delete.setOnAction(e -> {
			if (editSet) {
				deleteSelectedSet();
			} else {
				deleteSelectedInstance();
			}
			window.close();
		});

		cancel.setOnAction(e -> {
			window.close();
		});

		VBox options = new VBox(20);
		options.getChildren().addAll(header, meetingType, startDateLabel, startDate, toEndOfTerm, endDateLabel, endDate,
				hour, selectTimes, rep, chooseRepeat, loc, locField, confirm, delete, cancel, error);
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
	private void confirmChanges(LocalDate startDate, String type, Time start, Time end, String loc) {

		deleteSelectedInstance();

		MeetingSet ms = new MeetingSet();

		ms.addMeeting(new Meeting(type, startDate, LocalTime.of(start.hour, start.minute),
				LocalTime.of(end.hour, end.minute), loc));

		pc.addMeetingSet(this.meetingCourse, ms, "Never");
	}

	private void confirmChanges(LocalDate startDate, String type, Time start, Time end, String loc, LocalDate endDate,
			String repeat) {

		deleteSelectedSet();

		MeetingSet meetingSet = new MeetingSet(new ArrayList<>());

		LocalDate cur = startDate;

		while (cur.isBefore(endDate) || cur.equals(endDate)) {

			Meeting m = new Meeting(type, cur, LocalTime.of(start.hour, start.minute),
					LocalTime.of(end.hour, end.minute), loc);

			meetingSet.addMeeting(m);

			switch (repeat) {

			case "Never":
				/* Ensure that loop is broken out of. */
				cur = endDate.plusDays(1);
				break;

			case "Weekly":
				cur = cur.plusDays(7);
				break;

			case "Bi-Weekly":
				cur = cur.plusDays(14);
				break;

			case "Monthly":
				cur = cur.plusDays(28);
				break;
			}
		}

		pc.addMeetingSet(this.meetingCourse, meetingSet, repeat);
	}

	private void deleteSelectedSet() {
		pc.deleteMeetingSet(this.selectedSet);
	}

	private void deleteSelectedInstance() {
		/*
		 * Create a new MeetingSet containing the single meeting and delete
		 * that.
		 */
		MeetingSet deleted = new MeetingSet(new ArrayList<Meeting>());
		deleted.addMeeting(selected);

		this.selectedSet.getMeetings().remove(selected);
		pc.deleteMeetingSet(deleted);
	}
}
