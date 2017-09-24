package model;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * The Class Meeting.
 */
public class CourseMeeting extends Meeting {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static String[] TYPES = { "Lecture", "Tutorial", "Lab", "Seminar", "Other" };

	public Course course;

	/**
	 * Instantiates a new meeting.
	 *
	 * @param meetingType
	 *            the meeting type
	 * @param date
	 *            the date
	 * @param start
	 *            the start
	 * @param end
	 *            the end
	 * @param location
	 *            the location
	 */
	public CourseMeeting(Course course, String meetingType, LocalDate date, LocalTime start, LocalTime end,
			String location) {

		super(course.toString(), course.color, meetingType, date, start, end, location);
		this.course = course;
	}
}
