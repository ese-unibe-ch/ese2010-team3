package models.database;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import models.Item;
import models.User;

public interface IUserDatabase {
	
	/**
	 * Get the <code>User</code> with the given name.
	 * 
	 * @param name
	 * @return a <code>User</code> or null if the given name doesn't exist.
	 */
	public User get(String name);
	
	/**
	 * Creates a <code>User</code> with the given credentials.
	 * @param username
	 * @param password
	 * @return
	 */
	public User register(String username, String password);
	
	/**
	 * Validate if the <code>User</code> is already in our database.
	 * 
	 * @param username
	 * @return True iff there is no <code>User</code> of that name
	 */
	public boolean needSignUp(String username);

	public void remove(String name);

	public Collection<User> all();
	
	public int count();

	public void clear();
}
