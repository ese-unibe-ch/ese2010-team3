package controllers;

import models.User;

public class Session implements ISession{
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
		return User.get(Security.connected());
	}
}
