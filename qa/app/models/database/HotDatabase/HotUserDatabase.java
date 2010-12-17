package models.database.HotDatabase;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import models.IMailbox;
import models.Mailbox;
import models.Notification;
import models.User;
import models.database.IUserDatabase;
import models.helpers.ICleanup;

public class HotUserDatabase implements IUserDatabase, ICleanup<User> {
	/** Tracks all users by their lowercase(!) usernames. */
	private final HashMap<String, User> users = new HashMap();
	private final IMailbox moderatorMailbox = new Mailbox("Moderators");

	public boolean isAvailable(String username) {
		return get(username) == null;
	}

	public User register(String username, String password, String email) {
		User user = new User(username, password, email, this);
		users.put(username.toLowerCase(), user);
		return user;
	}

	public User get(String name) {
		return users.get(name.toLowerCase());
	}

	public void remove(String name) {
		users.remove(name.toLowerCase());
	}

	public Collection<User> all() {
		return users.values();
	}

	public int count() {
		return users.size();
	}

	public void clear() {
		users.clear();
		for (Notification n : moderatorMailbox.getAllNotifications()) {
			moderatorMailbox.removeNotification(n.id());
		}
	}

	public void add(User user) {
		users.put(user.getName().toLowerCase(), user);
	}

	public Collection<User> allModerators() {
		Set<User> moderators = new HashSet();
		for (User user : users.values()) {
			if (user.isModerator()) {
				moderators.add(user);
			}
		}
		return moderators;
	}

	public IMailbox getModeratorMailbox() {
		return this.moderatorMailbox;
	}

	@Override
	public void cleanUp(User user) {
		this.remove(user.getName());
	}
}
