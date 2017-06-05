package core;

import java.time.LocalDate;
import java.util.ArrayList;

public class Course implements Comparable<Course> {

	public String name;
	public String departmentID;
	public int code;
	public LocalDate start;
	public LocalDate end;
	public ArrayList<Meeting> meetings;
	public ArrayList<CalendarEvent> events;
	public double grade;
	public String colour;

	public Course(String name, String departmentID, int code, LocalDate start, LocalDate end,
			ArrayList<Meeting> meetings, ArrayList<CalendarEvent> events, String colour) {
		this.name = name;
		this.departmentID = departmentID;
		this.code = code;
		this.start = start;
		this.end = end;
		this.meetings = meetings;
		this.events = events;
		this.colour = colour;
	}

	public Course clone() {
		return new Course(this.name, this.departmentID, this.code, this.start, this.end, this.meetings, this.events,
				this.colour);
	}

	@Override
	public String toString() {
		return departmentID + " " + code;
	}

	@Override
	public boolean equals(Object o) {

		if (o instanceof Course) {
			if (this.name.equals(((Course) o).name) && this.departmentID.equals(((Course) o).departmentID)
					&& this.code == ((Course) o).code) {
				return true;
			}
		}

		return false;
	}

	@Override
	public int compareTo(Course o) {
		if (this.departmentID.compareTo(o.departmentID) == 0) {
			if (this.code < o.code) {
				return -1;
			} else {
				return 1;
			}
		} else if (this.departmentID.compareTo(o.departmentID) < 0) {
			return -1;
		} else {
			return 1;
		}
	}
}
