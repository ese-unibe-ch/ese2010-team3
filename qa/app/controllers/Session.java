package controllers;

import models.User;

public class Session implements ISession {
	private static ISession instance = new Session();

	public static ISession mockWith(ISession session) {
		ISession previous = instance;
		instance = session;
		return previous;
	}

	public static User user() {
		return instance.currentUser();
	}

	public User currentUser() {
		if (!Security.isConnected())
			return null;
		return Database.users().get(Security.connected());
	}
}
