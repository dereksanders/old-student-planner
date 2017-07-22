package model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * The Class CalendarEvent.
 */
public class CalendarEvent implements Comparable<CalendarEvent>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String name;
	public LocalDateTime start;
	public LocalDateTime end;
	/* Colour for personal calendar events */
	public String colour = "ff7d00";
	public double weight = 0;

	/**
	 * Instantiates a new calendar event.
	 *
	 * @param name
	 *            the name
	 * @param start
	 *            the start
	 * @param end
	 *            the end
	 */
	public CalendarEvent(String name, LocalDateTime start, LocalDateTime end) {
		this.name = name;
		this.start = start;
		this.end = end;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.name + " (" + this.start.toLocalTime() + " - " + this.end.toLocalTime() + ")";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		if (o instanceof CalendarEvent) {
			return (this.name.equals(((CalendarEvent) o).name) && this.start.equals(((CalendarEvent) o).start)
					&& this.colour.equals(((CalendarEvent) o).colour));
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(CalendarEvent arg0) {
		return (int) (this.weight - arg0.weight);
	}
}