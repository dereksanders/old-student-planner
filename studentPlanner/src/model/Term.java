package model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.PriorityQueue;

import javafx.scene.paint.Color;
import utility.GenericHashTable;
import utility.Pretty;

public class Term implements Comparable<Term> {

	public String name;
	public LocalDate start;
	public LocalDate end;
	public double grade;
	public ArrayList<Course> courses;
	public GenericHashTable<Color, Course> courseColors;
	public ArrayList<PriorityQueue<Meeting>> dayMeetings;

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

		this.dayMeetings = new ArrayList<>(7);
		for (int i = 0; i < 7; i++) {
			this.dayMeetings.add(new PriorityQueue<Meeting>());
		}

		/* Default course schedule params */
		this.minStart = LocalTime.of(8, 30);
		this.maxEnd = LocalTime.of(15, 00);
		this.maxDay = 5;
	}

	public void updateParams() {

		resetParams();

		this.minStart = this.minStart.minusMinutes(30);
		this.maxEnd = this.maxEnd.plusMinutes(30);
	}

	private void resetParams() {

		this.maxDay = 5;

		if (this.courses.get(0).meetings.size() > 0) {

			this.minStart = this.courses.get(0).meetings.get(0).start;
			this.maxEnd = this.courses.get(0).meetings.get(0).end;

			if (this.courses.get(0).meetings.get(0).dayOfWeekInt > this.maxDay) {

				this.maxDay = this.courses.get(0).meetings.get(0).dayOfWeekInt;
			}
		}

		for (int i = 1; i < this.courses.size(); i++) {
			for (int j = 0; j < this.courses.get(i).meetings.size(); j++) {

				addParams(this.courses.get(i).meetings.get(j));
			}
		}
	}

	private void addParams(Meeting m) {

		if (this.maxDay < m.dayOfWeekInt) {
			this.maxDay = m.dayOfWeekInt;
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
}
