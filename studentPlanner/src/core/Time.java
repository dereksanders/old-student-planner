package core;

public class Time implements Comparable<Time> {

	public int hour;
	public int minute;

	public Time(int hour, int minute) {
		this.hour = hour;
		this.minute = minute;
	}

	public static Time add(Time a, int minutes) {
		Time t = a.clone();
		if (a.minute + minutes > 60) {
			t.hour++;
			t.minute = (a.minute + minutes) % 60;
		} else {
			t.minute += minutes;
		}
		return t;
	}

	@Override
	public Time clone() {
		return new Time(this.hour, this.minute);
	}

	@Override
	public String toString() {
		if (this.minute < 10) {
			return this.hour + ":0" + this.minute;
		}
		return this.hour + ":" + this.minute;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Time) {
			if (((Time) o).hour == this.hour && ((Time) o).minute == this.minute) {
				return true;
			}
		}
		return false;
	}

	public static int getDistance(Time a, Time b, int interval) {
		Time t = a.clone();
		int i = 0;
		while (t.compareTo(b) < 0) {
			t = Time.add(t, interval);
			i++;
		}
		return i;
	}

	@Override
	public int compareTo(Time o) {
		return (60 * (this.hour - o.hour)) + (this.minute - o.minute);
	}
}