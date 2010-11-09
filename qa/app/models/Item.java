package models;

import java.util.Date;

/**
 * A item owned by a {@link User}.
 * 
 * @author Simon Marti
 * @author Mirco Kocher
 * 
 */
public abstract class Item {

	private User owner;
	private final Date timestamp;

	/** This entry's ID. */
	private final int id;

	/** An auto-incrementing counter for producing unique values as IDs. */
	private static int auto_increment = 0;

	/**
	 * Since there's no ideal place for using an IDTable, we just count through
	 * all items, assigning them an auto-incremented value as ID.
	 * 
	 * @return the next ID value
	 */
	private synchronized int autoIncrementID() {
		return auto_increment++;
	}

	/**
	 * Create an <code>Item</code>.
	 * 
	 * @param owner
	 *            the {@link User} who owns the <code>Item</code>
	 */
	public Item(User owner) {
		this.owner = owner;
		this.timestamp = SystemInformation.get().now();
		this.id = autoIncrementID();
		owner.registerItem(this);
	}

	/**
	 * Get the owner of an <code>Item</code>.
	 * 
	 * @return the owner
	 */
	public User owner() {
		return owner;
	}

	/**
	 * Get the time the <code>Item</code> was created.
	 * 
	 * @return the creation date as a UNIX timestamp
	 */
	public Date timestamp() {
		return timestamp;
	}

	/**
	 * Gets this notification's ID value.
	 * 
	 * @return this notification's ID
	 */
	public int getID() {
		return this.id;
	}

	/**
	 * Unregisters the <code>Item</code> if it gets deleted.
	 */
	public void unregister() {
		unregisterUser();
	}

	/**
	 * Unregisters the <code>Item</code> to it's owner.
	 */
	protected void unregisterUser() {
		owner.unregister(this);
		owner = null;
	}
}
