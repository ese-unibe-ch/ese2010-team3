package models.database.HotDatabase;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import models.User;
import models.database.IUserDatabase;

public class HotUserDatabase implements IUserDatabase {
	/** Tracks all users by their lowercase(!) usernames. */
	private static HashMap<String, User> users = new HashMap();

	public boolean isAvailable(String username) {
		return this.get(username) == null;
	}

	public User register(String username, String password) {
		User user = new User(username, password);
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
}
