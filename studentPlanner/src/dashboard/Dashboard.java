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
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import model.CalendarEvent;
import model.CourseEvent;
import model.Profile;
import termCalendar.UpcomingEvents;
import termCalendar.UpcomingEventsController;
import utility.Pretty;

public class Dashboard extends View implements Observer {

	Observable profile;
	DashboardController controller;

	private UpcomingEvents upcomingEvents;
	private TodaysMeetings todaysMeetings;
	protected ArrayList<CalendarEvent> unfinishedEvents;
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

		this.unfinishedEvents = new ArrayList<>();
		this.chooseEvent = new ChoiceBox<>();
		Style.setChoiceBoxStyle(chooseEvent);
		updateChooseEvent();

		Button add = new Button("+");
		Style.setButtonStyle(add);
		add.setMinSize(32, 32);
		add.setMaxSize(32, 32);

		add.setOnAction(e -> {
			if (chooseEvent.getValue() != null) {
				this.controller.addPriority(chooseEvent.getValue());
			}
		});

		HBox addPriority = new HBox(10);

		addPriority.getChildren().addAll(this.chooseEvent, add);

		this.priorityList = new VBox(10);
		this.priorityList.setStyle("-fx-background-color: #" + Style.colorToHex(Style.appGrey));
		this.priorityList.setPadding(new Insets(0, 0, 10, 0));

		// This makes the priority list fit its contents width-wise.
		this.priorityList.setMaxWidth(1);

		updatePriorityList();

		todaysPriorities.getChildren().addAll(dashTitle, addPriority, this.priorityList);

		this.todaysMeetings = new TodaysMeetings(new TodaysMeetingsController(this.controller.profile));
		this.upcomingEvents = new UpcomingEvents(new UpcomingEventsController(this.controller.profile));

		this.todaysMeetings.mainLayout.setMaxWidth(675);

		VBox centre = new VBox(10);
		centre.getChildren().addAll(todaysPriorities, this.todaysMeetings.mainLayout);

		main.setCenter(centre);
		main.setRight(upcomingEvents.mainLayout);

		main.setPadding(new Insets(0, 0, 0, 20));

