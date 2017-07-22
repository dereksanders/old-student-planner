package model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

import utility.Pretty;

/**
 * The Class Meeting.
 */
public class Meeting implements Comparable<Meeting>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static String[] TYPES = { "Lecture", "Tutorial", "Lab", "Seminar" };

	public String meetingType;
	public LocalDate date;
	public LocalTime start;
	public LocalTime end;
	public String location;
	public Course course;
	public MeetingSet set;
	public String repeat;

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
	public Meeting(Course course, String meetingType, LocalDate date, LocalTime start, LocalTime end, String location) {
		this.course = course;
		this.meetingType = meetingType;
		this.date = date;
		this.start = start;
		this.end = end;
		this.location = location;
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

	/**
	 * Tests for conflicts with the meetings provided and returns the conflicts.
	 *
	 * @param meetings
	 *            the meetings
	 * @return the conflicts
	 */
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {

		if (o instanceof Meeting) {
			if (this.date.equals(((Meeting) o).date) && this.start.equals(((Meeting) o).start)
					&& this.end.equals(((Meeting) o).end) && this.course.equals(((Meeting) o).course)) {
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
}
