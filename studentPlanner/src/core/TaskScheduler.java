package core;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Timer;
import java.util.TimerTask;

import planner.Planner;

public class TaskScheduler {

	private volatile static TaskScheduler uniqueInstance;

	private static ProfileController pc;
	private static LocalDateTime last;

	private TaskScheduler(LocalDateTime first, ProfileController pc) {

		TaskScheduler.last = first;
		TaskScheduler.pc = pc;

		TimerTask checkCompletion = new TimerTask() {

			@Override
			public void run() {

				if (TaskScheduler.last.toLocalDate().isBefore(LocalDate.now())) {

					// Date has incremented, mark courses complete as necessary.
					TaskScheduler.pc.markCoursesComplete(LocalDate.now());
				}

				TaskScheduler.pc.markEventsComplete(LocalDateTime.now());

				TaskScheduler.last = LocalDateTime.now();
			}
		};

		Timer second = new Timer("update");

		// Tick every second.
		second.scheduleAtFixedRate(checkCompletion, 0, 1000);
	}

	public static TaskScheduler getInstance(LocalDateTime first, ProfileController pc) {
		if (uniqueInstance == null) {
			synchronized (Planner.class) {
				if (uniqueInstance == null) {
					uniqueInstance = new TaskScheduler(first, pc);
				}
			}
		}
		return uniqueInstance;
	}
}
