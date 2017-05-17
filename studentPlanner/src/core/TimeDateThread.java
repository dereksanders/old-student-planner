package core;

import java.time.LocalDateTime;

public class TimeDateThread extends Thread {

	public LocalDateTime current;
	LocalDateTime cutoffDate;
	boolean running;

	public void update() {
		current = LocalDateTime.now();
		// java.util.Date curDate = new java.util.Date();
		// Calendar c = Calendar.getInstance();
		// c.setTime(curDate);
		// currentTime = new Time(c.get(Calendar.HOUR_OF_DAY),
		// c.get(Calendar.MINUTE));
		// currentDate = new
		// Date(Date.convertDayInt(c.get(Calendar.DAY_OF_WEEK)),
		// c.get(Calendar.DATE),
		// c.get(Calendar.MONTH), c.get(Calendar.YEAR));
		// currentLocalDate = LocalDate.now();
		// upcomingCutoffDate = currentLocalDate.plusDays(14);
	}
}
