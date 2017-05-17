package core;

public abstract class CalendarEvent implements Comparable<CalendarEvent> {

	// "required": ["name", "course", "due", "weightPercent"]

	public String name;
	public double weight;
	// TODO: NEEDS DATE FIELD HERE

	@Override
	public int compareTo(CalendarEvent arg0) {
		return (int) (this.weight - arg0.weight);
	}
}