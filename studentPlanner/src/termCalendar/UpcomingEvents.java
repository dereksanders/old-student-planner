package termCalendar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Observable;
import java.util.Observer;
import java.util.PriorityQueue;

import core.Clock;
import core.Listing;
import core.Style;
import core.View;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import model.CalendarEvent;
import model.CourseEvent;
import model.Profile;
import utility.Pretty;

public class UpcomingEvents extends View implements Observer {

	private UpcomingEventsController controller;
	private Observable profile;

	private VBox upcomingEvents = new VBox(15);
	private Label title = new Label("Upcoming");

	private HBox showWithinThresholdDays = new HBox(5);
	private Label showWithin = new Label("Show events within: ");
	private ComboBox<Integer> chooseThreshold;
	private Label days = new Label("days.");

	private VBox eventsWithinThreshold = new VBox(10);

	public UpcomingEvents(UpcomingEventsController controller) {

		this.controller = controller;

		this.profile = this.controller.profile;
		this.controller.profile.addObserver(this);

		this.mainLayout = initLayout();
	}

	public BorderPane initLayout() {

		BorderPane main = new BorderPane();

		Style.setTitleStyle(title);

		ObservableList<Integer> thresholds = FXCollections.observableArrayList();
		for (int i = 1; i < 31; i++) {
			thresholds.add(i);
		}

		chooseThreshold = new ComboBox<>(thresholds);
		Style.setComboBoxStyle(chooseThreshold);
		chooseThreshold.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldIndex, Number newIndex) {
				controller.setShowWithinThreshold(thresholds.get(newIndex.intValue()));
			}
		});

		chooseThreshold.setValue(this.controller.profile.showWithinThreshold);
		showWithinThresholdDays.getChildren().addAll(showWithin, chooseThreshold, days);
		upcomingEvents.getChildren().addAll(title, showWithinThresholdDays, eventsWithinThreshold);
		upcomingEvents
				.setStyle("-fx-background-color: #fff; -fx-border-width: 1; -fx-border-color: #ccc; -fx-padding: 10;");
		BorderPane.setAlignment(upcomingEvents, Pos.CENTER);

		main.setCenter(upcomingEvents);
		return main;
	}

	private void setUpcomingEvents() {

		boolean eventsExist = false;

		this.chooseThreshold.setValue(controller.profile.showWithinThreshold);

		this.eventsWithinThreshold.getChildren().clear();

		if (controller.profile.currentlySelectedTerm != null) {

			for (int i = 0; i <= controller.profile.showWithinThreshold; i++) {

				PriorityQueue<CalendarEvent> de = controller.profile.currentlySelectedTerm.dateEvents
						.get(Clock.now.toLocalDate().plusDays(i));

				if (de != null && !de.isEmpty()) {

					eventsExist = true;

					Label dateOfEvents = new Label("");

					if (i == 0) {
						dateOfEvents.setText("Today:");
					} else if (i == 1) {
						dateOfEvents.setText("Tomorrow:");
					} else {
						dateOfEvents.setText(Pretty.prettyDate(Clock.now.toLocalDate().plusDays(i)) + ":");
					}

					this.eventsWithinThreshold.getChildren().add(dateOfEvents);

					ArrayList<CalendarEvent> events = new ArrayList<>();
					events.addAll(de);
					events.sort(Collections.reverseOrder());

					for (CalendarEvent e : events) {

						if (e instanceof CourseEvent) {

							this.eventsWithinThreshold.getChildren().add(new Listing(Color.web(e.color),
									((CourseEvent) e).course.toString() + " " + e.toString()).show());

						} else {

							this.eventsWithinThreshold.getChildren()
									.add(new Listing(Color.web(e.color), e.toString()).show());
						}
					}
				}
			}
		}

		if (!eventsExist) {

			this.eventsWithinThreshold.getChildren().add(new Label("No upcoming events!"));
		}
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		if (arg0 instanceof Profile) {
			setUpcomingEvents();
		}
	}

	@Override
	public void refresh() {

		update(this.profile, null);
	}
}
