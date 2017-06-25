package model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import javafx.scene.paint.Color;
import utility.GenericHashTable;
import utility.GenericLinkedHashTable;
import utility.Pretty;

public class Term implements Comparable<Term> {

	public String name;
	public LocalDate start;
	public LocalDate end;

	public double avg;
	public double avgSoFar;

	public ArrayList<Course> courses;
	public GenericHashTable<Color, Course> courseColors;
	public GenericLinkedHashTable<LocalDate, Meeting> dayMeetings;

	/* Course Schedule params */
	public int maxDay;
	public LocalTime minStart;
	public LocalTime maxEnd;

	public Term(String name, LocalDate start, LocalDate end) {
		this.name = name;
		this.start = start;
		this.end = end;
		this.courses = new ArrayList<>();
		this.courseColors = new GenericHashTable<>(100);

		this.dayMeetings = new GenericLinkedHashTable<>(300, true);

		/* Default course schedule params */
		this.minStart = LocalTime.of(8, 30);
		this.maxEnd = LocalTime.of(15, 00);
		this.maxDay = 5;
	}

	public void updateParams() {

		/* resetParams() should be true if any meetings exist in this term. */
		if (resetParams()) {
			this.minStart = this.minStart.minusMinutes(30);
			this.maxEnd = this.maxEnd.plusMinutes(30);
		} else {
			this.minStart = LocalTime.of(8, 30);
			this.maxEnd = LocalTime.of(15, 00);
		}
	}

	private boolean resetParams() {

		boolean changesMade = false;

		this.maxDay = 5;
		this.minStart = LocalTime.of(23, 30);
		this.maxEnd = LocalTime.of(0, 0);

		for (Course c : this.courses) {
			for (MeetingSet ms : c.meetingSets) {
				for (Meeting m : ms.getMeetings()) {
					changesMade = true;
					addParams(m);
				}
			}
		}

		return changesMade;
	}

	private void addParams(Meeting m) {

		if (this.maxDay < m.date.getDayOfWeek().getValue()) {
			this.maxDay = m.date.getDayOfWeek().getValue();
		}
		if (this.minStart.compareTo(m.start) > 0) {
			this.minStart = m.start;
		}
		if (this.maxEnd.compareTo(m.end) < 0) {
			this.maxEnd = m.end;
		}
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Term) {
			if (this.start.isEqual(((Term) o).start) && this.end.isEqual(((Term) o).end)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return name + " (" + Pretty.prettyShortDate(start) + " - " + Pretty.prettyShortDate(end) + ")";
	}

	@Override
	public int compareTo(Term arg0) {

		if (this.start.isBefore(arg0.start)) {
			return -1;
		}
		/*
		 * This should never happen as term dates should be mutually exclusive.
		 */
		else if (this.start.equals(arg0.start)) {
			return 0;
		} else {
			return 1;
		}
	}

	public void calcGrades() {

		this.avg = 0;
		this.avgSoFar = 0;

		for (Course c : this.courses) {
			this.avg += c.cumulativeGrade;
			this.avgSoFar += c.gradeSoFar;
		}

		if (this.courses.size() > 0) {
			this.avg = this.avg / this.courses.size();
			this.avgSoFar = this.avgSoFar / this.courses.size();
		}
	}
}
