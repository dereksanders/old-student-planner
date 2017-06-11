package termCalendar;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import core.Driver;
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
import model.CalendarEvent;
import model.Course;
import model.CourseEvent;

/**
 * The Class EditCalendarEvent.
 */
public class EditCalendarEvent {

	private CalendarEvent currentlySelected;
	private TextField name;
	private Label time;
	private ComboBox<Time> startTime;
	private Label dash;
	private ComboBox<Time> endTime;
	private TextField weight;
	private Label error;
	private Label current;

	private LocalDate date;
	private TermCalendarController controller;

	/**
	 * Instantiates a new edits the calendar event.
	 *
	 * @param date the date
	 * @param controller the controller
	 */
	public EditCalendarEvent(LocalDate date, TermCalendarController controller) {
		this.date = date;
		this.controller = controller;
		display();
	}

	/**
	 * Display.
	 */
	private void display() {
		ObservableList<Time> times = FXCollections.observableArrayList();
		for (int i = 0; i < 24; i++) {
			for (int j = 0; j < 31; j += 30) {
				times.add(new Time(i, j));
			}
		}
		currentlySelected = null;
		Stage window = new Stage();
		window.initModality(Modality.APPLICATION_MODAL);
		window.setTitle("Edit Event");
		window.getIcons().add(new Image(Driver.class.getResourceAsStream("icon.png")));
		ObservableList<CalendarEvent> events = FXCollections.observableArrayList();
		for (CalendarEvent e : controller.active.dateEvents.get(date)) {
			events.add(e);
		}
		ChoiceBox<CalendarEvent> chooseEvent = new ChoiceBox<>(events);
		chooseEvent.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldIndex, Number newIndex) {
				updateCurrentlySelected(events.get(newIndex.intValue()));
			}
		});
		current = new Label();
		name = new TextField();
		name.setPromptText("Event Name");
		time = new Label("Time: ");
		HBox selectTimes = new HBox(20);
		startTime = new ComboBox<>(times);
		dash = new Label(" - ");
		endTime = new ComboBox<>(times);
		selectTimes.getChildren().addAll(startTime, dash, endTime);
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
		startTime.setValue(times.get(17));
		weight = new TextField();
		weight.setPromptText("Enter Event Weight");
		error = new Label();
		chooseEvent.setValue(events.get(0));
		Button delete = new Button("Delete Event");
		delete.setOnAction(e -> {
			controller.deleteEvent(controller.active.courseColors.get(Color.web(currentlySelected.colour)).peek(),
					currentlySelected, date);
			window.close();
		});
		Button confirm = new Button("Confirm Changes");
		confirm.setOnAction(e -> {
			confirmChanges(date);
			window.close();
		});
		VBox options = new VBox(20);
		options.getChildren().addAll(chooseEvent, current, name, time, selectTimes, weight, delete, confirm, error);
		Scene scene = new Scene(options);
		window.setScene(scene);
		window.showAndWait();
	}

	/**
	 * Confirm changes.
	 *
	 * @param date the date
	 */
	private void confirmChanges(LocalDate date) {
		try {
			if (currentlySelected instanceof CourseEvent) {
				CourseEvent edited = new CourseEvent(name.getText(), currentlySelected.colour,
						LocalDateTime.of(date, LocalTime.of(startTime.getValue().hour, startTime.getValue().minute)),
						LocalDateTime.of(date, LocalTime.of(startTime.getValue().hour, startTime.getValue().minute)),
						Double.parseDouble(weight.getText()));
				Course eventCourse = controller.active.courseColors.get(Color.web(currentlySelected.colour)).peek();
				controller.deleteEvent(eventCourse, currentlySelected, date);
				controller.addEvent(eventCourse, edited, date);
			} else {
				CalendarEvent edited = new CalendarEvent(name.getText(),
						LocalDateTime.of(date, LocalTime.of(startTime.getValue().hour, startTime.getValue().minute)),
						LocalDateTime.of(date, LocalTime.of(endTime.getValue().hour, endTime.getValue().minute)));
				controller.deleteEvent(null, currentlySelected, date);
				controller.addEvent(null, edited, date);
			}
		} catch (NumberFormatException e) {
			error.setText("Weight must be a valid decimal number.");
		}
	}

	/**
	 * Update currently selected.
	 *
	 * @param e the e
	 */
	private void updateCurrentlySelected(CalendarEvent e) {
		currentlySelected = e;
		if (currentlySelected instanceof CourseEvent) {
			current.setText(currentlySelected.name);
			time.setText("Due Time: ");
			startTime.setValue(new Time(currentlySelected.start.getHour(), currentlySelected.start.getMinute()));
			dash.setVisible(false);
			endTime.setVisible(false);
			weight.setVisible(true);
		} else {
			current.setText("Personal Event");
			startTime.setValue(new Time(currentlySelected.start.getHour(), currentlySelected.start.getMinute()));
			endTime.setVisible(true);
			endTime.setValue(new Time(currentlySelected.end.getHour(), currentlySelected.end.getMinute()));
			weight.setVisible(false);
		}
		name.setText(currentlySelected.name);
		weight.setText("" + currentlySelected.weight);
	}
}
