package models.database.HotDatabase;

import java.util.Collection;
import java.util.HashMap;

import models.User;
import models.database.IUserDatabase;

public class HotUserDatabase implements IUserDatabase {
	private static HashMap<String, User> users = new HashMap();

	public boolean needSignUp(String username) {
		return (users.get(username) == null);
	}

	public User register(String username, String password) {
		User user = new User(username, password);
		users.put(username, user);
		return user;
	}

	public User get(String name) {
		for (User user : users.values()) {
			if (user.getName().toLowerCase().equals(name.toLowerCase()))
				return users.get(user.getName());
		}
		return null;
	}

	public void remove(String name) {
		users.remove(name);
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
}
