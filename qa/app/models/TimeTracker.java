package models;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Sets the starting time of the application for statistical purposes.
 * Calculations are based on change of date, week, month. The first day, week
 * month is counted as 1
 * 
 * @author tbrog
 * 
 */
public final class TimeTracker {

	private GregorianCalendar startTime;
	private static TimeTracker t;

	public TimeTracker(GregorianCalendar now) {
		startTime = now;
	}

	public static void setRealTimeTracker(GregorianCalendar now) {
		t = new TimeTracker(now);
	}

	public static TimeTracker getRealTimeTracker() {
		return t;
	}

	/**
	 * Get the days between the start of the time tracking and now
	 * 
	 * @return int the number of days
	 */
	public int getDays(GregorianCalendar now) {
		GregorianCalendar startClone, nowClone;
		int elapsed = 0;

		startClone = (GregorianCalendar) startTime.clone();
		nowClone = (GregorianCalendar) now.clone();

		startClone.clear(Calendar.MILLISECOND);
		startClone.clear(Calendar.SECOND);
		startClone.clear(Calendar.MINUTE);
		startClone.clear(Calendar.HOUR_OF_DAY);
		startClone.clear(Calendar.HOUR_OF_DAY);
		nowClone.clear(Calendar.MILLISECOND);
		nowClone.clear(Calendar.SECOND);
		nowClone.clear(Calendar.MINUTE);
		nowClone.clear(Calendar.HOUR_OF_DAY);

		while (startClone.before(nowClone)) {
			startClone.add(Calendar.DATE, 1);
			elapsed++;
		}
		return elapsed;
	}

	/**
	 * Get the weeks between the start of the time tracking and now
	 * 
	 * @return int the number of weeks
	 */
	public int getWeeks(GregorianCalendar now) {
		GregorianCalendar startClone, nowClone;
		int elapsed = 0;
		startClone = (GregorianCalendar) startTime.clone();
		nowClone = (GregorianCalendar) now.clone();

		startClone.clear(Calendar.MILLISECOND);
		startClone.clear(Calendar.SECOND);
		startClone.clear(Calendar.MINUTE);
		startClone.clear(Calendar.HOUR_OF_DAY);

		nowClone.clear(Calendar.MILLISECOND);
		nowClone.clear(Calendar.SECOND);
		nowClone.clear(Calendar.MINUTE);
		nowClone.clear(Calendar.HOUR_OF_DAY);

		while (startClone.before(nowClone)) {
			startClone.add(Calendar.WEEK_OF_YEAR, 1);
			elapsed++;
		}
		return elapsed;
	}

	/**
	 * Get the months between the start of the time tracking and now
	 * 
	 * @return int the number of months
	 */
	public int getMonths(GregorianCalendar now) {
		GregorianCalendar startClone, nowClone;
		int elapsed = 0;

		startClone = (GregorianCalendar) startTime.clone();
		nowClone = (GregorianCalendar) now.clone();

		startClone.clear(Calendar.MILLISECOND);
		startClone.clear(Calendar.SECOND);
		startClone.clear(Calendar.MINUTE);
		startClone.clear(Calendar.HOUR_OF_DAY);

		nowClone.clear(Calendar.MILLISECOND);
		nowClone.clear(Calendar.SECOND);
		nowClone.clear(Calendar.MINUTE);
		nowClone.clear(Calendar.HOUR_OF_DAY);

		while (startClone.before(nowClone)) {
			startClone.add(Calendar.MONTH, 1);
			elapsed++;
		}
		return elapsed;
	}

}
