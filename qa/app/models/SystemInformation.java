package models;

import java.util.Date;

public class SystemInformation {

	public Date now() {
		return new Date();
	}

	private boolean testMode = false;

	/**
	 * Checks if the application runs in Test Mode and nobody can be blocked for
	 * spamming or cheating.
	 */
	public boolean isInTestMode() {
		return this.testMode;
	}

	/**
	 * Sets the Test Mode Status for the whole application to make sure we can
	 * post as much as needed without being banned for spamming or cheating
	 */
	public void setTestMode(boolean testMode) {
		this.testMode = testMode;
	}

}
