package models;

import models.helpers.ICleanup;

/**
 * A notification about a (recent) change such as a newly added answer to a
 * question.
 * 
 * @author sbuenzli
 */
public class Notification extends Item implements Comparable<Notification> {

	/** What this notification is all about. */
	private final Entry about;

	/** Whether this notification has been seen by the user. */
	protected boolean isNew;

	/** The mailbox this notification belongs to. */
	private final ICleanup<Notification> cleaner;

	/**
	 * Sends a Notification to a Mailbox, signifying that e.g. an answer was
	 * posted to a watched question or an Entry was reported as spam. Creating a
	 * notification will automatically add it to the mailbox that's passed in.
	 * 
	 * @param mailbox
	 *            the mailbox that should receive the message
	 * @param about
	 *            what Entry this is all about.
	 */
	public Notification(User owner, Entry about, ICleanup<Notification> cleaner) {
		super(owner);
		this.about = about;
		this.isNew = true;
		this.cleaner = cleaner;
		about.registerNotification(this);
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
	 * The interval during which a notification is considered to be very recent.
	 */
	private final int VERY_RECENT_INTERVAL_IN_MS = 5 * 60 * 1000;

	/**
	 * Checks if the notification is very recent (i.e. no older than 5 minutes).
	 * 
	 * @return true, if it is very recent, ie no older than 5 minutes
	 */
	public boolean isVeryRecent() {
		return SysInfo.now().getTime() - this.timestamp().getTime() <= VERY_RECENT_INTERVAL_IN_MS;
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
	 * Sort notifications most-recent one first (i.e. in order of object
	 * creation, not timestamp).
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Notification n) {
		return n.id() - this.id();
	}

	/**
	 * Removes all references to this <code>Notification</code> from the mailbox
	 * this notification is in and from the <code>Entry</code> this
	 * <code>Notification</code> is about.
	 * 
	 * @see models.Item#delete()
	 */
	@Override
	public void delete() {
		this.about.cleanUp(this);
		this.cleaner.cleanUp(this);
		super.delete();
	}

	@Override
	public String toString() {
		return "N[" + this.owner().toString() + this.about.toString()
				+ "]";
	}
}
