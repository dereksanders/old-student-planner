package core;

import java.time.LocalDateTime;

import javafx.scene.paint.Color;

public class Deliverable extends CalendarEvent {

	// "required": ["name", "course", "due", "weightPercent"]

	public LocalDateTime due;
	public double grade;
	public String colour;

	public Deliverable(String name, LocalDateTime due, double weight, double grade, String colour) {

		this.name = name;
		this.due = due;
		this.weight = weight;
		this.grade = grade;
		this.colour = colour;
	}

	@Override
	public String toString() {

		return Planner.courseColors.get(Color.web(this.colour)) + " " + this.name;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Deliverable) {
			return (this.name.equals(((Deliverable) o).name) && this.due.equals(((Deliverable) o).due));
		}

		return false;
	}

	@Override
	public int compareTo(CalendarEvent o) {
		return super.compareTo(o);
	}
}
