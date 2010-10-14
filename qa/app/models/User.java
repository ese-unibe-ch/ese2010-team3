package models;

import java.util.HashMap;
import java.util.HashSet;

/**
 * A user with a name. Can contain {@link Item}s i.e. {@link Question}s,
 * {@link Answer}s, {@link Comment}s and {@link Vote}s. When deleted, the
 * <code>User</code> requests all his {@link Item}s to delete themselves.
 * 
 * @author Simon Marti
 * @author Mirco Kocher
 * 
 */
public class User {

	private String name;
	private String password;
	private String email;
	private HashSet<Item> items;

	private static HashMap<String, User> users = new HashMap();

	/**
	 * Creates a <code>User</code> with a given name.
	 * @param name the name of the <code>User</code>
	 */
	public User(String name, String password) {
		this.name = name;
		this.password = password;
		this.items = new HashSet<Item>();
		users.put(name, this);
	}

	/**
	 * Returns the name of the <code>User</code>.
	 * @return name of the <code>User</code>
	 */
	public String name() {
		return this.name;
	}

	public boolean checkPW(String password){
		return this.password.equals(password);
	}
	
	public static boolean needSignUp(String username){
    	return (User.get(username)==null);
    }
	
	public static void register(String username, String password){
		User user = new User(username, password);
	}
	
	public boolean checkeMail(String email){
		return this.email.equals(email);
	}
	
	/**
	 * Registers an {@link Item} which should be deleted in case the <code>User</code> gets deleted.
	 * 
	 * @param item the {@link Item} to register
	 */
	public void registerItem(Item item) {
		this.items.add(item);
	}

	/*
	 * Causes the <code>User</code> to delete all his {@link Item}s.
	 */
	public void delete() {
		// operate on a clone to prevent a ConcurrentModificationException
		HashSet<Item> clone = (HashSet<Item>) this.items.clone();
		for (Item item : clone)
			item.unregister();
		this.items.clear();
		users.remove(this.name);
	}

	/**
	 * Unregisters an {@link Item} which has been deleted.
	 * @param item the {@link Item} to unregister
	 */
	public void unregister(Item item) {
		this.items.remove(item);
	}

	/**
	 * Checks if an {@link Item} is registered and therefore owned by a
	 * <code>User</code>.
	 * @param item the {@link Item}to check
	 * @return true if the {@link Item} is registered
	 */
	public boolean hasItem(Item item) {
		return this.items.contains(item);
	}

	/**
	 * Get the <code>User</code> with the given name.
	 * @param name
	 * @return a <code>User</code> or null if the given name doesn't exist.
	 */
	public static User get(String name) {
		if (users.containsKey(name))
			return users.get(name);
		return null;
	}

	/**
	 * Anonymizes all questions, answers and comments by this user.
	 * 
	 * @param doAnswers
	 *            - whether to anonymize this user's answers as well
	 * @param doComments
	 *            - whether to anonymize this user's comments as well
	 */
	public void anonymize(boolean doAnswers, boolean doComments) {
		// operate on a clone to prevent a ConcurrentModificationException
		HashSet<Item> clone = (HashSet<Item>) this.items.clone();
		for (Item item : clone) {
			if (item instanceof Question || doAnswers && item instanceof Answer
					|| doComments && item instanceof Comment) {
				((Entry) item).anonymize();
				this.items.remove(item);
			}
		}
	}
}
