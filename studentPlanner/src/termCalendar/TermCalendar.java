package termCalendar;

import java.time.LocalDate;
import java.util.Observable;
import java.util.Observer;
import java.util.PriorityQueue;

import core.Driver;
import core.Style;
import core.View;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import model.CalendarEvent;
import model.CourseEvent;
import model.Profile;
import model.Term;
import utility.Pretty;

/**
 * The Class TermCalendar.
 */
public class TermCalendar extends View implements Observer {

	/* Observable & Controller */
	private Observable observable;
	private TermCalendarController controller;

	private HBox termViewBox = new HBox(50);
	private VBox upcomingEvents = new VBox(10);
	private VBox upcomingLayout = new VBox(15);

	private BooleanProperty selectedTermNotNull = new SimpleBooleanProperty(false);

	/**
	 * Instantiates a new term calendar.
	 *
	 * @param planner
	 *            the planner
	 * @param observable
	 *            the observable
	 * @param controller
	 *            the controller
	 */
	public TermCalendar(Observable observable, TermCalendarController controller) {

		this.observable = observable;
		observable.addObserver(this);

		this.controller = controller;
		controller.calendar = this;

		this.mainLayout = initLayout();
	}

	/**
	 * Initializes the main layout of the TermCalendar.
	 *
	 * @return the border pane
	 */
	private BorderPane initLayout() {

		BorderPane tcbp = new BorderPane();

		/*
		 * Term Calendar should only display if the currently selected term is
		 * not null.
		 */
		tcbp.visibleProperty().bind(selectedTermNotNull);

		Label termCal = new Label("Term Calendar");
		Style.setTitleStyle(termCal);
		HBox header = new HBox(50);
		Label upcomingTitle = new Label("Upcoming");
		Style.setTitleStyle(upcomingTitle);
		HBox upcomingShow = new HBox();
		Label showWithin = new Label("Show events within: ");

		ObservableList<Integer> thresholds = FXCollections.observableArrayList();
		for (int i = 1; i < 31; i++) {
			thresholds.add(i);
		}

		ComboBox<Integer> upcomingThreshold = new ComboBox<>(thresholds);
		upcomingThreshold.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldIndex, Number newIndex) {
				controller.active.showWithinThreshold = thresholds.get(newIndex.intValue());
				updateUpcomingEvents();
			}
		});

		upcomingThreshold.setValue(14);
		Label showWithinDays = new Label(" days.");
		upcomingShow.getChildren().addAll(showWithin, upcomingThreshold, showWithinDays);
		upcomingLayout.getChildren().addAll(upcomingTitle, upcomingShow, upcomingEvents);
		upcomingLayout
				.setStyle("-fx-background-color: #fff; -fx-border-width: 1; -fx-border-color: #000; -fx-padding: 10;");
		BorderPane.setAlignment(upcomingLayout, Pos.CENTER);

		header.getChildren().add(termCal);
		tcbp.setTop(header);
		tcbp.setCenter(termViewBox);

		tcbp.setStyle("-fx-padding: 10;");

		return tcbp;
	}

	/**
	 * Draw calendar.
	 *
	 * @param term
	 *            the term
	 * @return the scroll pane
	 */
	private ScrollPane drawCalendar(Term term) {

		/*
		 * TODO: Fix issue #13. The scroll-bar of the ScrollPane cuts off the
		 * edges of the calendar when there are more than 4 months.
		 */
		ScrollPane termCalendar = new ScrollPane();

		/*
		 * Calculate the number of months between the Term's start and end
		 * dates.
		 */
		int numMonths = ((term.end.getYear() - term.start.getYear()) * 12) + term.end.getMonthValue()
				- term.start.getMonthValue() + 1; // length of the term in
													// months

		VBox area = new VBox();
		HBox curBox = null;

		for (int i = 0; i < numMonths; i++) {

			BorderPane curMonthCalendar = new BorderPane();
			GridPane monthGrid = new GridPane();

			/*
			 * Get the date of the first day of the month (so that we know its
			 * weekday).
			 */
			LocalDate firstOfMonth = LocalDate.of(term.start.plusMonths(i).getYear(),
					term.start.plusMonths(i).getMonthValue(), 1);

			/* Month name, e.g. July */
			Label title = new Label(firstOfMonth.getMonth().toString().substring(0, 1)
					+ firstOfMonth.getMonth().toString().substring(1).toLowerCase() + ", " + firstOfMonth.getYear());
			title.setStyle(title.getStyle() + "-fx-font-size: 12.0pt;" + "-fx-font-weight: bold;");
			BorderPane.setAlignment(title, Pos.CENTER);
			curMonthCalendar.setTop(title);

			int daysInMonth = firstOfMonth.lengthOfMonth();
			int firstWeekDay = firstOfMonth.getDayOfWeek().getValue() - 1;

			Button[][] dayButtons = new Button[6][7];

			/*
			 * Numbering is false when the day button represents a date before
			 * the first day of the month - the greyed out days in the calendar
			 * prior to the 1st.
			 */
			boolean numbering = false;
			int date = 1;

			for (int k = 0; k < dayButtons.length; k++) { // 0 - 5

				for (int j = 0; j < dayButtons[0].length; j++) { // 0 - 6
																	// (weekday)
					if (k == 0) {

						Label dayLabel = new Label(intToDay(j));
						dayLabel.setStyle("-fx-font-size: 10.0pt;");
						GridPane.setHalignment(dayLabel, HPos.CENTER);
						monthGrid.add(dayLabel, j, k);
					}
					if (!numbering && j == firstWeekDay) {
						numbering = true;
					} else if (!numbering) {
						Button emptyDay = new Button();
						emptyDay.setStyle(emptyDay.getStyle() + "-fx-background-color: #"
								+ Style.colorToHex(Style.appGrey) + ";");
						if (j != firstWeekDay - 1) {
							emptyDay.setBorder(new Border(Style.noRightBorderStroke));
						} else {
							emptyDay.setBorder(new Border(Style.fullBorderStroke));
						}
						emptyDay.setMinWidth(40);
						emptyDay.setMinHeight(40);
						monthGrid.add(emptyDay, j, k + 1);
					}
					if (numbering && date <= daysInMonth) {
						Button add = new Button("" + date);
						add.setMinWidth(40);
						add.setMinHeight(40);
						PriorityQueue<CalendarEvent> ce = controller.active.dateEvents.get(LocalDate.of(
								term.start.plusMonths(i).getYear(), term.start.plusMonths(i).getMonthValue(), date));
						if (ce != null && !ce.isEmpty()) {
							add.setStyle(add.getStyle() + "-fx-background-color: #" + ce.peek().colour + ";"
									+ "-fx-background-radius: 0.0;");
							if ((Color.web((ce.peek()).colour).getBrightness() < 0.7)) {
								add.setStyle(add.getStyle() + "-fx-text-fill: #fff;");
							} else {
								add.setStyle(add.getStyle() + "-fx-text-fill: #000");
							}
						} else {
							add.setStyle(add.getStyle() + "-fx-text-fill: #444;" + "-fx-font-size: 12.4pt;");
							add.setStyle(add.getStyle()
									+ "-fx-background-color: linear-gradient(from 25% 25% to 100% 100%, #fff, #ccc);"
									+ "-fx-background-radius: 0.0;");
						}
						final int cDate = date;
						final int offset = i;
						add.setOnAction(e -> {
							if (ce == null || ce.isEmpty()) {
								new AddCalendarEvent(LocalDate.of(term.start.plusMonths(offset).getYear(),
										term.start.plusMonths(offset).getMonthValue(), cDate), controller);
							} else {
								new AddOrEditCalendarEvent(LocalDate.of(term.start.plusMonths(offset).getYear(),
										term.start.plusMonths(offset).getMonthValue(), cDate), controller);
							}
						});
						monthGrid.add(add, j, k + 1);
						date++;
					}
				}
			}
			curMonthCalendar.setCenter(monthGrid);
			if (i % 2 == 0) {
				curBox = new HBox(20);
				curBox.getChildren().add(curMonthCalendar);
				if (i == numMonths - 1) {
					area.getChildren().add(curBox);
				}
			} else {
				curBox.getChildren().add(curMonthCalendar);
				area.getChildren().add(curBox);
			}
		}
		termCalendar.setContent(area);
		updateUpcomingEvents();
		return termCalendar;
	}

	/**
	 * Int to day.
	 *
	 * @param j
	 *            the j
	 * @return the string
	 */
	private static String intToDay(int j) {

		String day = "";

		switch (j) {
		case 0:
			day = "Mon";
			break;
		case 1:
			day = "Tue";
			break;
		case 2:
			day = "Wed";
			break;
		case 3:
			day = "Thu";
			break;
		case 4:
			day = "Fri";
			break;
		case 5:
			day = "Sat";
			break;
		case 6:
			day = "Sun";
			break;
		}

		return day;
	}

	private void updateUpcomingEvents() {

		this.upcomingEvents.getChildren().clear();

		for (int i = 0; i <= controller.active.showWithinThreshold; i++) {

			if (controller.active.dateEvents.get(Driver.t.current.toLocalDate().plusDays(i)) != null
					&& !controller.active.dateEvents.get(Driver.t.current.toLocalDate().plusDays(i)).isEmpty()) {

				Label dateOfEvents = new Label("");

				// Events are today
				if (i == 0) {
					dateOfEvents.setText("Today:");
				}
				// Events are tomorrow
				else if (i == 1) {
					dateOfEvents.setText("Tomorrow:");
				} else {
					dateOfEvents.setText(Pretty.prettyDate(Driver.t.current.toLocalDate().plusDays(i)) + ":");
				}

				this.upcomingEvents.getChildren().add(dateOfEvents);

				for (CalendarEvent e : controller.active.dateEvents.get(Driver.t.current.toLocalDate().plusDays(i))) {

					HBox eventListing = new HBox(5);

					Rectangle eventIcon = new Rectangle(20, 20);
					eventIcon.setFill(Color.web(e.colour));

					Label eventDesc = new Label();

					if (e instanceof CourseEvent) {
						if (e.start.equals(e.end)) {
							eventDesc.setText(e.name + " (Due: " + e.start.toLocalTime() + ")");
						} else {
							eventDesc
									.setText(e.name + " (" + e.start.toLocalTime() + " - " + e.end.toLocalTime() + ")");
						}
					} else {
						eventDesc.setText(e.toString());
					}

					eventListing.getChildren().addAll(eventIcon, eventDesc);
					this.upcomingEvents.getChildren().add(eventListing);
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	@Override
	public void update(Observable arg0, Object arg1) {
		if (arg0 instanceof Profile) {
			if (((Profile) arg0).currentlySelectedTerm != null) {
				selectedTermNotNull.set(true);
				termViewBox.getChildren().clear();
				termViewBox.getChildren().add(drawCalendar(((Profile) arg0).currentlySelectedTerm));
				termViewBox.getChildren().add(upcomingLayout);
			} else {
				selectedTermNotNull.set(false);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see core.View#refresh()
	 */
	@Override
	public void refresh() {
		update(this.observable, null);
	}

	@Override
	public String toString() {
		return "Term Calendar";
	}
}
