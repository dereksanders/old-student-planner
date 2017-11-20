package model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

import utility.GenericHashTable;
import utility.GenericLinkedHashTable;
import utility.Pretty;

/**
 * The Class Term.
 */
public class Term implements Comparable<Term>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public enum STATES {
		NOT_STARTED(0), IN_PROGRESS(1), COMPLETED(2), GRADED(3);

		public int val;

		private STATES(int val) {
			this.val = val;
		}
	};

	public String name;
	public LocalDate start;
	public LocalDate end;

	public int state;
	public double avg;
	public double avgSoFar;

	public ArrayList<Course> courses;
	public GenericHashTable<String, Course> courseColors;
	public ArrayList<MeetingSet> nonCourseMeetingSets;
	public GenericLinkedHashTable<LocalDate, Meeting> dayMeetings;
	public GenericLinkedHashTable<LocalDate, CalendarEvent> dateEvents;

	// Dashboard
	public ArrayList<CalendarEvent> priorities;

	/* Course Schedule params */
	public int maxDay;
	public LocalTime minStart;
	public LocalTime maxEnd;

	/**
	 * Instantiates a new term.
	 *
	 * @param name
	 *            the name
	 * @param start
	 *            the start
	 * @param end
	 *            the end
	 */
	public Term(String name, LocalDate start, LocalDate end) {
		this.name = name;
		this.start = start;
		this.end = end;
		this.courses = new ArrayList<>();
		this.courseColors = new GenericHashTable<>(100);

		this.dayMeetings = new GenericLinkedHashTable<>(300, false);
		this.dateEvents = new GenericLinkedHashTable<>(300, true);
		this.nonCourseMeetingSets = new ArrayList<>();

		this.priorities = new ArrayList<>();

		/* Default course schedule params */
		this.minStart = LocalTime.of(8, 30);
		this.maxEnd = LocalTime.of(15, 00);
		this.maxDay = 5;
	}

	/**
	 * Updates this term's minStart, maxEnd, and maxDay.
	 */
	public void updateParams() {

		if (resetParams()) {

			if (!this.minStart.equals(LocalTime.of(0, 0))) {
				this.minStart = this.minStart.minusMinutes(30);
			}

			if (!this.maxEnd.equals(LocalTime.of(23, 30))) {
				this.maxEnd = this.maxEnd.plusMinutes(30);
			}

		} else {
			this.minStart = LocalTime.of(8, 30);
			this.maxEnd = LocalTime.of(15, 00);
		}
	}

	/**
	 * Helps updateParams.
	 *
	 * @return true if meetings exist in this term.
	 */
	private boolean resetParams() {

		boolean changesMade = false;

		this.minStart = LocalTime.of(23, 30);
		this.maxEnd = LocalTime.of(0, 0);
		this.maxDay = 5;

		for (MeetingSet ms : this.nonCourseMeetingSets) {
			for (Meeting m : ms.getMeetings()) {
				changesMade = true;
				addParams(m);
			}
		}

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

	/**
	 * Checks the meeting's start, end, and day against the term's minStart, maxEnd,
	 * and maxDay.
	 *
	 * @param m
	 *            the meeting
	 */
	private void addParams(Meeting m) {

		if (this.minStart.compareTo(m.start) > 0) {
			this.minStart = m.start;
		}
		if (this.maxEnd.compareTo(m.end) < 0) {
			this.maxEnd = m.end;
		}
		if (this.maxDay < m.date.getDayOfWeek().getValue()) {
			this.maxDay = m.date.getDayOfWeek().getValue();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {

		if (o instanceof Term) {

			if (this.start.isEqual(((Term) o).start) && this.end.isEqual(((Term) o).end)) {
				return true;
			}
		}

		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return name + " (" + Pretty.prettyShortDate(start) + " - " + Pretty.prettyShortDate(end) + ")";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
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

	/**
	 * Calculates this term's grades.
	 */
	public void calcGrades() {

		this.avg = 0;
		this.avgSoFar = 0;

		int numCoursesEnding = 0;

		for (Course c : this.courses) {

			if (c.getLastTerm().equals(this)) {

				this.avg += c.cumulativeGrade;
				this.avgSoFar += c.gradeSoFar;
				numCoursesEnding++;
			}
		}

		if (numCoursesEnding > 0) {

			this.avg = this.avg / numCoursesEnding;
			this.avgSoFar = this.avgSoFar / numCoursesEnding;
		}
	}
}
