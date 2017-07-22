package core;

import java.time.LocalDateTime;
import java.util.Timer;
import java.util.TimerTask;

public class Clock {

	public static LocalDateTime now;

	public Clock() {
		TimerTask update = new TimerTask() {

			@Override
			public void run() {
				Clock.now = LocalDateTime.now();
			}
		};

		Timer timer = new Timer("update");
		timer.scheduleAtFixedRate(update, 0, 10000);
	}
}
