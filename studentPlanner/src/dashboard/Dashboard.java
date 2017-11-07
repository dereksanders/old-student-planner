package dashboard;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.PriorityQueue;

import core.Clock;
import core.Style;
import core.View;
import courseSchedule.TodaysMeetings;
import courseSchedule.TodaysMeetingsController;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.CalendarEvent;
import model.Profile;
import termCalendar.UpcomingEvents;
import termCalendar.UpcomingEventsController;
import utility.Pretty;

public class Dashboard extends View implements Observer {

	Observable profile;
	DashboardController controller;

	private Label title = new Label("Dashboard");
	private UpcomingEvents upcomingEvents;
	private TodaysMeetings todaysMeetings;

	public Dashboard(DashboardController controller) {

		this.controller = controller;
		this.controller.dashboard = this;

		this.profile = controller.profile;
		this.profile.addObserver(this);

		this.mainLayout = initLayout();
	}

	public BorderPane initLayout() {

		BorderPane main = new BorderPane();

		VBox header = new VBox();
		Label date = new Label(Pretty.prettyDate(Clock.now.toLocalDate()));
		Style.setTitleStyle(title);
		header.getChildren().addAll(title, date);

		// main.setTop(header);

		HBox body = new HBox();

		VBox priorities = new VBox();
		VBox allEventCards = new VBox();

		ArrayList<LocalDate> datesWithEvents = new ArrayList<>();

		if (this.controller.profile.termInProgress != null) {

			for (Object d : this.controller.profile.termInProgress.dateEvents.keys) {
				if (d != null && d instanceof LocalDate) {
					datesWithEvents.add((LocalDate) d);
				}
			}
		}

		datesWithEvents.sort(null);

		for (LocalDate d : datesWithEvents) {

			PriorityQueue<CalendarEvent> dq = this.controller.profile.termInProgress.dateEvents.get(d);

			for (CalendarEvent e : dq) {

				EventCard ec = new EventCard(e);

				// TODO: Add drag behaviour to EventCard (should follow mouse movement while
				// being dragged).

				allEventCards.getChildren().add(ec.layout);
			}
		}

		// TODO: Add drop behaviour to priorities.

		body.getChildren().addAll(priorities, allEventCards);

		main.setCenter(body);

		this.todaysMeetings = new TodaysMeetings(new TodaysMeetingsController(this.controller.profile));
		main.setLeft(todaysMeetings.mainLayout);

		this.upcomingEvents = new UpcomingEvents(new UpcomingEventsController(this.controller.profile));
		main.setRight(upcomingEvents.mainLayout);

		main.setPadding(new Insets(0, 0, 0, 20));

		return main;
	}

	@Override
	public void update(Observable arg0, Object arg1) {

		if (arg0 instanceof Profile) {

		}
	}

	@Override
	public void refresh() {
		update(this.profile, null);
	}
}
