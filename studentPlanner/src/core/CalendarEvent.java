package core;

import java.time.LocalDateTime;

import javafx.scene.paint.Color;

public class CalendarEvent implements Comparable<CalendarEvent> {

	private static String personalColor = "ff7d00";
	public String name;
	public LocalDateTime start;
	public LocalDateTime end;
	public double weight;
	public double grade;
	public String colour;
	public boolean personal;

	/* Constructor for Deliverable CalendarEvent */
	public CalendarEvent(String name, LocalDateTime due, double weight, String colour) {

		this.name = name;
		this.start = due;
		this.weight = weight;
		this.colour = colour;
		this.grade = 0;
		this.personal = false;
	}

	/* Constructor for Personal CalendarEvent */
	public CalendarEvent(String name, LocalDateTime start, LocalDateTime end) {

		this.name = name;
		this.start = start;
		this.end = end;
		this.colour = CalendarEvent.personalColor;
		this.personal = true;
		this.weight = 0;
	}

	@Override
	public String toString() {
		if (personal) {
			return this.name + " (" + this.start.toLocalTime() + " - " + this.end.toLocalTime() + ")";
		} else {
			return Planner.active.courseColors.get(Color.web(this.colour)) + " " + this.name + " ("
					+ start.toLocalTime() + ")";
		}
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof CalendarEvent) {
			if (this.personal == ((CalendarEvent) o).personal) {
				return (this.name.equals(((CalendarEvent) o).name) && this.start.equals(((CalendarEvent) o).start));
			}
		}
		return false;
	}

	@Override
	public int compareTo(CalendarEvent arg0) {
		return (int) (this.weight - arg0.weight);
	}
}