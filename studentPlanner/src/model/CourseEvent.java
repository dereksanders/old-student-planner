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

	public enum STATES {
		NOT_STARTED(0), IN_PROGRESS(1), SUBMITTED(2), GRADED(3);

		public int val;

		private STATES(int val) {
			this.val = val;
		}
	};

	public enum TYPES {
		ASSIGNMENT(0), TEST(1), OTHER(2);

		public int val;

		private TYPES(int val) {
			this.val = val;
		}
	}

	public double grade = 0;

	public Course course;
	public int type;
	public int state;

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
	public CourseEvent(String name, String color, int type, LocalDateTime start, LocalDateTime end, double weight) {

		super(name, color, start, end);

		this.type = type;
		this.weight = weight;
		this.state = STATES.NOT_STARTED.val;
	}
}
