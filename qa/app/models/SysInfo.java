package models;

import java.util.Date;

/**
 * A mockable kind-of-Singleton for providing access to system-wide state such
 * as the current date and whether the application is in test mode (where
 * anti-spam measures are disabled).
 */
public class SysInfo {

	private static SystemInformation instance = new SystemInformation();

	/**
	 * Replace the current SystemInformation object with a mock-object (e.g. for
	 * testing). You might want to save the returned previous SystemInformation
	 * object in order to restore it after you're done.
	 * 
	 * @param mock
	 *            the mock-object
	 * @return the previous SystemInformation object
	 */
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
	 * 
	 * @return true, if is in test mode
	 */
	public static boolean isInTestMode() {
		return instance.isInTestMode();
	}

	/**
	 * Sets the Test Mode Status for the whole application to make sure we can
	 * post as much as needed without being banned for spamming or cheating.
	 * 
	 * @param testMode
	 *            the new test mode
	 */
	public static void setTestMode(boolean testMode) {
		instance.setTestMode(testMode);
	}

}
