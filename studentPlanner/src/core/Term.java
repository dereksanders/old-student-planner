package core;

import java.time.LocalDate;
import java.time.LocalTime;

import utility.Pretty;

public class Term implements Comparable<Term> {

	public String name;
	public LocalDate start;
	public LocalDate end;
	public double grade = 50;

	/* Course Schedule params */
	public int maxDay;
	public LocalTime minStart;
	public LocalTime maxEnd;

	public Term(String name, LocalDate start, LocalDate end) {
		this.name = name;
		this.start = start;
		this.end = end;

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

		for (Course c : Planner.termCourses.get(this)) {
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

		return 0;
	}
}
