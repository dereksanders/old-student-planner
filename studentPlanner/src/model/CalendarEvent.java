package model;

import java.time.LocalDateTime;

public class CalendarEvent implements Comparable<CalendarEvent> {

	public String name;
	public LocalDateTime start;
	public LocalDateTime end;
	/* Colour for personal calendar events */
	public String colour = "ff7d00";
	public double weight = 0;

	public CalendarEvent(String name, LocalDateTime start, LocalDateTime end) {
		this.name = name;
		this.start = start;
		this.end = end;
	}

	@Override
	public String toString() {
		return this.name + " (" + this.start.toLocalTime() + " - " + this.end.toLocalTime() + ")";
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof CalendarEvent) {
			return (this.name.equals(((CalendarEvent) o).name) && this.start.equals(((CalendarEvent) o).start)
					&& this.colour.equals(((CalendarEvent) o).colour));
		}
		return false;
	}

	@Override
	public int compareTo(CalendarEvent arg0) {
		return (int) (this.weight - arg0.weight);
	}
}