		return main;
	}

	private void updateChooseEvent() {

		this.chooseEvent.getItems().clear();
		this.unfinishedEvents.clear();

		if (this.controller.profile.currentlySelectedTerm != null) {

			for (LocalDate d : this.controller.profile.currentlySelectedTerm.dateEvents.sortedKeys) {

				for (CalendarEvent e : this.controller.profile.currentlySelectedTerm.dateEvents.get(d)) {

					if (e instanceof CourseEvent) {

						if (((CourseEvent) e).state != CourseEvent.STATES.SUBMITTED.val
								&& ((CourseEvent) e).state != CourseEvent.STATES.GRADED.val)

							this.unfinishedEvents.add(e);
					}
				}
			}

			this.unfinishedEvents.removeAll(this.controller.profile.currentlySelectedTerm.priorities);
		}

		this.chooseEvent.setItems(FXCollections.observableArrayList(this.unfinishedEvents));
	}

	private void updatePriorityList() {

		this.priorityList.getChildren().clear();

		Label prioritiesTitle = new Label("Priorities");
		Style.setTitleStyle(prioritiesTitle);
		prioritiesTitle.setStyle(prioritiesTitle.getStyle() + "-fx-text-fill: #" + Style.colorToHex(Style.appWhite));

		HBox titleContainer = new HBox();
		titleContainer.setStyle("-fx-background-color: #" + Style.colorToHex(Style.appGreen) + ";");
		titleContainer.setPadding(new Insets(0, 0, 0, 10));
		titleContainer.getChildren().add(prioritiesTitle);

		this.priorityList.getChildren().add(titleContainer);

		GridPane priorityGrid = new GridPane();

		Label priCol = new Label("Priority");
		priCol.setStyle("-fx-font-weight: bold;");
		priCol.setPadding(new Insets(0, 0, 0, 10));

		Label delCol = new Label("Deliverable");
		delCol.setStyle("-fx-font-weight: bold;");
		delCol.setPadding(new Insets(0, 0, 0, 10));

		Label dueCol = new Label("Due Time");
		dueCol.setStyle("-fx-font-weight: bold;");
		dueCol.setPadding(new Insets(0, 0, 0, 10));

		Label stateCol = new Label("State");
		stateCol.setStyle("-fx-font-weight: bold;");
		stateCol.setPadding(new Insets(0, 0, 0, 10));

		Label optCol = new Label("Options");
		optCol.setStyle("-fx-font-weight: bold;");
		optCol.setPadding(new Insets(0, 0, 0, 10));

		priorityGrid.add(priCol, 0, 0);
		priorityGrid.add(delCol, 1, 0);
		priorityGrid.add(dueCol, 2, 0);
		priorityGrid.add(stateCol, 3, 0);
		priorityGrid.add(optCol, 4, 0);

		priorityGrid.getColumnConstraints().add(new ColumnConstraints(75));
		priorityGrid.getColumnConstraints().add(new ColumnConstraints(150));
		priorityGrid.getColumnConstraints().add(new ColumnConstraints(150));
		priorityGrid.getColumnConstraints().add(new ColumnConstraints(150));
		priorityGrid.getColumnConstraints().add(new ColumnConstraints(150));
		priorityGrid.getRowConstraints().add(new RowConstraints(10));

		this.priorityList.getChildren().add(priorityGrid);

		if (this.controller.profile.currentlySelectedTerm != null) {

			for (int i = 0; i < this.controller.profile.currentlySelectedTerm.priorities.size(); i++) {

				CalendarEvent current = this.controller.profile.currentlySelectedTerm.priorities.get(i);

				priorityGrid = new GridPane();

				Label number = new Label("" + (i + 1));
				number.setPadding(new Insets(0, 0, 0, 10));

				HBox priorityInfo = null;

				if (current instanceof CourseEvent) {

					priorityInfo = new Listing(Color.web(current.color),
							((CourseEvent) current).course + " " + current.name).show();

				} else {

					priorityInfo = new Listing(Color.web(current.color), current.name).show();
				}

				priorityInfo.setPadding(new Insets(0, 0, 0, 10));

				Label dueTime = new Label(
						Pretty.veryShortDate(current.end.toLocalDate()) + " at " + current.end.toLocalTime());
				dueTime.setPadding(new Insets(0, 0, 0, 10));

				priorityGrid.add(number, 0, i + 1);
				GridPane.setValignment(number, VPos.TOP);

				priorityGrid.add(priorityInfo, 1, i + 1);
				GridPane.setValignment(priorityInfo, VPos.TOP);

				priorityGrid.add(dueTime, 2, i + 1);
				GridPane.setValignment(dueTime, VPos.TOP);

				String[] courseEventStates = { "Not Started", "In Progress", "Submitted" };

				ArrayList<String> states = new ArrayList<>();
				for (String s : courseEventStates) {
					states.add(s);
				}

				if (current instanceof CourseEvent) {

					ChoiceBox<String> chooseState = new ChoiceBox<>(FXCollections.observableArrayList(states));
					Style.setChoiceBoxStyle(chooseState);

					if (((CourseEvent) current).state == CourseEvent.STATES.NOT_STARTED.val) {

						chooseState.setValue(courseEventStates[0]);

					} else if (((CourseEvent) current).state == CourseEvent.STATES.IN_PROGRESS.val) {

						chooseState.setValue(courseEventStates[1]);

					} else {

						// Submitted or Graded event should not be in priorities.
						// this.controller.deletePriority(current);
					}

					chooseState.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
						@Override
						public void changed(ObservableValue<? extends Number> observable, Number oldIndex,
								Number newIndex) {

							if (newIndex.intValue() != -1) {

								if (newIndex.intValue() == 0) {

									controller.updateState((CourseEvent) current, CourseEvent.STATES.NOT_STARTED.val);

								} else if (newIndex.intValue() == 1) {

									controller.updateState((CourseEvent) current, CourseEvent.STATES.IN_PROGRESS.val);

								} else if (newIndex.intValue() == 2) {

									controller.updateState((CourseEvent) current, CourseEvent.STATES.SUBMITTED.val);
								}
							}
						}
					});

					priorityGrid.add(chooseState, 3, i + 1);
					GridPane.setValignment(chooseState, VPos.TOP);
				}

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

				HBox options = new HBox(10);
				options.getChildren().addAll(up, down, del);
				options.setPadding(new Insets(0, 0, 0, 10));

				priorityGrid.add(options, 4, i + 1);
				GridPane.setValignment(options, VPos.TOP);

				priorityGrid.getColumnConstraints().add(new ColumnConstraints(75));
				priorityGrid.getColumnConstraints().add(new ColumnConstraints(150));
				priorityGrid.getColumnConstraints().add(new ColumnConstraints(150));
				priorityGrid.getColumnConstraints().add(new ColumnConstraints(150));
				priorityGrid.getColumnConstraints().add(new ColumnConstraints(150));
				priorityGrid.getRowConstraints().add(new RowConstraints(10));

				this.priorityList.getChildren().add(priorityGrid);
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
