package controllers;

import models.User;

/**
 * The Session object is meant to be mockable for functional tests.
 */
public interface ISession {

	/**
	 * Gets the currently logged in user or <code>null</code> if there isn't
	 * any.
	 * 
	 * @return the logged in user
	 */
	public User currentUser();

}