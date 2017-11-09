package courseSchedule;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.PriorityQueue;

import core.Clock;
import core.Listing;
import core.Style;
import core.View;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import model.CourseMeeting;
import model.Meeting;
import model.Profile;

public class TodaysMeetings extends View implements Observer {

	private TodaysMeetingsController controller;
	private Observable profile;

	public VBox todaysMeetings = new VBox();

	public TodaysMeetings(TodaysMeetingsController controller) {

		this.controller = controller;

		this.profile = this.controller.profile;
		this.controller.profile.addObserver(this);

		this.mainLayout = initLayout();
	}

	@Override
	public BorderPane initLayout() {

		BorderPane main = new BorderPane();

		todaysMeetings.setStyle("-fx-background-color: #" + Style.colorToHex(Style.appGrey) + ";");
		BorderPane.setAlignment(todaysMeetings, Pos.CENTER);

		main.setCenter(todaysMeetings);
		return main;
	}

	private void setTodaysMeetings() {

		todaysMeetings.getChildren().clear();

		Label title = new Label("Today's Meetings");
		Style.setTitleStyle(title);
		title.setStyle(title.getStyle() + "-fx-text-fill: #fff;");

		VBox titleContainer = new VBox();
		titleContainer.setStyle("-fx-background-color: #" + Style.colorToHex(Style.appGreen) + "; -fx-padding: 10;");
		titleContainer.getChildren().add(title);

		todaysMeetings.getChildren().add(titleContainer);

		if (controller.profile.currentlySelectedTerm != null) {

			/* Priority queue of today's meetings */
			PriorityQueue<Meeting> td = controller.profile.currentlySelectedTerm.dayMeetings
					.get(Clock.now.toLocalDate());

			if (td != null && !td.isEmpty()) {

				ArrayList<Meeting> meetings = new ArrayList<>();
				meetings.addAll(td);
				meetings.sort(null);

				for (Meeting m : meetings) {

					VBox timeContainer = new VBox();

					int hours = m.start.getHour() - Clock.now.getHour();
					int mins = m.start.getMinute() - Clock.now.getMinute();

					Label timeUntil = null;

					if (hours > 0 || mins > 0) {

						if (hours > 0 && mins > 0) {

							timeUntil = new Label("In " + hours + " hours, " + mins + " mins " + " (" + m.start + " - "
									+ m.end + "):");

						} else if (hours > 1 && mins < 0) {

							hours--;
							mins = 60 + mins;

							timeUntil = new Label("In " + hours + " hours, " + mins + " mins " + " (" + m.start + " - "
									+ m.end + "):");

						} else if (hours == 1 && mins < 0) {

							hours--;
							mins = 60 + mins;

							timeUntil = new Label("In " + mins + " mins " + " (" + m.start + " - " + m.end + "):");

						} else if (hours == 0 && mins > 0) {

							mins = 60 + mins;

							timeUntil = new Label("In " + mins + " mins " + " (" + m.start + " - " + m.end + "):");

						} else {

							hours = hours * -1;
							hours--;
							mins = 60 - mins;

							// The meeting's start time has passed.
							timeUntil = new Label(
									hours + " hours, " + mins + " mins ago " + " (" + m.start + " - " + m.end + "):");
						}

					} else {

						hours = hours * -1;
						mins = mins * -1;

						// The meeting's start time has passed.
						timeUntil = new Label(
								hours + " hours, " + mins + " mins ago " + " (" + m.start + " - " + m.end + "):");
					}

					timeContainer.getChildren().add(timeUntil);

					timeContainer.setStyle(
							"-fx-background-color: #" + Style.colorToHex(Style.appYellow) + "; -fx-padding: 10;");

					todaysMeetings.getChildren().add(timeContainer);

					VBox meetingContainer = new VBox();
					HBox meetingInfo = null;

					if (m instanceof CourseMeeting) {

						if (m.location.isEmpty()) {

							meetingInfo = new Listing(Color.web(m.color),
									"" + ((CourseMeeting) m).course + " " + m.meetingType).show();

						} else {

							meetingInfo = new Listing(Color.web(m.color),
									"" + ((CourseMeeting) m).course + " " + m.meetingType + " in " + m.location).show();

						}

					} else {

						meetingInfo = new Listing(Color.web(m.color), "" + m.name + " " + m.meetingType).show();
					}

					meetingInfo.setOnMouseClicked(new EventHandler<MouseEvent>() {
						@Override
						public void handle(MouseEvent t) {

							new EditInstanceOrSet(LocalDateTime.of(m.date, m.start),
									new CourseScheduleController(controller.profile));
						}
					});

					meetingContainer.getChildren().add(meetingInfo);
					meetingContainer.setPadding(new Insets(10, 10, 10, 10));
					todaysMeetings.getChildren().add(meetingContainer);
				}

			} else {

				Label empty = new Label("No meetings today!");
				todaysMeetings.getChildren().add(empty);
			}

		} else {

			Label empty = new Label("No meetings exist. Start by adding a Term.");
			todaysMeetings.getChildren().add(empty);
		}
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		if (arg0 instanceof Profile) {
			setTodaysMeetings();
		}
	}

	@Override
	public void refresh() {
		update(this.profile, null);
	}

}
