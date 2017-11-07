package termCalendar;

import java.time.LocalDate;
import java.util.Observable;
import java.util.Observer;
import java.util.PriorityQueue;

import core.Style;
import core.View;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import model.CalendarEvent;
import model.Profile;
import model.Term;

/**
 * The Class TermCalendar.
 */
public class TermCalendar extends View implements Observer {

	/* Observable & Controller */
	private Observable observable;
	private TermCalendarController controller;

	private HBox termViewBox = new HBox(50);
	private UpcomingEvents upcomingEvents;

	private BooleanProperty selectedTermNotNull = new SimpleBooleanProperty(false);

	/**
	 * Instantiates a new term calendar.
	 *
	 * @param planner
	 *            the planner
	 * @param profile
	 *            the observable
	 * @param controller
	 *            the controller
	 */
	public TermCalendar(TermCalendarController controller) {

		this.controller = controller;
		this.controller.calendar = this;

		this.observable = controller.profile;
		this.observable.addObserver(this);

		this.mainLayout = initLayout();
	}

	/**
	 * Initializes the main layout of the TermCalendar.
	 *
	 * @return the border pane
	 */
	public BorderPane initLayout() {

		BorderPane tcbp = new BorderPane();

		/*
		 * Term Calendar should only display if the currently selected term is not null.
		 */
		tcbp.visibleProperty().bind(selectedTermNotNull);

		// Label termCal = new Label("Term Calendar");
		// Style.setTitleStyle(termCal);
		// HBox header = new HBox(50);

		// header.getChildren().add(termCal);
		// tcbp.setTop(header);
		tcbp.setCenter(termViewBox);

		this.upcomingEvents = new UpcomingEvents(new UpcomingEventsController(this.controller.profile));
		tcbp.setRight(upcomingEvents.mainLayout);

		tcbp.setPadding(new Insets(0, 0, 0, 20));
		tcbp.setStyle("-fx-background-color: #" + Style.colorToHex(Style.appWhite) + ";");

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

		ScrollPane termCalendar = new ScrollPane();

		/*
		 * Calculate the number of months between the Term's start and end dates.
		 */
		int numMonths = ((term.end.getYear() - term.start.getYear()) * 12) + term.end.getMonthValue()
				- term.start.getMonthValue() + 1; // length of the term in
													// months

		VBox area = new VBox(10);
		HBox curBox = null;

		for (int i = 0; i < numMonths; i++) {

			BorderPane curMonthCalendar = new BorderPane();
			GridPane monthGrid = new GridPane();

			/*
			 * Get the date of the first day of the month (so that we know its weekday).
			 */
			LocalDate firstOfMonth = LocalDate.of(term.start.plusMonths(i).getYear(),
					term.start.plusMonths(i).getMonthValue(), 1);

			VBox titleContainer = new VBox();

			/* Month name, e.g. July */
			Label title = new Label(firstOfMonth.getMonth().toString().substring(0, 1)
					+ firstOfMonth.getMonth().toString().substring(1).toLowerCase() + ", " + firstOfMonth.getYear());
			title.setStyle(title.getStyle() + "-fx-font-size: 12.0pt;" + "-fx-text-fill: #"
					+ Style.colorToHex(Style.appWhite) + ";");

			titleContainer.getChildren().add(title);
			titleContainer.setStyle("-fx-background-color: #" + Style.colorToHex(Style.appGreen) + ";");
			titleContainer.setPadding(new Insets(2, 0, 2, 10));

			// BorderPane.setAlignment(title, Pos.CENTER);
			curMonthCalendar.setTop(titleContainer);

			int daysInMonth = firstOfMonth.lengthOfMonth();
			int firstWeekDay = firstOfMonth.getDayOfWeek().getValue() - 1;

			Button[][] dayButtons = new Button[6][7];

			/*
			 * Numbering is false when the day button represents a date before the first day
			 * of the month - the greyed out days in the calendar prior to the 1st.
			 */
			boolean numbering = false;
			int date = 1;

			for (int k = 0; k < dayButtons.length; k++) { // 0 - 5

				for (int j = 0; j < dayButtons[0].length; j++) { // 0 - 6
																	// (weekday)
					if (k == 0) {

						VBox dayContainer = new VBox();

						Label dayLabel = new Label(intToDay(j));
						dayLabel.setStyle("-fx-font-size: 10.0pt");

						dayContainer.getChildren().add(dayLabel);
						dayContainer.setStyle("-fx-background-color: " + Style.colorToHex(Style.appGrey) + ";");
						dayContainer.setPadding(new Insets(0, 0, 0, 8));

						GridPane.setHalignment(dayLabel, HPos.CENTER);
						monthGrid.add(dayContainer, j, k);
					}
					if (!numbering && j == firstWeekDay) {
						numbering = true;
					} else if (!numbering) {
						Button emptyDay = new Button();
						emptyDay.setStyle(emptyDay.getStyle() + "-fx-background-color: #"
								+ Style.colorToHex(Style.appGrey) + ";-fx-background-radius: 0;");
						if (j != firstWeekDay - 1) {
							// emptyDay.setBorder(new Border(Style.noRightBorderStroke));
						} else {
							// emptyDay.setBorder(new Border(Style.fullBorderStroke));
						}
						emptyDay.setMinWidth(40);
						emptyDay.setMinHeight(40);
						monthGrid.add(emptyDay, j, k + 1);
					}
					if (numbering && date <= daysInMonth) {
						Button add = new Button("" + date);
						add.setMinWidth(40);
						add.setMinHeight(40);
						PriorityQueue<CalendarEvent> ce = controller.profile.currentlySelectedTerm.dateEvents
								.get(LocalDate.of(term.start.plusMonths(i).getYear(),
										term.start.plusMonths(i).getMonthValue(), date));
						if (ce != null && !ce.isEmpty()) {
							add.setStyle(add.getStyle() + "-fx-background-color: #" + ce.peek().color + ";"
									+ "-fx-background-radius: 0.0;");
							if ((Color.web((ce.peek()).color).getBrightness() < 0.7)) {
								add.setStyle(add.getStyle() + "-fx-text-fill: #fff;");
							} else {
								add.setStyle(add.getStyle() + "-fx-text-fill: #000");
							}
						} else {
							add.setStyle(add.getStyle() + "-fx-text-fill: #444;" + "-fx-font-size: 12.4pt;");
							add.setStyle(add.getStyle() + "-fx-background-color: " + Style.colorToHex(Style.appGrey)
									+ ";-fx-background-radius: 0.0;");
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

		area.setStyle("-fx-background-color: #" + Style.colorToHex(Style.appWhite) + ";");

		termCalendar.setContent(area);
		termCalendar.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

		if (numMonths > 4) {
			termCalendar.setMinWidth(617);
		} else {
			termCalendar.setMinWidth(602);
		}

		termCalendar
				.setStyle(termCalendar.getStyle() + "-fx-padding: 0; -fx-background-color: #fff; -fx-border-width: 0;");

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

				ScrollPane calendarScroll = drawCalendar(((Profile) arg0).currentlySelectedTerm);
				calendarScroll.setFitToWidth(true);

				VBox calendar = new VBox();
				calendar.getChildren().add(calendarScroll);

				termViewBox.getChildren().add(calendar);

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
