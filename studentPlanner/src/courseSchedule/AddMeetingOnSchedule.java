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
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Course;
import model.CourseMeeting;
import model.Meeting;
import model.MeetingSet;

/**
 * The Class AddMeetingOnSchedule.
 * 
 * This is the window that appears when the user clicks on an unoccupied time
 * slot in the CourseSchedule.
 */
public class AddMeetingOnSchedule {

	private CourseScheduleController pc;
	private LocalDateTime selected;
	private ComboBox<Time> startTime;
	private ComboBox<Time> endTime;
	private Label error;

	public AddMeetingOnSchedule(LocalDateTime cell, CourseScheduleController pc) {
		this.selected = cell;
		this.pc = pc;
		display();
	}

	private void display() {

		/* Window set-up */
		Stage window = new Stage();
		window.initModality(Modality.APPLICATION_MODAL);
		window.setTitle("Add Meeting");
		window.getIcons().add(new Image(Driver.class.getResourceAsStream("icon.png")));

		ObservableList<String> days = FXCollections.observableArrayList();
		days.addAll("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday");
		ObservableList<String> types = FXCollections.observableArrayList();

		if (this.pc.currentlySelectedTermCoursesExist()) {
			types.addAll(CourseMeeting.TYPES);
		} else {
			types.addAll(Meeting.TYPES);
		}

		ObservableList<Time> times = FXCollections.observableArrayList();
		for (int i = 0; i < 24; i++) {
			for (int j = 0; j < 31; j += 30) {
				times.add(new Time(i, j));
			}
		}

		Label courseLabel = new Label("Course:");

		ChoiceBox<Course> chooseCourse = new ChoiceBox<>(
				FXCollections.observableArrayList(pc.profile.currentlySelectedTerm.courses));
		Style.setChoiceBoxStyle(chooseCourse);

		TextField titleField = new TextField();
		titleField.setVisible(false);

		CheckBox isCourseMeeting = new CheckBox();
		isCourseMeeting.setText("This meeting is for a course.");
		isCourseMeeting.setSelected(true);

		Label typeLabel = new Label("Type:");
		ChoiceBox<String> meetingType = new ChoiceBox<>(types);
		Style.setChoiceBoxStyle(meetingType);

		ColorPicker chooseColor = new ColorPicker();
		chooseColor.setValue(Style.randomColor());
		chooseColor.setMinHeight(35);

		isCourseMeeting.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldVal, Boolean newVal) {

				if (newVal.booleanValue()) {

					titleField.setVisible(false);
					courseLabel.setVisible(true);
					chooseCourse.setVisible(true);
					chooseColor.setVisible(false);
					meetingType.setItems(FXCollections.observableArrayList(CourseMeeting.TYPES));
					meetingType.setValue(FXCollections.observableArrayList(CourseMeeting.TYPES).get(0));

				} else {

					titleField.setVisible(true);
					courseLabel.setVisible(false);
					chooseCourse.setVisible(false);
					chooseColor.setVisible(true);
					meetingType.setItems(FXCollections.observableArrayList(Meeting.TYPES));
					meetingType.setValue(FXCollections.observableArrayList(Meeting.TYPES).get(0));
				}
			}
		});

		if (this.pc.currentlySelectedTermCoursesExist()) {
			chooseCourse.setValue(pc.profile.currentlySelectedTerm.courses.get(0));
			chooseColor.setVisible(false);
		} else {
			chooseCourse.setVisible(false);
			isCourseMeeting.setSelected(false);
			isCourseMeeting.setVisible(false);
			titleField.setVisible(true);
		}

		TextField other = new TextField();
		other.setPromptText("Specify Meeting Type");
		other.setVisible(false);

		meetingType.valueProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldType, String newType) {

				if (newType != null) {

					if (newType.equals("Other")) {
						other.setVisible(true);
						titleField.setPromptText("Enter Meeting Name");
					} else {
						other.setVisible(false);
						titleField.setPromptText("Enter " + newType + " Name");
					}
				}
			}
		});

		if (isCourseMeeting.isSelected()) {
			meetingType.setValue(CourseMeeting.TYPES[0]);
		} else {
			meetingType.setValue(Meeting.TYPES[0]);
			titleField.setPromptText("Enter " + meetingType.getValue() + " Name");
		}

		Label startDateLabel = new Label("Start Date:");
		DatePicker startDate = new DatePicker();
		startDate.setValue(selected.toLocalDate());

		Label endDateLabel = new Label("End Date:");
		CheckBox toEndOfTerm = new CheckBox();
		toEndOfTerm.setSelected(true);
		toEndOfTerm.setText("To End of Term");

		DatePicker endDate = new DatePicker();
		endDate.setValue(pc.profile.currentlySelectedTerm.end);
		// endDate.setVisible(false);

		toEndOfTerm.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldVal, Boolean newVal) {
				if (newVal) {
					// endDate.setVisible(false);
					endDate.setValue(pc.profile.currentlySelectedTerm.end);
				} else {
					endDate.setVisible(true);
				}
			}
		});

		endDate.valueProperty().addListener(new ChangeListener<LocalDate>() {

			@Override
			public void changed(ObservableValue<? extends LocalDate> observable, LocalDate oldValue,
					LocalDate newValue) {
				if (newValue.isAfter(pc.profile.currentlySelectedTerm.end)) {
					new Alert(AlertType.ERROR,
							"The date selected is beyond the end date of the selected term."
									+ " You can only add meetings to one term at a time."
									+ " It has been changed to the end of term.").showAndWait();
					endDate.setValue(pc.profile.currentlySelectedTerm.end);
				}
			}
		});

		startDate.valueProperty().addListener(new ChangeListener<LocalDate>() {
			@Override
			public void changed(ObservableValue<? extends LocalDate> observable, LocalDate oldValue,
					LocalDate newValue) {
				if (newValue.isAfter(pc.profile.currentlySelectedTerm.end)) {
					new Alert(AlertType.ERROR,
							"The date selected is beyond the end date of the selected term."
									+ " You can only add meetings to one term at a time."
									+ " It has been changed to the start of term.").showAndWait();
					startDate.setValue(pc.profile.currentlySelectedTerm.start);
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
					endDate.setVisible(false);
				} else {
					toEndOfTerm.setVisible(true);
					endDateLabel.setVisible(true);
					endDate.setVisible(true);
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

					String mt = "";

					if (meetingType.getValue().equals("Other")) {
						mt = other.getText();
					} else {
						mt = meetingType.getValue();
					}

					if (isCourseMeeting.isSelected()) {

						confirm(chooseCourse.getValue(), startDate.getValue(), mt, startTime.getValue(),
								endTime.getValue(), locField.getText(), endDate.getValue(), chooseRepeat.getValue());
					} else {

						confirm(titleField.getText(), Style.colorToHex(chooseColor.getValue()), startDate.getValue(),
								mt, startTime.getValue(), endTime.getValue(), locField.getText(), endDate.getValue(),
								chooseRepeat.getValue());
					}

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

		HBox decisions = new HBox(20);
		decisions.getChildren().addAll(cancel, confirm);

		decisions.setMinHeight(40);

		VBox courseDecision = new VBox(20);
		courseDecision.getChildren().addAll(courseLabel, chooseCourse, isCourseMeeting);

		VBox typeDecision = new VBox(20);
		typeDecision.getChildren().addAll(typeLabel, meetingType, other);

		VBox startDecision = new VBox(20);
		startDecision.getChildren().addAll(startDateLabel, startDate);

		VBox endDecision = new VBox(20);
		endDecision.getChildren().addAll(endDateLabel, endDate, toEndOfTerm);

		HBox dateDecision = new HBox(20);
		dateDecision.getChildren().addAll(startDecision, endDecision);

		HBox courseAndType = new HBox(20);
		courseAndType.getChildren().addAll(courseDecision, typeDecision);

		locField.setPromptText("Enter Meeting Location");

		VBox titleDecision = new VBox(20);
		titleDecision.getChildren().addAll(titleField);

		VBox body = new VBox();
		ScrollPane scroll = new ScrollPane();

		VBox options = new VBox(20);
		options.getChildren().addAll(header, courseAndType, titleField, chooseColor, dateDecision, hour, selectTimes,
				locField, rep, chooseRepeat, decisions);

		scroll.setContent(options);
		Style.addPadding(scroll);

		body.getChildren().add(scroll);
		body.setPrefHeight(732);
		body.setPrefWidth(476);

		Scene scene = new Scene(body);
		window.setScene(scene);
		window.showAndWait();
	}

	private void confirm(Course course, LocalDate startDate, String type, Time start, Time end, String loc,
			LocalDate endDate, String repeat) {

		MeetingSet meetingSet = new MeetingSet(new ArrayList<>());

		LocalDate cur = startDate;

		while (cur.isBefore(endDate) || cur.equals(endDate)) {

			CourseMeeting m = new CourseMeeting(course, type, cur, LocalTime.of(start.hour, start.minute),
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

		pc.addMeetingSet(meetingSet, repeat);
	}

	private void confirm(String name, String color, LocalDate startDate, String type, Time start, Time end, String loc,
			LocalDate endDate, String repeat) {

		MeetingSet meetingSet = new MeetingSet(new ArrayList<>());

		LocalDate cur = startDate;

		while (cur.isBefore(endDate) || cur.equals(endDate)) {

			Meeting m = new Meeting(name, color, type, cur, LocalTime.of(start.hour, start.minute),
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

		pc.addMeetingSet(meetingSet, repeat);
	}
}