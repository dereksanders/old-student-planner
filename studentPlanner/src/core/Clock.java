package core;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Timer;
import java.util.TimerTask;

public class Clock {

	public static LocalDateTime now;
	private static ProfileController pc;

	public Clock(ProfileController pc) {
		Clock.pc = pc;

		TimerTask update = new TimerTask() {

			@Override
			public void run() {

				// The first time this task runs, Clock.now will be null.
				if (Clock.now != null) {
					if (Clock.now.toLocalDate().isBefore(LocalDate.now())) {
						// Date has incremented, mark courses and events complete as necessary.
						Clock.pc.markCoursesComplete(Clock.now.toLocalDate());
						Clock.pc.markEventsComplete(Clock.now.toLocalDate());
					}
				}

				Clock.now = LocalDateTime.now();
			}
		};

		Timer timer = new Timer("update");
		timer.scheduleAtFixedRate(update, 0, 10000);
	}
}
