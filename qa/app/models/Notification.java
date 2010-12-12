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

	private IMailbox mailbox;

	protected boolean isDeleted;

	private final NotificationType type;

	/**
	 * Sends a Notification to a Mailbox, signifying that an answer was posted
	 * to a watched question.
	 * 
	 * @param mailbox
	 *            the mailbox that should receive the message
	 * @param about
	 *            what Entry this is all about.
	 */
	public Notification(IMailbox mailbox, Entry about) {
		this(NotificationType.ANSWER, mailbox, about);
	}

	/**
	 * Sends a Notification to a Mailbox.
	 * 
	 * @param type
	 *            What kind of a notification this is. Might be ANSWER,
	 *            SPAM.
	 * @param mailbox
	 *            the mailbox that should receive the message
	 * @param about
	 *            what Entry this is all about.
	 */
	public Notification(NotificationType type, IMailbox mailbox, Entry about) {
		super(null);
		this.type = type;
		this.about = about;
		this.isNew = true;
		this.mailbox = mailbox;
		mailbox.receive(this);
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
	 * @return true, if it is very recent, ie no older than 5 minutes
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
	 * Sort notifications most-recent one first.
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Notification n) {
		return n.id() - id();
	}

	public void unregister() {
		this.mailbox.deleteNotification(id());
		super.unregister();
	}

	public String toString() {
		return "N[" + this.mailbox.toString() + this.about.toString() + "]";
	}

	public NotificationType getType() {
		return this.type;
	}

}
