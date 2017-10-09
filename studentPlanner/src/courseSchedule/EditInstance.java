package courseSchedule;

import java.time.LocalDate;
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
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Course;
import model.CourseMeeting;
import model.Meeting;
import model.MeetingSet;

public class EditInstance {

	private Meeting selected;
	private CourseScheduleController pc;

	/* GUI elements */
	private ComboBox<Time> startTime;
	private ComboBox<Time> endTime;

	public EditInstance(Meeting selected, CourseScheduleController pc) {
		this.selected = selected;
		this.pc = pc;
		display();
	}

	private void display() {

		/* Window set-up */
		Stage window = new Stage();
		window.initModality(Modality.APPLICATION_MODAL);
		window.setTitle("Edit Meeting");
		window.getIcons().add(new Image(Driver.class.getResourceAsStream("icon.png")));

		Label course = new Label("Course:");
		ObservableList<Course> courses = FXCollections.observableArrayList(pc.profile.currentlySelectedTerm.courses);
		ChoiceBox<Course> chooseCourse = new ChoiceBox<>(courses);
		Style.setChoiceBoxStyle(chooseCourse);

		VBox courseSelection = new VBox(20);
		courseSelection.getChildren().addAll(course, chooseCourse);

		ObservableList<String> types = FXCollections.observableArrayList();

		if (selected instanceof CourseMeeting) {
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

		TextField titleField = new TextField();
		TextField other = new TextField();

		other.setPromptText("Specify Meeting Type");

		if (types.contains(selected.meetingType)) {
			other.setVisible(false);
		}

		Label typeLabel = new Label("Type:");
		ChoiceBox<String> meetingType = new ChoiceBox<>(types);
		Style.setChoiceBoxStyle(meetingType);

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

		ColorPicker chooseColor = new ColorPicker();
		chooseColor.setValue(Color.web(selected.color));
		chooseColor.setMinHeight(35);

		HBox recentColors = new HBox(5);
		Label recent = new Label("Recent Colors:");
		recentColors.getChildren().add(recent);

		for (int i = 0; i < this.pc.profile.recentlyUsedColors.size(); i++) {

			Rectangle r = new Rectangle(30, 30);
			r.setFill(Color.web(this.pc.profile.recentlyUsedColors.get(i)));

			final int index = i;

			r.setOnMouseClicked(e -> {
				chooseColor.setValue(Color.web(this.pc.profile.recentlyUsedColors.get(index)));
			});

			recentColors.getChildren().add(r);
		}

		if (selected instanceof CourseMeeting) {
			chooseCourse.setValue(((CourseMeeting) selected).course);
			titleField.setVisible(false);
			chooseColor.setVisible(false);
			recentColors.setVisible(false);
		} else {
			chooseCourse.setVisible(false);
			course.setVisible(false);
		}

		titleField.setText(selected.name);
		if (types.contains(selected.meetingType)) {
			meetingType.setValue(selected.meetingType);
		} else {
			meetingType.setValue("Other");
			other.setText(selected.meetingType);
		}

		VBox typeSelection = new VBox(20);
		typeSelection.getChildren().addAll(typeLabel, meetingType, other);

		Label startDateLabel = new Label("Date:");
		DatePicker startDate = new DatePicker();
		startDate.setValue(selected.date);

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
		TextField locField = new TextField();
		locField.setPromptText("Enter Meeting Location");
		locField.setText(selected.location);
		Label error = new Label();
		Button confirm = new Button("Confirm changes");
		Style.setButtonStyle(confirm);
		Button delete = new Button("Delete Meeting");
		Style.setButtonStyle(delete);
		Button cancel = new Button("Cancel");
		Style.setButtonStyle(cancel);

		confirm.setOnAction(e -> {

			if (meetingType.getValue() != null && startTime.getValue() != null && endTime.getValue() != null) {

				String mt = "";

				if (meetingType.getValue().equals("Other")) {
					mt = other.getText();
				} else {
					mt = meetingType.getValue();
				}

				if (selected instanceof CourseMeeting) {

					confirmChanges(chooseCourse.getValue(), mt, startDate.getValue(), startTime.getValue(),
							endTime.getValue(), locField.getText());
				} else {

					confirmChanges(titleField.getText(), Style.colorToHex(chooseColor.getValue()), mt,
							startDate.getValue(), startTime.getValue(), endTime.getValue(), locField.getText());
				}
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

		HBox courseAndTypeSelection = new HBox(20);
		courseAndTypeSelection.getChildren().addAll(courseSelection, typeSelection);

		HBox decisions = new HBox(20);
		decisions.getChildren().addAll(cancel, delete, confirm);
		decisions.setMinHeight(40);

		VBox body = new VBox();
		ScrollPane scroll = new ScrollPane();

		HBox colorDecision = new HBox(20);
		colorDecision.getChildren().addAll(chooseColor, recentColors);

		VBox options = new VBox(20);
		options.getChildren().addAll(header, courseAndTypeSelection, titleField, colorDecision, startDateLabel,
				startDate, hour, selectTimes, locField, decisions, error);

		scroll.setContent(options);
		Style.addPadding(scroll);

		body.getChildren().add(scroll);
		body.setPrefHeight(638);
		body.setPrefWidth(462);

		Scene scene = new Scene(body);
		window.setScene(scene);
		window.showAndWait();
	}

	private void confirmChanges(Course course, String type, LocalDate startDate, Time start, Time end, String loc) {

		deleteSelectedInstance();

		MeetingSet ms = new MeetingSet();

		ms.addMeeting(new CourseMeeting(course, type, startDate, LocalTime.of(start.hour, start.minute),
				LocalTime.of(end.hour, end.minute), loc));

		pc.addMeetingSet(ms, MeetingSet.NO_REPEAT);
	}

	private void confirmChanges(String name, String color, String type, LocalDate startDate, Time start, Time end,
			String loc) {

		deleteSelectedInstance();

		MeetingSet ms = new MeetingSet();

		ms.addMeeting(new Meeting(name, color, type, startDate, LocalTime.of(start.hour, start.minute),
				LocalTime.of(end.hour, end.minute), loc));

		pc.addMeetingSet(ms, MeetingSet.NO_REPEAT);
	}

	private void deleteSelectedInstance() {

		this.pc.deleteMeetingFromSet(this.selected);
	}
}
