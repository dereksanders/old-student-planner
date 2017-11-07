package utility;

import java.time.LocalDate;

public class Pretty {

	public static String prettyDate(LocalDate d) {
		String day = d.getDayOfWeek().toString().substring(0, 1)
				+ d.getDayOfWeek().toString().substring(1, d.getDayOfWeek().toString().length()).toLowerCase();
		String month = d.getMonth().toString().substring(0, 1)
				+ d.getMonth().toString().substring(1, d.getMonth().toString().length()).toLowerCase();
		return day + ", " + month + " " + d.getDayOfMonth() + getDateEnding(Integer.toString(d.getDayOfMonth()));
	}

	public static String prettyShortDate(LocalDate d) {
		String s = d.toString();
		s = s.replaceAll("-", "/");
		return s;
	}

	public static String veryShortDate(LocalDate d) {

		String date = abbreviateMonth(d.getMonthValue()) + " " + d.getDayOfMonth()
				+ getDateEnding("" + d.getDayOfMonth());

		return date;
	}

	public static String getDateEnding(String date) {
		if (date.endsWith("1") && !date.endsWith("11")) {
			return "st";
		} else if (date.endsWith("2") && !date.endsWith("12")) {
			return "nd";
		} else if (date.endsWith("3") && !date.endsWith("13")) {
			return "rd";
		} else {
			return "th";
		}
	}

	public static String abbreviateMonth(int month) {

		String abbrev = "";

		switch (month) {
		case 1:
			abbrev = "Jan.";
			break;
		case 2:
			abbrev = "Feb.";
			break;
		case 3:
			abbrev = "Mar.";
			break;
		case 4:
			abbrev = "Apr.";
			break;
		case 5:
			abbrev = "May";
			break;
		case 6:
			abbrev = "June";
			break;
		case 7:
			abbrev = "July";
			break;
		case 8:
			abbrev = "Aug.";
			break;
		case 9:
			abbrev = "Sept.";
			break;
		case 10:
			abbrev = "Oct.";
			break;
		case 11:
			abbrev = "Nov.";
			break;
		case 12:
			abbrev = "Dec.";
			break;
		}

		return abbrev;
	}
}
