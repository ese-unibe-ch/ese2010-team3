package controllers;

import models.User;

/**
 * A session Singleton giving the controllers global access to the currently
 * logged in user (if there is any). The Session object is mockable for
 * functional tests.
 */
public class Session implements ISession {

	/** The actual Session object. */
	private static ISession instance = new Session();

	/**
	 * Mock the session singleton with a different object for testing purposes
	 * so that e.g. no user database is required and log-in tests are still
	 * possible. Returns the previously active Session object so that it can
	 * later be restored with another call to mockWith.
	 * 
	 * @param session
	 *            the mock session object
	 * @return the previously used session object
	 */
	public static ISession mockWith(ISession session) {
		ISession previous = instance;
		instance = session;
		return previous;
	}

	/**
	 * @return the currently logged in user or <code>null</code> if there isn't
	 *         any
	 */
	public static User user() {
		return instance.currentUser();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see controllers.ISession#currentUser()
	 */
	public User currentUser() {
		if (!Security.isConnected())
			return null;
		return Database.users().get(Security.connected());
	}
}
