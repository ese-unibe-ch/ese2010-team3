package controllers;

import models.User;

/**
 * Security class to interface with Play!'s own authentication services and
 * inform it about valid username/password combinations.
 */
public class Security extends Secure.Security {

	/**
	 * Authenticates a user with a given password, also checking whether the
	 * user has actually confirmed the e-mail sent right after registration.
	 * 
	 * @param username
	 *            the username
	 * @param password
	 *            the password for this user
	 * @return true, if username and password match and the user has confirmed
	 *         the reception of the registration e-mail
	 */
	static boolean authenticate(String username, String password) {
		User user = Database.users().get(username);
		return user != null && user.checkPW(password) && user.isConfirmed();
	}

	/**
	 * Redirects to the homepage when the user logs out.
	 */
	static void onDisconnected() {
		Application.index(0);
	}
}
