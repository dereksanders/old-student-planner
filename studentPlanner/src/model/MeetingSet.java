package model;

import java.time.LocalDate;
import java.util.ArrayList;

import javafx.scene.paint.Color;

public class MeetingSet {

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

	public Color getColor() {
		return Color.web(this.meetings.get(0).colour);
	}

	public void setColor(String color) {
		for (Meeting m : this.meetings) {
			m.colour = color;
		}
	}

	@Override
	public String toString() {
		return "MeetingSet " + this.hashCode() + ": " + this.meetings.size() + " meetings repeating " + repeat;
	}
}
