package core;

import java.util.ArrayList;

public class Course implements Comparable<Course> {

	// "required": ["name", "departmentID", "code", "terms", "meetings",
	// "deliverables", "color"],

	public String name;
	public String departmentID;
	public int code;
	public ArrayList<Term> terms;
	public ArrayList<Meeting> meetings;
	public ArrayList<Deliverable> deliverables;
	public double grade;
	public String colour;

	public Course(String name, String departmentID, int code, ArrayList<Term> terms, ArrayList<Meeting> meetings,
			ArrayList<Deliverable> deliverables, String colour) {
		this.name = name;
		this.departmentID = departmentID;
		this.code = code;
		this.terms = terms;
		this.meetings = meetings;
		this.deliverables = deliverables;
		this.colour = colour;
	}

	public Course(String name, String departmentID, int code, ArrayList<Term> terms, ArrayList<Meeting> meetings,
			String colour) {
		this.name = name;
		this.departmentID = departmentID;
		this.code = code;
		this.terms = terms;
		this.meetings = meetings;
		this.deliverables = new ArrayList<>();
		this.colour = colour;
	}

	public Course clone() {
		return new Course(this.name, this.departmentID, this.code, this.terms, this.meetings, this.deliverables,
				this.colour);
	}

	@Override
	public String toString() {
		return departmentID + " " + code;
	}

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
}
