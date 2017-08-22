package org.gooru.nucleus.search.indexers.app.jobs;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MonthlyTimer {
  private static final Logger LOGGER = LoggerFactory.getLogger(MonthlyTimer.class);
  private final Runnable task;

  // when
  private final int dayOfMonth;
  private final int hourOfDay;
  private final int minutes;

  // The current timer
  private Timer currentTimer = new Timer();// to avoid NPE

  public void cancelCurrent() {
    currentTimer.cancel();// cancel this execution;
    currentTimer.purge(); // removes the timertask so it can be gc'ed
  }

  // create a new instance
  public static MonthlyTimer schedule(Runnable runnable, int dayOfMonth, int hourOfDay, int minutes) {
    return new MonthlyTimer(runnable, dayOfMonth, hourOfDay, minutes);
  }

  private MonthlyTimer(Runnable runnable, int day, int hour, int minutes) {
    this.task = runnable;
    this.dayOfMonth = day;
    this.hourOfDay = hour;
    this.minutes = minutes;
    schedule();
  }

  // Schedules the task for execution on next month.
  private void schedule() {
    cancelCurrent();
    currentTimer = new Timer(); // assigning a new instance
    // will allow the previous Timer to be gc'ed

    currentTimer.schedule(new TimerTask() {
      public void run() {
        try {
          task.run();
        } finally {
          schedule();// schedule for the next month
        }
      }
    }, nextDate());
  }

  // Do the next date stuff
  private Date nextDate() {
    Calendar runDate = Calendar.getInstance();
    LOGGER.info("CURRENT TIME: : {}",runDate.getTime());
/*    runDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
    runDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
    runDate.set(Calendar.MINUTE, 0);*/
    runDate.add(Calendar.MINUTE, minutes);
    LOGGER.info("SCHEDULED TIME : : {}",runDate.getTime());// set to next month
    return runDate.getTime();
  }
}
