package courseSchedule;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.PriorityQueue;

import core.Clock;
import core.Listing;
import core.Style;
import core.View;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import model.Meeting;
import model.Profile;

public class TodaysMeetings extends View implements Observer {

	private TodaysMeetingsController controller;
	private Observable profile;

	public VBox todaysMeetings = new VBox(5);

	public TodaysMeetings(TodaysMeetingsController controller) {

		this.controller = controller;

		this.profile = this.controller.profile;
		this.controller.profile.addObserver(this);

		this.mainLayout = initLayout();
	}

	@Override
	public BorderPane initLayout() {

		BorderPane main = new BorderPane();

		todaysMeetings.setStyle("-fx-background-color: #fff; -fx-padding: 10;");
		BorderPane.setAlignment(todaysMeetings, Pos.CENTER);

		main.setCenter(todaysMeetings);
		return main;
	}

	private void setTodaysMeetings() {

		todaysMeetings.getChildren().clear();

		if (controller.profile.currentlySelectedTerm != null) {

			/* Priority queue of today's meetings */
			PriorityQueue<Meeting> td = controller.profile.currentlySelectedTerm.dayMeetings
					.get(Clock.now.toLocalDate());

			if (td != null && !td.isEmpty()) {

				Label todaysMeetingsTitle = new Label("Today's Meetings");
				Style.setTitleStyle(todaysMeetingsTitle);

				todaysMeetings.getChildren().add(todaysMeetingsTitle);

				ArrayList<Meeting> meetings = new ArrayList<>();
				meetings.addAll(td);
				meetings.sort(null);

				for (Meeting m : meetings) {

					todaysMeetings.getChildren().add(new Listing(Color.web(m.color), m.toString()).show());
				}
			}
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
