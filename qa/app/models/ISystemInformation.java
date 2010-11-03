package models;

import java.util.Date;

public interface ISystemInformation {

	/**
	 * Usage: <code>SystemInformation.get().now()</code>
	 * @return A Date representing the momentary... date (and time).
	 */
	public Date now();

}