package termCalendar;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import core.CalendarEvent;
import core.Course;
import core.Planner;
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
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AddCalendarEvent {

	private static ComboBox<Time> endTimes;

	public static void display(LocalDate d) {
		Stage window = new Stage();
		window.initModality(Modality.APPLICATION_MODAL);
		window.setTitle("Add Calendar Event");
		window.getIcons().add(new Image(Planner.class.getResourceAsStream("icon.png")));
		Label date = new Label(d.toString());
		ObservableList<String> types = FXCollections.observableArrayList();
		types.addAll("Deliverable", "Personal");
		ObservableList<Time> times = FXCollections.observableArrayList();
		for (int i = 0; i < 24; i++) {
			for (int j = 0; j < 31; j += 30) {
				times.add(new Time(i, j));
			}
		}
		ObservableList<Course> courses = FXCollections.observableArrayList();
		for (Course c : Planner.active.currentlySelectedTerm.courses) {
			courses.add(c);
		}

		ChoiceBox<Course> cChoice = new ChoiceBox<>(courses);
		if (courses.size() == 0) {
			cChoice.setVisible(false);
			types.removeAll("Deliverable");
		} else {
			cChoice.setValue(courses.get(0));
		}
		ChoiceBox<String> typeChoice = new ChoiceBox<>(types);
		TextField name = new TextField();
		name.setPromptText(typeChoice.getValue() + " title");
		TextField weight = new TextField();
		weight.setPromptText(typeChoice.getValue() + " weight %");
		HBox selectTime = new HBox();
		Label startTime = new Label("Time:");
		Label dash = new Label(" - ");
		ComboBox<Time> startTimes = new ComboBox<>(times);
		endTimes = new ComboBox<>();
		selectTime.getChildren().addAll(startTimes, dash, endTimes);
		startTimes.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number old, Number current) {
				selectTime.getChildren().remove(endTimes);
				ObservableList<Time> newEndTimes = FXCollections.observableArrayList();
				newEndTimes.addAll(times.subList(current.intValue() + 1, times.size()));
				endTimes = new ComboBox<>(newEndTimes);
				selectTime.getChildren().add(endTimes);
				endTimes.setDisable(false);
			}
		});
		startTimes.setValue(times.get(17));
		typeChoice.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number old, Number current) {
				name.setPromptText(types.get(current.intValue()) + " title");
				if (types.get(current.intValue()).equals("Personal")) {
					startTime.setText("Time:");
					cChoice.setVisible(false);
					weight.setVisible(false);
					endTimes.setVisible(true);
					dash.setVisible(true);
				} else if (types.get(current.intValue()).equals("Deliverable")) {
					cChoice.setVisible(true);
					weight.setVisible(true);
					startTime.setText("Due Time:");
					endTimes.setVisible(false);
					dash.setVisible(false);
				}
				weight.setPromptText(types.get(current.intValue()) + " weight %");
			}
		});
		typeChoice.setValue(types.get(0));
		Button addEvent = new Button("Add Event");
		Label error = new Label("");
		addEvent.setOnAction(e -> {
			boolean success = false;
			if (typeChoice.getValue().equals("Deliverable")) {
				try {
					CalendarEvent add = new CalendarEvent(name.getText(),
							LocalDateTime.of(d, LocalTime.of(startTimes.getValue().hour, startTimes.getValue().minute)),
							Double.parseDouble(weight.getText()), cChoice.getValue().colour);
					cChoice.getValue().deliverables.add(add);
					Planner.active.dateEvents.put(d, add);
					success = true;
				} catch (NumberFormatException er) {
					error.setText("Weight must be a valid decimal number.");
				}
			} else if (typeChoice.getValue().equals("Personal")) {
				CalendarEvent add = new CalendarEvent(name.getText(),
						LocalDateTime.of(d, LocalTime.of(startTimes.getValue().hour, startTimes.getValue().minute)),
						LocalDateTime.of(d, LocalTime.of(endTimes.getValue().hour, endTimes.getValue().minute)));
				Planner.active.dateEvents.put(d, add);
				success = true;
			}
			if (success) {
				TermCalendar.redrawCalendars();
				window.close();
			}
		});
		VBox options = new VBox(20);
		options.getChildren().addAll(date, cChoice, typeChoice, name, startTime, selectTime, weight, addEvent, error);
		Scene scene = new Scene(options);
		window.setScene(scene);
		window.showAndWait();
	}
}
