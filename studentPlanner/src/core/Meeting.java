package core;

import java.time.LocalTime;
import java.util.ArrayList;

import courseSchedule.CourseSchedule;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;

public class Meeting implements Comparable<Meeting> {

	public String meetingType;
	public LocalTime start;
	public LocalTime end;
	public String dayOfWeek;
	public int dayOfWeekInt;
	public String location;
	public String colour;

	public Meeting(String meetingType, LocalTime start, LocalTime end, String dayOfWeek, String location) {
		this.meetingType = meetingType;
		this.start = start;
		this.end = end;
		this.dayOfWeek = dayOfWeek;
		this.dayOfWeekInt = dayToInt(dayOfWeek);
		this.location = location;
	}

	private int dayToInt(String dayOfWeek) {

		int dayVal = 0;

		if (dayOfWeek == "Monday") {
			return 1;
		} else if (dayOfWeek == "Tuesday") {
			return 2;
		} else if (dayOfWeek == "Wednesday") {
			return 3;
		} else if (dayOfWeek == "Thursday") {
			return 4;
		} else if (dayOfWeek == "Friday") {
			return 5;
		} else if (dayOfWeek == "Saturday") {
			return 6;
		} else if (dayOfWeek == "Sunday") {
			return 7;
		}

		return dayVal;
	}

	@Override
	public String toString() {
		return this.dayOfWeek + ", " + this.start + " - " + this.end + " in " + this.location + " (" + meetingType
				+ ")";
	}

	public ArrayList<Meeting> conflictsWith(ArrayList<Meeting> meetings) {
		ArrayList<Meeting> conflicts = new ArrayList<>();
		conflicts.addAll(meetings);
		for (Meeting m : meetings) {
			if (this.dayOfWeekInt != m.dayOfWeekInt) {
				conflicts.remove(m);
			} else if (m.end.compareTo(this.start) <= 0) {
				conflicts.remove(m);
			} else if (this.end.compareTo(m.start) <= 0) {
				conflicts.remove(m);
			}
		}
		return conflicts;
	}

	public ArrayList<Meeting> conflictsWith(ObservableList<Course> courses) {
		ArrayList<Meeting> allMeetings = new ArrayList<>();
		for (Course c : courses) {
			allMeetings.addAll(c.meetings);
		}
		return this.conflictsWith(allMeetings);
	}

	public static void deleteMeetings(ArrayList<Meeting> conflicts) {
		for (Meeting m : conflicts) {
			Planner.courseColors.get(Color.web(m.colour)).meetings.remove(m);
			Planner.dayMeetings.del(m.dayOfWeek, m);
			CourseSchedule.setTodaysMeetings();
			for (Term t : Planner.courseColors.get(Color.web(m.colour)).terms) {
				CourseSchedule.removeFromSchedule(m, t);
			}
		}
	}

	@Override
	public int compareTo(Meeting o) {
		return this.start.compareTo(o.start);
	}
}
