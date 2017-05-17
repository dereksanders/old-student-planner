package courseSchedule;

import java.time.LocalTime;

import core.Meeting;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AddMeeting {

	public static Meeting add;
	public static ComboBox<Time> startTime;
	public static ComboBox<Time> endTime;

	public static Meeting display() {
		ObservableList<String> days = FXCollections.observableArrayList();
		days.addAll("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday");
		ObservableList<String> types = FXCollections.observableArrayList();
		types.addAll("Lecture", "Tutorial", "Lab", "Seminar");
		ObservableList<Time> times = FXCollections.observableArrayList();
		for (int i = 0; i < 24; i++) {
			for (int j = 0; j < 31; j += 30) {
				times.add(new Time(i, j));
			}
		}
		ChoiceBox<String> meetingType = new ChoiceBox<>(types);
		meetingType.setValue(types.get(0));
		ChoiceBox<String> daysChoose = new ChoiceBox<>(days);
		HBox selectTimes = new HBox();
		startTime = new ComboBox<>(times);
		Label min = new Label(" - ");
		endTime = new ComboBox<>();
		endTime.setVisible(false);
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
		startTime.setValue(times.get(17));
		Label header = new Label("Enter Meeting Info");
		header.setFont(Planner.h1);
		Label day = new Label("Day:");
		Label hour = new Label("Time:");
		Label loc = new Label("Location:");
		TextField locField = new TextField();
		Label error = new Label();
		Stage window = new Stage();
		window.initModality(Modality.APPLICATION_MODAL);
		window.setTitle("Add Meeting");
		Button confirm = new Button("Confirm changes");
		Button cancel = new Button("Cancel");
		confirm.setOnAction(e -> {
			if (meetingType.getValue() != null && daysChoose.getValue() != null && startTime.getValue() != null
					&& endTime.getValue() != null) {
				confirmChanges(meetingType.getValue(), daysChoose.getValue(), startTime.getValue(), endTime.getValue(),
						locField.getText());
				window.close();
			} else {
				error.setText("Error: You must fill out all fields.");
				error.setTextFill(Color.RED);
			}
		});
		cancel.setOnAction(e -> {
			add = null;
			window.close();
		});
		VBox layout = new VBox(20);
		layout.getChildren().addAll(header, meetingType, day, daysChoose, hour, selectTimes, loc, locField, confirm,
				cancel, error);
		Scene scene = new Scene(layout);
		window.setScene(scene);
		window.showAndWait();
		return add;
	}

	private static void confirmChanges(String type, String day, Time start, Time end, String loc) {
		add = new Meeting(type, LocalTime.of(start.hour, start.minute), LocalTime.of(end.hour, end.minute), day, loc);
	}
}
