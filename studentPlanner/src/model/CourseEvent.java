package model;

import java.time.LocalDateTime;

/**
 * The Class CourseEvent.
 */
public class CourseEvent extends CalendarEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public double grade = 0;
	public boolean gradeEntered = false;
	public Course course;

	/**
	 * Instantiates a new course event.
	 *
	 * @param name
	 *            the name
	 * @param colour
	 *            the colour
	 * @param start
	 *            the start
	 * @param end
	 *            the end
	 * @param weight
	 *            the weight
	 */
	public CourseEvent(String name, String colour, LocalDateTime start, LocalDateTime end, double weight) {

		super(name, start, end);

		this.colour = colour;
		this.weight = weight;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see model.CalendarEvent#toString()
	 */
	@Override
	public String toString() {
		return name;
	}
}
