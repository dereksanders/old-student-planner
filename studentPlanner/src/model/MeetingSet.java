package model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;

public class MeetingSet implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static String NO_REPEAT = "Never";
	public static String WEEKLY_REPEAT = "Weekly";
	public static String BIWEEKLY_REPEAT = "Bi-Weekly";
	public static String MONTHLY_REPEAT = "Monthly";

	private ArrayList<Meeting> meetings;
	public String repeat;

	public MeetingSet() {
		this.meetings = new ArrayList<>();
	}

	public MeetingSet(ArrayList<Meeting> meetings) {
		this.meetings = meetings;
	}

	public LocalDate getStart() {
		return this.meetings.get(0).date;
	}

	public LocalDate getEnd() {
		return this.meetings.get(this.meetings.size() - 1).date;
	}

	public void addMeeting(Meeting m) {
		this.meetings.add(m);
	}

	public ArrayList<Meeting> getMeetings() {
		return this.meetings;
	}

	public Course getCourse() {
		return this.meetings.get(0).course;
	}

	public void setCourse(Course c) {
		for (Meeting m : this.meetings) {
			m.course = c;
		}
	}

	@Override
	public String toString() {
		return "MeetingSet " + this.hashCode() + ": " + this.meetings.size() + " meetings repeating " + repeat;
	}
}
