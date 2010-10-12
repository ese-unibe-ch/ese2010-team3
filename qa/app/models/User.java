package models;

import java.util.*;

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
	private Set<Item> items;

	private static HashMap<String, User> users = new HashMap();

	/**
	 * Creates a <code>User</code> with a given name.
	 * @param name the name of the <code>User</code>
	 */
	public User(String name) {
		this.name = name;
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
		for (Item item : this.items)
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
	  * Anonymizes all questions and answers by this user.
	  * @param doAnswers - whether to anonymize this user's answers as well 
	  */
	 public void anonymize(boolean doAnswers) {
		 for (Item item : this.items) {
			 if (item instanceof Question ||
					 item instanceof Answer && doAnswers) {
				 ((Entry)item).anonymize();
				 this.items.remove(item);
			 }
		 }
	 }

}
