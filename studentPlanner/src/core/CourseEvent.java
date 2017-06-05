package core;

import java.time.LocalDateTime;

public class CourseEvent extends CalendarEvent {

	public double grade = 0;

	public CourseEvent(String name, String colour, LocalDateTime start, LocalDateTime end, double weight) {

		super(name, start, end);

		this.colour = colour;
		this.weight = weight;
	}
}