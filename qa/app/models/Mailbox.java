package models;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * A representation of notification collection. Each user has at least one
 * mailbox - their personal one - and possibly more. For example, a moderator
 * gains access to the global mod-mailbox.
 * 
 * All methods to access Notifications return them in their natural order.
 * 
 */
public class Mailbox implements IMailbox {
	private SortedMap<Integer, Notification> notifications;

	public Mailbox() {
		this.notifications = new TreeMap();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see models.IMailbox#recieve(models.Notification)
	 */
	public void recieve(Notification notification) {
		this.notifications.put(notification.id(), notification);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see models.IMailbox#getAllNotifications()
	 */
	public List<Notification> getAllNotifications() {
		List<Notification> all = new LinkedList();
		for (Notification notification : this.notifications.values()) {
			if (notification.isDeleted() || notification.getAbout().isDeleted()) {
				deleteNotification(notification.id());
			} else {
				all.add(notification);
			}
		}
		return sort(all);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see models.IMailbox#getRecentNotifications()
	 */
	public List<Notification> getRecentNotifications() {
		List<Notification> recent = new LinkedList();
		for (Notification notification : getAllNotifications()) {
			if (notification.isVeryRecent()) {
				recent.add(notification);
			}
		}
		return sort(recent);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see models.IMailbox#getNewNotifications()
	 */
	public List<Notification> getNewNotifications() {
		List<Notification> unread = new LinkedList();
		for (Notification notification : getAllNotifications()) {
			if (notification.isNew()) {
				unread.add(notification);
			}
		}
		return sort(unread);
	}

	public void deleteNotification(int id) {
		this.notifications.remove(id);
	}

	private List<Notification> sort(List<Notification> l) {
		Collections.sort(l);
		return l;
	}

	public String toString() {
		return "MB[" + this.notifications.size() + "]";
	}

	public void delete() {
		for (Notification notification : getAllNotifications()) {
			notification.unregister();
		}
	}
}
