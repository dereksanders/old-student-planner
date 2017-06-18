package model;

import java.time.LocalDate;
import java.util.ArrayList;

import core.ProfileController;

/**
 * The Class Course.
 */
public class Course implements Comparable<Course> {

	public String name;
	public String departmentID;
	public int code;
	public LocalDate start;
	public LocalDate end;
	public ArrayList<Meeting> meetings;
	public ArrayList<CourseEvent> events;
	public String colour;

	public double percentDone;
	public double gradeSoFar;
	public double cumulativeGrade;

	/**
	 * Instantiates a new course.
	 *
	 * @param name
	 *            the name
	 * @param departmentID
	 *            the department ID
	 * @param code
	 *            the code
	 * @param start
	 *            the start
	 * @param end
	 *            the end
	 * @param meetings
	 *            the meetings
	 * @param events
	 *            the events
	 * @param colour
	 *            the colour
	 */
	public Course(String name, String departmentID, int code, LocalDate start, LocalDate end,
			ArrayList<Meeting> meetings, ArrayList<CourseEvent> events, String colour) {
		this.name = name;
		this.departmentID = departmentID;
		this.code = code;
		this.start = start;
		this.end = end;
		this.meetings = meetings;
		this.events = events;
		this.colour = colour;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	public Course clone() {
		return new Course(this.name, this.departmentID, this.code, this.start, this.end, this.meetings, this.events,
				this.colour);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return departmentID + " " + code;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {

		if (o instanceof Course) {
			if (this.name.equals(((Course) o).name) && this.departmentID.equals(((Course) o).departmentID)
					&& this.code == ((Course) o).code) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Gets the terms.
	 *
	 * @param pc
	 *            the pc
	 * @return the terms
	 */
	public ArrayList<Term> getTerms(ProfileController pc) {
		return pc.findTermsBetween(this.start, this.end);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Course o) {
		if (this.departmentID.compareTo(o.departmentID) == 0) {
			if (this.code < o.code) {
				return -1;
			} else {
				return 1;
			}
		} else if (this.departmentID.compareTo(o.departmentID) < 0) {
			return -1;
		} else {
			return 1;
		}
	}

	public void calcGrades() {

		this.percentDone = 0;
		this.cumulativeGrade = 0;
		this.gradeSoFar = 0;

		for (CourseEvent e : this.events) {

			if (e.gradeEntered) {
				this.percentDone += e.weight;
				this.cumulativeGrade += e.grade * (e.weight / 100);
				this.gradeSoFar += e.grade * e.weight;
			}
		}

		if (this.percentDone != 0) {
			this.gradeSoFar = this.gradeSoFar / this.percentDone;
		}
	}
}
