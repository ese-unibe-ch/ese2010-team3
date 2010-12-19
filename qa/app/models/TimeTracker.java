package models;

import java.util.Calendar;
import java.util.Date;
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

	private final GregorianCalendar startTime = new GregorianCalendar();

	public TimeTracker() {
		startTime.setTime(new Date());
	}

	public void setStartTime(Date startTime) {
		this.startTime.setTime(startTime);
	}

	/**
	 * Get the days between the start of the time tracking and now
	 * 
	 * @return int the number of days
	 */
	public int getDays() {
		return getDifference(Calendar.DATE);
	}

	/**
	 * Get the weeks between the start of the time tracking and now
	 * 
	 * @return int the number of weeks
	 */
	public int getWeeks() {
		return this.getDifference(Calendar.WEEK_OF_YEAR);
	}

	/**
	 * Get the months between the start of the time tracking and now
	 * 
	 * @return int the number of months
	 */
	public int getMonths() {
		return this.getDifference(Calendar.MONTH);
	}

	private int getDifference(int field) {
		GregorianCalendar startClone, nowClone;
		int elapsed = 0;

		startClone = (GregorianCalendar) this.startTime.clone();
		nowClone = new GregorianCalendar();
		nowClone.setTime(SysInfo.now());

		startClone.clear(Calendar.MILLISECOND);
		startClone.clear(Calendar.SECOND);
		startClone.clear(Calendar.MINUTE);
		startClone.clear(Calendar.HOUR_OF_DAY);

		nowClone.clear(Calendar.MILLISECOND);
		nowClone.clear(Calendar.SECOND);
		nowClone.clear(Calendar.MINUTE);
		nowClone.clear(Calendar.HOUR_OF_DAY);

		while (startClone.before(nowClone)) {
			startClone.add(field, 1);
			elapsed++;
		}
		return elapsed;
	}
}
