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

	public static String[] TYPES = { "Assignment", "Test", "Other" };

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
	public CourseEvent(String name, String color, LocalDateTime start, LocalDateTime end, double weight) {

		super(name, color, start, end);

		this.weight = weight;
	}
}
