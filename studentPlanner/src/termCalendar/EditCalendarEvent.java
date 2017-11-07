package termCalendar;

import java.time.LocalDate;
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
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
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
	private DatePicker chooseDate;
	private HBox colorDecision;
	private ColorPicker chooseColor;
	private HBox recentColors;
	private Label time;
	private ComboBox<Time> startTime;
	private Label dash;
	private ComboBox<Time> endTime;
	private Label weightTitle;
	private TextField weight;
	private Label error;
	private Label current;

	private LocalDate date;
	private TermCalendarController controller;

	/**
	 * Instantiates a new edits the calendar event.
	 *
	 * @param date
	 *            the date
	 * @param controller
	 *            the controller
	 */
	public EditCalendarEvent(LocalDate date, TermCalendarController controller) {
		this.date = date;
		this.controller = controller;
		display();
	}

	public EditCalendarEvent(CalendarEvent currentlySelected, LocalDate date, TermCalendarController controller) {

		this.currentlySelected = currentlySelected;
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
		for (CalendarEvent e : controller.profile.currentlySelectedTerm.dateEvents.get(date)) {
			events.add(e);
		}

		ChoiceBox<CalendarEvent> chooseEvent = new ChoiceBox<>(events);
		Style.setChoiceBoxStyle(chooseEvent);
		chooseEvent.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldIndex, Number newIndex) {
				updateCurrentlySelected(events.get(newIndex.intValue()));
			}
		});

		current = new Label();
		name = new TextField();
		name.setPromptText("Event Name");

		chooseDate = new DatePicker();
		chooseDate.setValue(date);

		chooseColor = new ColorPicker();

		recentColors = new HBox(5);
		Label recent = new Label("Recent Colors:");
		recentColors.getChildren().add(recent);

		colorDecision = new HBox(10);
		colorDecision.getChildren().addAll(chooseColor, recentColors);

		for (int i = 0; i < this.controller.profile.recentlyUsedColors.size(); i++) {

			Rectangle r = new Rectangle(30, 30);
			r.setFill(Color.web(this.controller.profile.recentlyUsedColors.get(i)));

			final int index = i;

			r.setOnMouseClicked(e -> {
				chooseColor.setValue(Color.web(this.controller.profile.recentlyUsedColors.get(index)));
			});

			recentColors.getChildren().add(r);
		}

		time = new Label("Time: ");
		HBox selectTimes = new HBox(20);
		startTime = new ComboBox<>(times);
		Style.setComboBoxStyle(startTime);
		dash = new Label(" - ");
		endTime = new ComboBox<>(times);
		Style.setComboBoxStyle(endTime);
		selectTimes.getChildren().addAll(startTime, dash, endTime);
		startTime.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number old, Number current) {

				if (currentlySelected != null && !currentlySelected.start.equals(currentlySelected.end)) {

					selectTimes.getChildren().remove(endTime);
					ObservableList<Time> newEndTimes = FXCollections.observableArrayList();
					newEndTimes.addAll(times.subList(current.intValue() + 1, times.size()));
					endTime = new ComboBox<>(newEndTimes);
					Style.setComboBoxStyle(endTime);
					selectTimes.getChildren().add(endTime);
				}
			}
		});
		startTime.setValue(times.get(17));
		weightTitle = new Label("Weight:");
		weight = new TextField();
		weight.setPromptText("Enter weight %");
		error = new Label();
		chooseEvent.setValue(events.get(0));

		Button delete = new Button("Delete Event");
		Style.setButtonStyle(delete);
		delete.setOnAction(e -> {
			controller.deleteEvent(controller.profile.currentlySelectedTerm.courseColors.get(currentlySelected.color),
					currentlySelected, date);
			window.close();
		});

		Button confirm = new Button("Confirm Changes");
		Style.setButtonStyle(confirm);
		confirm.setOnAction(e -> {
			confirmChanges(date);
			window.close();
		});

		HBox decisions = new HBox(10);
		decisions.getChildren().addAll(delete, confirm);

		VBox options = new VBox(15);
		options.getChildren().addAll(chooseEvent, name, chooseDate, colorDecision, time, selectTimes, weightTitle,
				weight, decisions, error);
		Style.addPadding(options);
		Scene scene = new Scene(options);
		window.setScene(scene);
		window.showAndWait();
	}

	/**
	 * Confirm changes.
	 *
	 * @param date
	 *            the date
	 */
	private void confirmChanges(LocalDate date) {

		try {

			if (currentlySelected.start.equals(currentlySelected.end)) {

				if (currentlySelected instanceof CourseEvent) {

					CourseEvent edited = new CourseEvent(name.getText(), currentlySelected.color,
							((CourseEvent) currentlySelected).type,
							LocalDateTime.of(chooseDate.getValue(),
									LocalTime.of(startTime.getValue().hour, startTime.getValue().minute)),
							LocalDateTime.of(chooseDate.getValue(),
									LocalTime.of(startTime.getValue().hour, startTime.getValue().minute)),
							Double.parseDouble(weight.getText()));
					Course eventCourse = controller.profile.currentlySelectedTerm.courseColors
							.get(currentlySelected.color);
					controller.deleteEvent(eventCourse, currentlySelected, date);
					controller.addEvent(eventCourse, edited, chooseDate.getValue());

				} else {

					CalendarEvent edited = new CalendarEvent(name.getText(), Style.colorToHex(chooseColor.getValue()),
							LocalDateTime.of(chooseDate.getValue(),
									LocalTime.of(startTime.getValue().hour, startTime.getValue().minute)),
							LocalDateTime.of(chooseDate.getValue(),
									LocalTime.of(startTime.getValue().hour, startTime.getValue().minute)));
					controller.deleteEvent(null, currentlySelected, date);
					controller.addEvent(null, edited, chooseDate.getValue());

				}

			} else {

				if (currentlySelected instanceof CourseEvent) {

					CourseEvent edited = new CourseEvent(name.getText(), currentlySelected.color,
							((CourseEvent) currentlySelected).type,
							LocalDateTime.of(chooseDate.getValue(),
									LocalTime.of(startTime.getValue().hour, startTime.getValue().minute)),
							LocalDateTime.of(chooseDate.getValue(),
									LocalTime.of(endTime.getValue().hour, endTime.getValue().minute)),
							Double.parseDouble(weight.getText()));
					Course eventCourse = controller.profile.currentlySelectedTerm.courseColors
							.get(currentlySelected.color);
					controller.deleteEvent(eventCourse, currentlySelected, date);
					controller.addEvent(eventCourse, edited, chooseDate.getValue());

				} else {

					CalendarEvent edited = new CalendarEvent(name.getText(), Style.colorToHex(chooseColor.getValue()),
							LocalDateTime.of(chooseDate.getValue(),
									LocalTime.of(startTime.getValue().hour, startTime.getValue().minute)),
							LocalDateTime.of(chooseDate.getValue(),
									LocalTime.of(endTime.getValue().hour, endTime.getValue().minute)));
					controller.deleteEvent(null, currentlySelected, date);
					controller.addEvent(null, edited, chooseDate.getValue());
				}
			}

		} catch (NumberFormatException e) {

			error.setText("Weight must be a valid decimal number.");
		}
	}

	/**
	 * Update currently selected.
	 *
	 * @param e
	 *            the e
	 */
	private void updateCurrentlySelected(CalendarEvent e) {
		currentlySelected = e;
		if (e.start.equals(e.end)) {
			current.setText(currentlySelected.name);
			time.setText("Due Time: ");
			startTime.setValue(new Time(currentlySelected.start.getHour(), currentlySelected.start.getMinute()));

			dash.setVisible(false);
			dash.setManaged(false);

			endTime.setVisible(false);
			endTime.setManaged(false);

		} else {
			current.setText(currentlySelected.name);
			startTime.setValue(new Time(currentlySelected.start.getHour(), currentlySelected.start.getMinute()));

			endTime.setVisible(true);
			endTime.setManaged(true);

			endTime.setValue(new Time(currentlySelected.end.getHour(), currentlySelected.end.getMinute()));

			weight.setVisible(false);
			weight.setManaged(false);
		}
		name.setText(currentlySelected.name);
		weight.setText("" + currentlySelected.weight);

		if (e instanceof CourseEvent) {

			colorDecision.setVisible(false);
			colorDecision.setManaged(false);

			weightTitle.setVisible(true);
			weightTitle.setManaged(true);

			weight.setVisible(true);
			weight.setManaged(true);

		} else {

			colorDecision.setVisible(true);
			colorDecision.setManaged(true);

			chooseColor.setValue(Color.web(e.color));

			weightTitle.setVisible(false);
			weightTitle.setManaged(false);

			weight.setVisible(false);
			weight.setManaged(false);
		}

		chooseDate.setValue(date);
	}
}
