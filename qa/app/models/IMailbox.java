package models;

import java.util.List;

public interface IMailbox {

	/**
	 * Sends the notification to the Mailbox.
	 * 
	 * @param notification
	 *            A non-null notification
	 */
	public void recieve(Notification notification);

	/**
	 * Gets all Notifications regardless of status.
	 * 
	 * @return
	 *         a list of all notifications sorted (desc) by id.
	 */
	public List<Notification> getAllNotifications();

	/**
	 * Gets the recent Notifications.
	 * 
	 * @return
	 *         a list of recent notifications sorted (desc) by id.
	 */
	public List<Notification> getRecentNotifications();

	/**
	 * Gets the unread notifications.
	 * 
	 * @return
	 *         a list of all unread notifications sorted (desc) by id.
	 */
	public List<Notification> getNewNotifications();

	/**
	 * Removes the identified notification from the mailbox.
	 * 
	 * @param id
	 *            unique id identifying the notification to be deleted.
	 */
	public void deleteNotification(int id);

}