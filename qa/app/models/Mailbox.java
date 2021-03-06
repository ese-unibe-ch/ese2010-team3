package models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import models.helpers.ICleanup;

/**
 * A representation of notification collection. Each user has at least one
 * mailbox - their personal one - and possibly more. For example, a moderator
 * gains access to the global mod-mailbox.
 * 
 * All methods to access Notifications return them in their natural order.
 * 
 */
public class Mailbox implements IMailbox, ICleanup<Notification> {
	private final SortedMap<Integer, Notification> notifications;
	private final String name;

	public Mailbox(String name) {
		this.name = name;
		this.notifications = new TreeMap();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see models.IMailbox#receive(models.Notification)
	 */
	public void notify(User user, Entry about) {
		Notification notification = new Notification(user, about, this);
		this.notifications.put(notification.id(), notification);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see models.IMailbox#getAllNotifications()
	 */
	public List<Notification> getAllNotifications() {
		List<Notification> result = new ArrayList(this.notifications.values());
		Collections.reverse(result);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see models.IMailbox#getRecentNotifications()
	 */
	public List<Notification> getRecentNotifications() {
		List<Notification> recent = new LinkedList();
		for (Notification notification : this.getAllNotifications()) {
			if (notification.isVeryRecent()) {
				recent.add(notification);
			}
		}
		return recent;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see models.IMailbox#getNewNotifications()
	 */
	public List<Notification> getNewNotifications() {
		List<Notification> unread = new LinkedList();
		for (Notification notification : this.getAllNotifications()) {
			if (notification.isNew()) {
				unread.add(notification);
			}
		}
		return unread;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see models.helpers.ICleanup#cleanUp(java.lang.Object)
	 */
	public void cleanUp(Notification notification) {
		this.notifications.remove(notification.id());
	}

	@Override
	public String toString() {
		return "MB[" + this.name + "(" + this.notifications.size() + ")" + "]";
	}

	public void delete() {
		for (Notification notification : this.getAllNotifications()) {
			notification.delete();
		}
	}

	public String getName() {
		return this.name;
	}
}
