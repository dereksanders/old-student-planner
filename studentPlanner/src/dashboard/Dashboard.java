package dashboard;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import core.Clock;
import core.Listing;
import core.Style;
import core.View;
import courseSchedule.TodaysMeetings;
import courseSchedule.TodaysMeetingsController;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
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
	private ArrayList<CalendarEvent> termEvents;

	// TODO: What if event within priorities is removed?
	protected ArrayList<CalendarEvent> priorities;
	private VBox priorityList;
	protected ChoiceBox<CalendarEvent> chooseEvent;

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

		VBox todaysPriorities = new VBox(10);
		this.priorities = new ArrayList<>();

		Label dashTitle = new Label("What are your priorities today?");
		Style.setTitleStyle(dashTitle);

		this.termEvents = new ArrayList<>();

		if (this.controller.profile.currentlySelectedTerm != null) {

			for (LocalDate d : this.controller.profile.currentlySelectedTerm.dateEvents.sortedKeys) {

				this.termEvents.addAll(this.controller.profile.currentlySelectedTerm.dateEvents.get(d));
			}
		}

		this.chooseEvent = new ChoiceBox<>();
		Style.setChoiceBoxStyle(chooseEvent);

		this.chooseEvent.setItems(FXCollections.observableArrayList(this.termEvents));

		Button add = new Button("Add");
		Style.setButtonStyle(add);

		add.setOnAction(e -> {
			if (chooseEvent.getValue() != null) {
				this.controller.addPriority(e);
			}
		});

		HBox addPriority = new HBox(10);

		addPriority.getChildren().addAll(this.chooseEvent, add);

		this.priorityList = new VBox(10);
		updatePriorityList();

		todaysPriorities.getChildren().addAll(dashTitle, addPriority, this.priorityList);

		this.todaysMeetings = new TodaysMeetings(new TodaysMeetingsController(this.controller.profile));
		this.upcomingEvents = new UpcomingEvents(new UpcomingEventsController(this.controller.profile));

		HBox importantInfo = new HBox(10);

		importantInfo.getChildren().addAll(todaysMeetings.mainLayout, upcomingEvents.mainLayout);

		main.setCenter(todaysPriorities);
		main.setRight(importantInfo);

		main.setPadding(new Insets(0, 0, 0, 20));

		return main;
	}

	private void updateChooseEvent() {

		this.chooseEvent.getItems().clear();
		this.termEvents.clear();

		if (this.controller.profile.currentlySelectedTerm != null) {

			for (LocalDate d : this.controller.profile.currentlySelectedTerm.dateEvents.sortedKeys) {

				this.termEvents.addAll(this.controller.profile.currentlySelectedTerm.dateEvents.get(d));
			}
		}

		this.chooseEvent.setItems(FXCollections.observableArrayList(termEvents));
	}

	private void updatePriorityList() {

		this.priorityList.getChildren().clear();

		Label prioritiesTitle = new Label("Priorities");
		Style.setTitleStyle(prioritiesTitle);

		this.priorityList.getChildren().add(prioritiesTitle);

		for (int i = 0; i < priorities.size(); i++) {

			HBox priorityListing = new HBox(10);

			Label number = new Label("" + (i + 1));

			priorityListing.getChildren().addAll(number,
					new Listing(Color.web(this.priorities.get(i).color), this.priorities.get(i).toString()).show());

			this.priorityList.getChildren().add(priorityListing);
		}
	}

	@Override
	public void update(Observable arg0, Object arg1) {

		if (arg0 instanceof Profile) {

			updateChooseEvent();
			updatePriorityList();
		}
	}

	@Override
	public void refresh() {
		update(this.profile, null);
	}
}
