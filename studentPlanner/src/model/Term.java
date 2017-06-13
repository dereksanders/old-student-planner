package model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

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

		/* Default course schedule params */
		this.maxDay = 5;
		this.minStart = LocalTime.of(8, 30);
		this.maxEnd = LocalTime.of(15, 00);
	}

	public void updateParams(Meeting m) {
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

	public void resetParams() {
		this.maxDay = 5;
		this.minStart = LocalTime.of(8, 30);
		this.maxEnd = LocalTime.of(15, 00);

		for (Course c : this.courses) {
			for (Meeting m : c.meetings) {
				updateParams(m);
			}
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
