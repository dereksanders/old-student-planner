package model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

import utility.Pretty;

public class Meeting implements Comparable<Meeting>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static String[] TYPES = { "Club", "Work", "Recreation", "Other" };

	public String name;
	public String meetingType;
	public LocalDate date;
	public LocalTime start;
	public LocalTime end;
	public String location;
	public MeetingSet set;
	public String repeat;
	public String color;

	/**
	 * Instantiates a new meeting.
	 *
	 * @param meetingType
	 *            the meeting type
	 * @param date
	 *            the date
	 * @param start
	 *            the start
	 * @param end
	 *            the end
	 * @param location
	 *            the location
	 */
	public Meeting(String name, String color, String meetingType, LocalDate date, LocalTime start, LocalTime end,
			String location) {
		this.name = name;
		this.color = color;
		this.meetingType = meetingType;
		this.date = date;
		this.start = start;
		this.end = end;
		this.location = location;
	}

	/**
	 * Tests for conflicts with the meetings provided and returns the conflicts.
	 *
	 * @param possibleConflicts
	 *            the meetings
	 * @return the conflicts
	 */
	public ArrayList<Meeting> conflictsWith(ArrayList<Meeting> possibleConflicts) {

		ArrayList<Meeting> conflicts = new ArrayList<>();
		conflicts.addAll(possibleConflicts);

		for (Meeting m : possibleConflicts) {

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {

		if (o instanceof Meeting) {
			if (this.date.equals(((Meeting) o).date) && this.start.equals(((Meeting) o).start)
					&& this.end.equals(((Meeting) o).end)) {
				return true;
			}
		}

		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Meeting o) {

		if (!this.date.equals(o.date)) {
			return this.date.compareTo(o.date);
		} else {
			return this.start.compareTo(o.start);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return Pretty.prettyShortDate(this.date) + ", " + this.start + " - " + this.end + " in " + this.location + " ("
				+ meetingType + ")";
	}
}
