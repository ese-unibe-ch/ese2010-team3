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
	 * @param owner the {@link User} who owns the <code>Item</code>
	 */
	public Item(User owner) {
		this.owner = owner;
		this.timestamp = SystemInformation.get().now();
		owner.registerItem(this);
	}

	/**
	 * Get the owner of an <code>Item</code>.
	 * 
	 * @return the owner
	 */
	public User owner() {
		return this.owner;
	}

	/**
	 * Get the time the <code>Item</code> was created.
	 * 
	 * @return the creation date as a UNIX timestamp
	 */
	public Date timestamp() {
		return this.timestamp;
	}

	/**
	 * Unregisters the <code>Item</code> if it gets deleted.
	 */
	public void unregister() {
		this.unregisterUser();
	}

	/**
	 * Unregisters the <code>Item</code> to it's owner.
	 */
	protected void unregisterUser() {
		this.owner.unregister(this);
		this.owner = null;
	}
}
