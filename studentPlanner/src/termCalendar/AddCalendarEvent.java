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
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
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
 * The Class AddCalendarEvent.
 */
public class AddCalendarEvent {

	private LocalDate date;
	private TermCalendarController controller;
	private ComboBox<Time> endTimes;
	private boolean updateEndTimes = false;

	private static String[] courseEventTypes = { "Assignment", "Test", "Other" };

	/**
	 * Instantiates a new adds the calendar event.
	 *
	 * @param date
	 *            the date
	 * @param controller
	 *            the controller
	 */
	public AddCalendarEvent(LocalDate date, TermCalendarController controller) {
		this.date = date;
		this.controller = controller;
		display();
	}

	/**
	 * Display.
	 */
	private void display() {

		/* Window set-up */
		Stage window = new Stage();
		window.initModality(Modality.APPLICATION_MODAL);
		window.setTitle("Add Calendar Event");
		window.getIcons().add(new Image(Driver.class.getResourceAsStream("icon.png")));

		Label dateLabel = new Label(date.toString());
		ObservableList<String> types = FXCollections.observableArrayList();
		types.addAll(courseEventTypes);

		/* Times drop-down */
		ObservableList<Time> times = FXCollections.observableArrayList();
		for (int i = 0; i < 24; i++) {
			for (int j = 0; j < 31; j += 30) {
				times.add(new Time(i, j));
			}
		}

		ObservableList<Course> courses = FXCollections.observableArrayList();
		for (Course c : controller.profile.currentlySelectedTerm.courses) {
			courses.add(c);
		}

		ChoiceBox<Course> cChoice = new ChoiceBox<>(courses);
		Style.setChoiceBoxStyle(cChoice);

		if (courses.size() == 0) {
			cChoice.setVisible(false);
			cChoice.setManaged(false);

			types.removeAll("Test", "Assignment");
		} else {
			cChoice.setValue(courses.get(0));
		}

		ChoiceBox<String> typeChoice = new ChoiceBox<>(types);
		Style.setChoiceBoxStyle(typeChoice);

		CheckBox personal = new CheckBox();
		personal.setText("Personal Event");

		personal.setSelected(false);

		ColorPicker chooseColor = new ColorPicker();
		chooseColor.setValue(Style.randomColor());
		chooseColor.setMinHeight(35);

		HBox recentColors = new HBox(5);

		Label recent = new Label("Recent Colors:");
		recentColors.getChildren().add(recent);

		chooseColor.setVisible(false);
		chooseColor.setManaged(false);

		recentColors.setVisible(false);
		recentColors.setManaged(false);

		for (int i = 0; i < this.controller.profile.recentlyUsedColors.size(); i++) {

			Rectangle r = new Rectangle(30, 30);
			r.setFill(Color.web(this.controller.profile.recentlyUsedColors.get(i)));

			final int index = i;

			r.setOnMouseClicked(e -> {
				chooseColor.setValue(Color.web(this.controller.profile.recentlyUsedColors.get(index)));
			});

			recentColors.getChildren().add(r);
		}

		TextField name = new TextField();
		name.setPromptText(typeChoice.getValue() + " title");
		TextField weight = new TextField();
		weight.setPromptText(typeChoice.getValue() + " weight %");
		HBox selectTime = new HBox();
		Label startTime = new Label("Time:");
		Label dash = new Label(" - ");
		ComboBox<Time> startTimes = new ComboBox<>(times);
		Style.setComboBoxStyle(startTimes);

		endTimes = new ComboBox<>();
		Style.setComboBoxStyle(endTimes);
		selectTime.getChildren().addAll(startTimes, dash, endTimes);

		dash.setVisible(false);
		dash.setManaged(false);

		endTimes.setVisible(false);
		endTimes.setManaged(false);

		CheckBox timeRange = new CheckBox();
		timeRange.setText("Time Range");

		timeRange.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldVal, Boolean newVal) {
				if (newVal) {
					endTimes.setVisible(true);
					endTimes.setManaged(true);

					dash.setVisible(true);
					dash.setManaged(true);

					startTimes.getSelectionModel().selectNext();
					updateEndTimes = true;
					startTimes.getSelectionModel().selectPrevious();
				} else {
					endTimes.setVisible(false);
					endTimes.setManaged(false);

					dash.setVisible(false);
					dash.setManaged(false);

					updateEndTimes = false;
				}
			}
		});

		typeChoice.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number old, Number current) {

				name.setPromptText(types.get(current.intValue()) + " title");

				if (types.get(current.intValue()).equals("Personal")) {

				} else if (types.get(current.intValue()).equals("Assignment")) {

					cChoice.setVisible(true);
					cChoice.setManaged(true);

					weight.setVisible(true);
					weight.setManaged(true);

					startTime.setText("Due Time:");
					timeRange.setSelected(false);

				} else if (types.get(current.intValue()).equals("Test")) {

					cChoice.setVisible(true);
					cChoice.setManaged(true);

					weight.setVisible(true);
					weight.setManaged(true);

					startTime.setText("Time:");
					timeRange.setSelected(true);
				}

				weight.setPromptText(types.get(current.intValue()) + " weight %");
			}
		});

		personal.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldVal, Boolean newVal) {
				if (newVal) {
					name.setPromptText("Event name");
					chooseColor.setVisible(true);
					chooseColor.setManaged(true);

					recentColors.setVisible(true);
					recentColors.setManaged(true);

					startTime.setText("Time:");
					cChoice.setVisible(false);
					cChoice.setManaged(false);

					typeChoice.setVisible(false);
					typeChoice.setManaged(false);

					weight.setVisible(false);
					weight.setManaged(false);

				} else {
					cChoice.setVisible(true);
					cChoice.setManaged(true);

					chooseColor.setVisible(false);
					chooseColor.setManaged(false);

					recentColors.setVisible(false);
					recentColors.setManaged(false);

					typeChoice.setVisible(true);
					typeChoice.setManaged(true);

					typeChoice.setValue(types.get(0));
					weight.setVisible(true);
					weight.setManaged(true);
				}
			}
		});

		typeChoice.setValue(types.get(0));

		/* Update selectable end-times whenever start-times are altered. */
		startTimes.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number old, Number current) {
				if (updateEndTimes) {
					selectTime.getChildren().remove(endTimes);
					ObservableList<Time> newEndTimes = FXCollections.observableArrayList();
					newEndTimes.addAll(times.subList(current.intValue() + 1, times.size()));
					endTimes = new ComboBox<>(newEndTimes);
					Style.setComboBoxStyle(endTimes);
					selectTime.getChildren().add(endTimes);
					endTimes.setDisable(false);
				}
			}
		});

		startTimes.setValue(times.get(17));

		Label error = new Label("");
		Button addEvent = new Button("Add Event");
		Style.setButtonStyle(addEvent);

		addEvent.setOnAction(e -> {
			boolean success = false;
			if (timeRange.isSelected()) {

				if (personal.isSelected()) {

					CalendarEvent add = new CalendarEvent(name.getText(), Style.colorToHex(chooseColor.getValue()),
							LocalDateTime.of(date,
									LocalTime.of(startTimes.getValue().hour, startTimes.getValue().minute)),
							LocalDateTime.of(date, LocalTime.of(endTimes.getValue().hour, endTimes.getValue().minute)));
					controller.addEvent(null, add, date);
					success = true;

				} else {

					try {

						/* Add course event */

						String type = typeChoice.getValue();
						int typeVal;
						if (type.equals("Assignment")) {
							typeVal = CourseEvent.TYPES.ASSIGNMENT.val;
						} else if (type.equals("Test")) {
							typeVal = CourseEvent.TYPES.TEST.val;
						} else {
							typeVal = CourseEvent.TYPES.OTHER.val;
						}

						CourseEvent add = new CourseEvent(name.getText(), cChoice.getValue().color, typeVal,
								LocalDateTime.of(date,
										LocalTime.of(startTimes.getValue().hour, startTimes.getValue().minute)),
								LocalDateTime.of(date,
										LocalTime.of(endTimes.getValue().hour, endTimes.getValue().minute)),
								Double.parseDouble(weight.getText()));
						controller.addEvent(cChoice.getValue(), add, date);
						success = true;

					} catch (NumberFormatException er) {
						error.setText("Weight must be a valid decimal number.");
					}
				}

			} else {

				if (personal.isSelected()) {

					CalendarEvent add = new CalendarEvent(name.getText(), Style.colorToHex(chooseColor.getValue()),
							LocalDateTime.of(date,
									LocalTime.of(startTimes.getValue().hour, startTimes.getValue().minute)),
							LocalDateTime.of(date,
									LocalTime.of(startTimes.getValue().hour, startTimes.getValue().minute)));
					controller.addEvent(null, add, date);
					success = true;

				} else {

					try {

						/* Add course event */

						String type = typeChoice.getValue();
						int typeVal;
						if (type.equals("Assignment")) {
							typeVal = CourseEvent.TYPES.ASSIGNMENT.val;
						} else if (type.equals("Test")) {
							typeVal = CourseEvent.TYPES.TEST.val;
						} else {
							typeVal = CourseEvent.TYPES.OTHER.val;
						}

						CourseEvent add = new CourseEvent(name.getText(), cChoice.getValue().color, typeVal,
								LocalDateTime.of(date,
										LocalTime.of(startTimes.getValue().hour, startTimes.getValue().minute)),
								LocalDateTime.of(date,
										LocalTime.of(startTimes.getValue().hour, startTimes.getValue().minute)),
								Double.parseDouble(weight.getText()));
						controller.addEvent(cChoice.getValue(), add, date);
						success = true;

					} catch (NumberFormatException er) {
						error.setText("Weight must be a valid decimal number.");
					}
				}
			}
			if (success) {
				window.close();
			}
		});

		HBox courseAndType = new HBox(10);
		courseAndType.getChildren().addAll(cChoice, typeChoice);

		HBox colorDecision = new HBox(10);
		colorDecision.getChildren().addAll(chooseColor, recentColors);

		VBox options = new VBox(15);
		options.getChildren().addAll(dateLabel, courseAndType, personal, chooseColor, recentColors, name, startTime,
				selectTime, timeRange, weight, addEvent, error);

		options.setMinWidth(300);

		Style.addPadding(options);
		Scene scene = new Scene(options);
		window.setScene(scene);
		window.showAndWait();
	}
}
