package termCalendar;

import java.time.LocalDate;
import java.util.PriorityQueue;

import core.CalendarEvent;
import core.Planner;
import core.Term;
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
import utility.Pretty;

public class TermCalendar {

	public static BorderPane curMonthCalendar;
	public static BorderPane nxtMonthCalendar;
	public static BorderPane nxtMonthCalendar2;
	public static BorderPane nxtMonthCalendar3;
	public static HBox calendarsUpper;
	public static HBox calendarsLower;
	public static Label upcomingEventsLabel;
	public static HBox termViewBox = new HBox(50);
	public static VBox upcoming = new VBox();

	public static BorderPane init() {

		BorderPane tcbp = new BorderPane();

		upcomingEventsLabel = new Label("");
		Label termCal = new Label("Term Calendar");
		Planner.setTitleStyle(termCal);
		HBox header = new HBox(50);
		Label upcomingTitle = new Label("Upcoming");
		Planner.setTitleStyle(upcomingTitle);
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
				Planner.active.showWithinThreshold = thresholds.get(newIndex.intValue());
				updateUpcomingEvents();
			}
		});
		upcomingThreshold.setValue(14);
		Label showWithinDays = new Label(" days.");
		upcomingShow.getChildren().addAll(showWithin, upcomingThreshold, showWithinDays);
		upcoming.getChildren().addAll(upcomingTitle, upcomingShow, upcomingEventsLabel);
		BorderPane.setAlignment(upcoming, Pos.CENTER);
		header.getChildren().add(termCal);
		tcbp.setTop(header);
		tcbp.setCenter(termViewBox);

		return tcbp;
	}

	public static void redrawCalendars() {
		termViewBox.getChildren().clear();
		termViewBox.getChildren().add(drawCalendar(Planner.active.currentlySelectedTerm));
		termViewBox.getChildren().add(upcoming);
	}

	public static ScrollPane drawCalendar(Term term) {

		int numMonths = ((term.end.getYear() - term.start.getYear()) * 12) + term.end.getMonthValue()
				- term.start.getMonthValue() + 1; // length of the term in
													// months

		ScrollPane termCalendar = new ScrollPane();
		VBox area = new VBox();
		HBox curBox = null;

		for (int i = 0; i < numMonths; i++) {

			BorderPane curMonthCalendar = new BorderPane();
			GridPane monthGrid = new GridPane();

			LocalDate firstOfMonth = LocalDate.of(term.start.plusMonths(i).getYear(),
					term.start.plusMonths(i).getMonthValue(), 1);

			Label title = new Label(firstOfMonth.getMonth().toString().substring(0, 1)
					+ firstOfMonth.getMonth().toString().substring(1).toLowerCase() + ", " + firstOfMonth.getYear());
			title.setStyle(title.getStyle() + "-fx-font-size: 12.0pt;" + "-fx-font-weight: bold;");

			BorderPane.setAlignment(title, Pos.CENTER);
			curMonthCalendar.setTop(title);

			int daysInMonth = firstOfMonth.lengthOfMonth();
			int firstWeekDay = firstOfMonth.getDayOfWeek().getValue() - 1;

			Button[][] dayButtons = new Button[6][7];
			boolean numbering = false;
			int date = 1;

			for (int k = 0; k < dayButtons.length; k++) { // 0 - 5

				for (int j = 0; j < dayButtons[0].length; j++) { // 0 - 6
																	// (weekday)
					if (k == 0) {

						Label dayLabel = new Label(intToDay(j));
						GridPane.setHalignment(dayLabel, HPos.CENTER);
						monthGrid.add(dayLabel, j, k);
					}
					if (!numbering && j == firstWeekDay) {
						numbering = true;
					} else if (!numbering) {
						Button emptyDay = new Button();
						emptyDay.setStyle(emptyDay.getStyle() + "-fx-background-color: #"
								+ Planner.colorToHex(Planner.appGrey) + ";");
						if (j != firstWeekDay - 1) {
							emptyDay.setBorder(new Border(Planner.noRightBorderStroke));
						} else {
							emptyDay.setBorder(new Border(Planner.fullBorderStroke));
						}
						emptyDay.setMinWidth(40);
						emptyDay.setMinHeight(40);
						monthGrid.add(emptyDay, j, k + 1);
					}
					if (numbering && date <= daysInMonth) {
						Button add = new Button("" + date);
						add.setMinWidth(40);
						add.setMinHeight(40);
						PriorityQueue<CalendarEvent> ce = Planner.active.dateEvents.get(LocalDate.of(
								term.start.plusMonths(i).getYear(), term.start.plusMonths(i).getMonthValue(), date));
						if (ce != null && !ce.isEmpty()) {
							add.setStyle(add.getStyle() + "-fx-background-color: #" + ce.peek().colour + ";");
							if ((Color.web((ce.peek()).colour).getBrightness() < 0.7)) {
								add.setStyle(add.getStyle() + "-fx-text-fill: #fff;");
							} else {
								add.setStyle(add.getStyle() + "-fx-text-fill: #000");
							}
						} else {
							add.setStyle(add.getStyle() + "-fx-text-fill: #444;" + "-fx-font-size: 12.4pt;");
							add.setStyle(add.getStyle()
									+ "-fx-background-color: linear-gradient(from 25% 25% to 100% 100%, #fff, #ccc);");
						}
						final int cDate = date;
						final int offset = i;
						add.setOnAction(e -> {
							if (ce == null || ce.isEmpty()) {
								AddCalendarEvent.display(LocalDate.of(term.start.plusMonths(offset).getYear(),
										term.start.plusMonths(offset).getMonthValue(), cDate));
								// listGrades(gChooseCourse.getValue());
							} else {
								AddOrEditCalendarEvent.display(LocalDate.of(term.start.plusMonths(offset).getYear(),
										term.start.plusMonths(offset).getMonthValue(), cDate));
								// listGrades(gChooseCourse.getValue());
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

	private static void updateUpcomingEvents() {
		String desc = "";
		for (int i = 0; i <= Planner.active.showWithinThreshold; i++) {
			if (Planner.active.dateEvents.get(Planner.t.current.toLocalDate().plusDays(i)) != null
					&& !Planner.active.dateEvents.get(Planner.t.current.toLocalDate().plusDays(i)).isEmpty()) {
				if (i == 0) {
					desc += "Today: \n";
				} else if (i == 1) {
					desc += "Tomorrow: \n";
				} else {
					desc += Pretty.prettyDate(Planner.t.current.toLocalDate().plusDays(i)) + ": \n";
				}
				for (CalendarEvent e : Planner.active.dateEvents.get(Planner.t.current.toLocalDate().plusDays(i))) {
					desc += e + "\n";
				}
				desc += "\n";
			}
		}
		if (!desc.isEmpty()) {
			desc = desc.substring(0, desc.lastIndexOf("\n"));
		}
		upcomingEventsLabel.setText(desc);
	}
}
