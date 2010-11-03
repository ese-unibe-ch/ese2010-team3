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
	private Date timestamp;

	/**
	 * Create an <code>Item</code>.
	 * 
	 * @param owner
	 *            the {@link User} who owns the <code>Item</code>
	 */
	public Item(User owner) {
		this.owner = owner;
		timestamp = SystemInformation.get().now();
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
