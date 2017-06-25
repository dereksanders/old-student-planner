package model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

import utility.Pretty;

public class Meeting implements Comparable<Meeting> {

	public static String[] TYPES = { "Lecture", "Tutorial", "Lab", "Seminar" };

	public String meetingType;
	public LocalDate date;
	public LocalTime start;
	public LocalTime end;
	public String location;
	public String colour;
	public String repeat;

	public Meeting(String meetingType, LocalDate date, LocalTime start, LocalTime end, String location) {
		this.meetingType = meetingType;
		this.date = date;
		this.start = start;
		this.end = end;
		this.location = location;
	}

	@Override
	public String toString() {
		return Pretty.prettyShortDate(this.date) + ", " + this.start + " - " + this.end + " in " + this.location + " ("
				+ meetingType + ")";
	}

	public ArrayList<Meeting> conflictsWith(ArrayList<Meeting> meetings) {
		ArrayList<Meeting> conflicts = new ArrayList<>();
		conflicts.addAll(meetings);
		for (Meeting m : meetings) {
			if (!this.date.equals(m.date)) {
				conflicts.remove(m);
			} else if (m.end.compareTo(this.start) <= 0) {
				conflicts.remove(m);
			} else if (this.end.compareTo(m.start) <= 0) {
				conflicts.remove(m);
			}
		}
		return conflicts;
	}

	@Override
	public boolean equals(Object o) {

		if (o instanceof Meeting) {
			if (this.date.equals(((Meeting) o).date) && this.start.equals(((Meeting) o).start)
					&& this.end.equals(((Meeting) o).end) && this.colour.equals(((Meeting) o).colour)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public int compareTo(Meeting o) {

		if (!this.date.equals(o.date)) {
			return this.date.compareTo(o.date);
		} else {
			return this.start.compareTo(o.start);
		}
	}
}
