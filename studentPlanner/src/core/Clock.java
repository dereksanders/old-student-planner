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

						// Date has incremented, mark courses complete as necessary.
						Clock.pc.markCoursesComplete(Clock.now.toLocalDate());
					}
				}

				Clock.now = LocalDateTime.now();
			}
		};

		TimerTask updateEvents = new TimerTask() {

			@Override
			public void run() {

				// Check every tick if an event's end time has passed.
				Clock.pc.markEventsComplete(Clock.now);
			}
		};

		Timer timer = new Timer("update");
		Timer events = new Timer("events");

		// Tick every second.
		timer.scheduleAtFixedRate(update, 0, 1000);

		// Tick every minute.
		events.scheduleAtFixedRate(updateEvents, 1000, 60000);
	}
}
