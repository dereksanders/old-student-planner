package dashboard;

import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import model.CalendarEvent;

public class EventCard {

	public CalendarEvent event;
	public Rectangle weightBar;
	public VBox layout;

	public EventCard(CalendarEvent event) {

		this.event = event;
		this.layout = new VBox();
	}
}
