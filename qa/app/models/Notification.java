package models;

/**
 * A notification about a (recent) change such as a newly added answer to a
 * question.
 */
public class Notification extends Item implements Comparable<Notification> {

	/** What this notification is all about. */
	private final Entry about;

	/** Whether this notification has been seen by the user. */
	protected boolean isNew;

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
		this.isNew = true;
	}

	/**
	 * Gets what the notification is all about.
	 * 
	 * @return what the notification is all about.
	 */
	public Entry getAbout() {
		return this.about;
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
		return this.isNew;
	}

	/**
	 * Resets the isNew flag, marking this notification as acknowledged (but not
	 * yet deleted)
	 */
	public void unsetNew() {
		this.isNew = false;
	}

	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Notification n) {
		// sort notifications most-recent one first
		return n.getID() - getID();
	}
}
