package models.database;

import java.util.Collection;

import models.IMailbox;
import models.User;

public interface IUserDatabase {

	/**
	 * Get the <code>User</code> with the given name.
	 * 
	 * @param name
	 *            unique user name
	 * @return a <code>User</code> or null if the given name doesn't exist.
	 */
	public User get(String name);

	/**
	 * Creates a <code>User</code> with the given credentials. Asserts that
	 * <code>needsSignUp(username)</code> before executing this and
	 * <code>register(username,password,email) == get(name)</code>
	 * 
	 * @param username
	 *            unique identifier
	 * @param password
	 * 
	 * @param email
	 * 
	 * @return The user with this credentials
	 */
	public User register(String username, String password, String email);

	/**
	 * Checks at Sign Up if the entered username is available. This way we can
	 * avoid having two User called "SoMeThinG" and "SoMetHinG" which might be
	 * hard to distinguish
	 * 
	 * @param username
	 * @return True iff there is no <code>User</code> of that name
	 */
	public boolean isAvailable(String username);

	/**
	 * Deletes the user from the database without clean up.
	 * 
	 * @param name
	 */

	public void remove(String name);

	/**
	 * A collection of all registered Users in the system.
	 * 
	 * @return
	 */

	public Collection<User> all();

	/**
	 * The number of all registered Users.
	 * 
	 * @return int n >= 0
	 */

	public int count();

	/**
	 * Deletes every user from the DB.
	 */

	public void clear();

	/**
	 * Add a User directly.
	 * 
	 * @param user
	 */

	public void add(User user);

	/**
	 * Get moderating crowd.
	 */
	public Collection<User> allModerators();

	/**
	 * Returns the mailbox that every moderator can read.
	 */

	public IMailbox getModeratorMailbox();
}
