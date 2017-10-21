package dashboard;

import java.util.Observable;
import java.util.Observer;

import core.Clock;
import core.Main;
import core.Style;
import core.View;
import courseSchedule.TodaysMeetings;
import courseSchedule.TodaysMeetingsController;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
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

		main.setTop(header);

		Image books = new Image(Main.class.getResourceAsStream("books.jpg"));

		VBox body = new VBox();
		BackgroundImage bi = new BackgroundImage(books, BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT,
				BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);

		body.setBackground(new Background(bi));

		VBox info = new VBox(5);
		Label getStarted = new Label("Get Started");
		Style.setSmallTitleStyle(getStarted);
		info.getChildren().add(getStarted);
		info.getChildren()
				.add(new Label("To begin, add a term by pressing the \"Add Term\" button at the bottom. Then "));
		info.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));

		Style.addPadding(body);
		body.getChildren().add(info);

		main.setCenter(body);

		this.todaysMeetings = new TodaysMeetings(new TodaysMeetingsController(this.controller.profile));
		main.setBottom(todaysMeetings.mainLayout);

		this.upcomingEvents = new UpcomingEvents(new UpcomingEventsController(this.controller.profile));
		main.setRight(upcomingEvents.mainLayout);

		Style.addPadding(main);

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
