package models;

import java.util.List;

/**
 * A mailbox collects all the notifications for a user or a user group.
 */
public interface IMailbox {

	/**
	 * Creates a new notification in the Mailbox (Factory-method). To remove a
	 * notification from the mailbox, just delete the notification.
	 * 
	 * @see Notification#delete()
	 * @param user
	 *            the user this notification is for (<code>null</code> for a
	 *            group of users)
	 * @param about
	 *            what the notification is about
	 */
	public void notify(User user, Entry about);

	/**
	 * Gets all Notifications regardless of status (being recent or not).
	 * 
	 * @return a list of all notifications sorted by creation time (newest
	 *         first).
	 */
	public List<Notification> getAllNotifications();

	/**
	 * Gets a list of recent Notifications (i.e. notifications created within
	 * the last 5 minutes)
	 * 
	 * @return a list of recent notifications sorted by creation time (newest
	 *         first).
	 */
	public List<Notification> getRecentNotifications();

	/**
	 * Gets the list of unread notifications.
	 * 
	 * @return a list of all unread notifications sorted by creation time
	 *         (newest first).
	 */
	public List<Notification> getNewNotifications();

	/**
	 * Gets the name of the mailbox. Useful for display and possibly debugging.
	 * 
	 * @return the name
	 */
	public String getName();

}