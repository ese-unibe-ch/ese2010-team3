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

	public Collection<User> all() {
		return users.values();
	}

	public int count() {
		return users.size();
	}

	public void clear(boolean keepAdmins) {
		Collection<User> mods = this.allModerators();
		users.clear();
		for (Notification n : moderatorMailbox.getAllNotifications()) {
			n.delete();
		}

		if (keepAdmins) {
			for (User mod : mods) {
				users.put(mod.getName().toLowerCase(), mod);
			}
		}
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

	/**
	 * Remove all references to the <code>User</code> when it's being deleted
	 * (Callback method).
	 * 
	 * @see models.helpers.ICleanup#cleanUp(java.lang.Object)
	 */
	public void cleanUp(User user) {
		this.users.remove(user.getName().toLowerCase());
	}
}
