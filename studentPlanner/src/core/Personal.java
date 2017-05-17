package core;

import java.time.LocalDateTime;

public class Personal extends CalendarEvent {

	public LocalDateTime start;
	public LocalDateTime end;
	public String location;

	public Personal(String name, LocalDateTime start, LocalDateTime end, String location) {
		this.name = name;
		this.start = start;
		this.end = end;
		this.location = location;
		this.weight = 0;
	}

	@Override
	public int compareTo(CalendarEvent arg0) {
		return super.compareTo(arg0);
	}

	@Override
	public String toString() {
		return this.name + " (" + this.start + " - " + this.end + ")";
	}
}
