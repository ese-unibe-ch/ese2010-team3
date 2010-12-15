package models;

import java.util.Date;

public class SysInfo {

	private static SystemInformation instance = new SystemInformation();

	public static SystemInformation mockWith(SystemInformation mock) {
		SystemInformation previous = instance;
		instance = mock;
		return previous;
	}

	/**
	 * Usage: <code>SysInfo.now()</code>
	 * 
	 * @return A Date representing the momentary... date (and time).
	 */
	public static Date now() {
		return instance.now();
	}

	/**
	 * Checks if the application runs in Test Mode and nobody can be blocked for
	 * spamming or cheating.
	 */
	public static boolean isInTestMode() {
		return instance.isInTestMode();
	}

	/**
	 * Sets the Test Mode Status for the whole application to make sure we can
	 * post as much as needed without being banned for spamming or cheating
	 */
	public static void setTestMode(boolean testMode) {
		instance.setTestMode(testMode);
	}

}
