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

public class EditSet {

	private Course meetingCourse;
	private Meeting selected;
	private MeetingSet selectedSet;
	private MeetingSet pastSelectedSet;
	private CourseScheduleController pc;

	/* GUI elements */
	private ComboBox<Time> startTime;
	private ComboBox<Time> endTime;

	public EditSet(Course meetingCourse, Meeting selected, CourseScheduleController pc, boolean editThisAndFuture) {

		this.meetingCourse = meetingCourse;
		this.selected = selected;
		this.pastSelectedSet = selected.set;

		if (editThisAndFuture) {

			MeetingSet thisAndFuture = new MeetingSet();

			for (int i = selected.set.getMeetings().indexOf(selected); i < selected.set.getMeetings().size(); i++) {
				thisAndFuture.addMeeting(selected.set.getMeetings().get(i));
				selected.set.getMeetings().remove(i);
				i--;
			}

			this.selectedSet = thisAndFuture;

		} else {

			this.selectedSet = selected.set;
		}

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

		Label endDateLabel = new Label("End Date:");
		CheckBox toEndOfTerm = new CheckBox();
		toEndOfTerm.setText("To End of Term");
		toEndOfTerm.setSelected(false);

		DatePicker endDate = new DatePicker();
		endDate.setValue(selectedSet.getEnd());

		toEndOfTerm.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldVal, Boolean newVal) {
				if (newVal) {
					endDate.setVisible(false);
					endDate.setValue(pc.profile.currentlySelectedTerm.end);
				} else {
					endDate.setVisible(true);
				}
			}
		});

		Label rep = new Label("Repeat:");
		ObservableList<String> repeatOptions = FXCollections.observableArrayList(MeetingSet.WEEKLY_REPEAT,
				MeetingSet.BIWEEKLY_REPEAT, MeetingSet.MONTHLY_REPEAT, MeetingSet.NO_REPEAT);
		ChoiceBox<String> chooseRepeat = new ChoiceBox<>(repeatOptions);
		Style.setChoiceBoxStyle(chooseRepeat);
		chooseRepeat.setValue(pastSelectedSet.repeat);

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

				if (endDate.getValue().isAfter(startDate.getValue())
						|| endDate.getValue().equals(startDate.getValue())) {

					confirmChanges(startDate.getValue(), meetingType.getValue(), startTime.getValue(),
							endTime.getValue(), locField.getText(), endDate.getValue(), chooseRepeat.getValue());

					window.close();
				}

			} else {
				error.setText("Error: You must fill out all fields.");
				error.setTextFill(Color.RED);
			}
		});

		delete.setOnAction(e -> {
			deleteSelectedSet();
			window.close();
		});

		cancel.setOnAction(e -> {
			mergeSets();
			window.close();
		});

		window.setOnCloseRequest(e -> {
			mergeSets();
			window.close();
		});

		VBox options = new VBox(20);
		options.getChildren().addAll(header, typeLabel, meetingType, startDateLabel, startDate, toEndOfTerm,
				endDateLabel, endDate, hour, selectTimes, rep, chooseRepeat, loc, locField, confirm, delete, cancel,
				error);
		Style.addPadding(options);
		Scene scene = new Scene(options);
		window.setScene(scene);
		window.showAndWait();
	}

	private void mergeSets() {

		this.pastSelectedSet.getMeetings().addAll(selectedSet.getMeetings());
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

		pc.addMeetingSet(this.meetingCourse, meetingSet, repeat);
	}

	private void deleteSelectedSet() {
		pc.deleteMeetingSet(this.selectedSet);
	}
}
