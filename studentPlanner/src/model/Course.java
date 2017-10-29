package model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * The Class Course.
 */
public class Course implements Comparable<Course>, Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public String name;
	public String departmentID;
	public int code;
	public ArrayList<Term> terms;
	public ArrayList<MeetingSet> meetingSets;
	public ArrayList<CourseEvent> events;
	public String color;

	public double percentDone;
	public double gradeSoFar;
	public double cumulativeGrade;

	public boolean completed;

	/**
	 * Instantiates a new course.
	 *
	 * @param name
	 *            the name
	 * @param departmentID
	 *            the department ID
	 * @param code
	 *            the code
	 * @param terms
	 *            the terms
	 * @param meetingSets
	 *            the meeting sets
	 * @param events
	 *            the events
	 * @param colour
	 *            the colour
	 */
	public Course(String name, String departmentID, int code, ArrayList<Term> terms, ArrayList<MeetingSet> meetingSets,
			ArrayList<CourseEvent> events, String colour) {
		this.name = name;
		this.departmentID = departmentID;
		this.code = code;
		this.terms = terms;
		this.meetingSets = meetingSets;
		this.events = events;
		this.color = colour;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	public Course clone() {
		return new Course(this.name, this.departmentID, this.code, this.terms, this.meetingSets, this.events,
				this.color);
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

	/**
	 * Calc grades.
	 */
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
