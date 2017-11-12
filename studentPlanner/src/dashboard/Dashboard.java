package dashboard;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

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

public class Dashboard extends View implements Observer {

	Observable profile;
	DashboardController controller;

	private UpcomingEvents upcomingEvents;
	private TodaysMeetings todaysMeetings;
	protected ArrayList<CalendarEvent> termEvents;

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

		VBox todaysPriorities = new VBox(10);

		Label dashTitle = new Label("What are your priorities today?");
		Style.setTitleStyle(dashTitle);

		this.termEvents = new ArrayList<>();
		this.chooseEvent = new ChoiceBox<>();
		Style.setChoiceBoxStyle(chooseEvent);
		updateChooseEvent();

		Button add = new Button("+");
		Style.setButtonStyle(add);

		add.setOnAction(e -> {
			if (chooseEvent.getValue() != null) {
				this.controller.addPriority(chooseEvent.getValue());
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

		this.termEvents.removeAll(this.controller.profile.currentlySelectedTerm.priorities);

		this.chooseEvent.setItems(FXCollections.observableArrayList(this.termEvents));
	}

	private void updatePriorityList() {

		this.priorityList.getChildren().clear();

		Label prioritiesTitle = new Label("Priorities");
		Style.setTitleStyle(prioritiesTitle);

		this.priorityList.getChildren().add(prioritiesTitle);

		if (this.controller.profile.currentlySelectedTerm != null) {

			for (int i = 0; i < this.controller.profile.currentlySelectedTerm.priorities.size(); i++) {

				HBox priorityListing = new HBox(10);

				Label number = new Label("" + (i + 1));

				priorityListing.getChildren().addAll(number,
						new Listing(Color.web(this.controller.profile.currentlySelectedTerm.priorities.get(i).color),
								this.controller.profile.currentlySelectedTerm.priorities.get(i).toString()).show());

				HBox options = new HBox(10);

				int buttonSize = 32;

				Button up = new Button("^");
				Button down = new Button("v");
				Button del = new Button("x");

				Style.setButtonStyle(up);
				Style.setButtonStyle(down);
				Style.setButtonStyle(del);

				up.setMinSize(buttonSize, buttonSize);
				up.setMaxSize(buttonSize, buttonSize);
				down.setMinSize(buttonSize, buttonSize);
				down.setMaxSize(buttonSize, buttonSize);
				del.setMinSize(buttonSize, buttonSize);
				del.setMaxSize(buttonSize, buttonSize);

				final int index = i;
				up.setOnAction(e -> {

					this.controller
							.increasePriority(this.controller.profile.currentlySelectedTerm.priorities.get(index));
				});

				down.setOnAction(e -> {

					this.controller
							.decreasePriority(this.controller.profile.currentlySelectedTerm.priorities.get(index));
				});

				del.setOnAction(e -> {

					this.controller.deletePriority(this.controller.profile.currentlySelectedTerm.priorities.get(index));
				});

				options.getChildren().addAll(up, down, del);

				priorityListing.getChildren().add(options);

				this.priorityList.getChildren().addAll(priorityListing);
			}
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
