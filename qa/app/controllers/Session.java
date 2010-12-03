package controllers;

import models.User;
import models.database.Database;

public class Session implements ISession {
	private static ISession instance;

	public static void mockWith(ISession session) {
		instance = session;
	}

	public static ISession get() {
		if (instance == null) {
			instance = new Session();
		}
		return instance;
	}

	public User currentUser() {
		if (!Secure.Security.isConnected())
			return null;
		return Database.get().users().get(Secure.Security.connected());
	}
}
