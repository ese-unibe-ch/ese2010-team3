package models;

/**
 * A notification about a (recent) change such as a newly added answer to a
 * question.
 */
public class Notification extends Item implements Comparable {

	/** What this notification is all about. */
	private final Entry about;

	/** Whether this notification has been seen by the user. */
	protected boolean isNew;

	/** This notification's ID. */
	private final int id;

	/** An auto-incrementing counter for producing unique values as IDs. */
	private static int auto_increment = 0;

	/**
	 * Since there's no ideal place for using an IDTable, we just count through
	 * all notifications, assigning them an auto-incremented value as ID.
	 * 
	 * @return the next ID value
	 */
	private synchronized int autoIncrementID() {
		return auto_increment++;
	}

	/**
	 * Instantiates a new notification.
	 * 
	 * @param owner
	 *            the owner
	 * @param about
	 *            what the notification is all about.
	 */
	public Notification(User owner, Entry about) {
		super(owner);
		this.about = about;
		isNew = true;
		id = autoIncrementID();
	}

	/**
	 * Gets what the notification is all about.
	 * 
	 * @return what the notification is all about.
	 */
	public Entry getAbout() {
		return about;
	}

	/**
	 * Checks if the notification is very recent.
	 * 
	 * @return true, if it is very recent
	 */
	public boolean isVeryRecent() {
		return SystemInformation.get().now().getTime() - timestamp().getTime() <= 5 * 60 * 1000;
	}

	/**
	 * Checks if the notification hasn't been seen yet.
	 * 
	 * @return true, if it is new
	 */
	public boolean isNew() {
		return isNew;
	}

	/**
	 * Resets the isNew flag, marking this notification as acknowledged (but not
	 * yet deleted)
	 */
	public void unsetNew() {
		isNew = false;
	}

	/**
	 * Gets this notification's ID value.
	 * 
	 * @return this notification's ID
	 */
	public int getID() {
		return id;
	}

	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object o) {
		// sort notifications most-recent one first
		return ((Notification) o).getID() - id;
	}
}
