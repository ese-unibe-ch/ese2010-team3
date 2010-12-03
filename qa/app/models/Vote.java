package models;

/**
 * A vote on a {@link Entry} belonging to a {@link User}. The <code>Vote</code>
 * can be positive or negative.
 * 
 * @author Simon Marti
 * @author Mirco Kocher
 * 
 */
public class Vote extends Item {

	private final boolean up;
	private final Entry entry;

	/**
	 * Create a <code>Vote</code>.
	 * 
	 * @param owner
	 *            the {@link User} who created the <code>Vote</code>
	 * @param entry
	 *            the {@link Entry} the <code>Vote</code> belongs to.
	 * @param up
	 *            true if the <code>Vote</code> is positive
	 */
	public Vote(User owner, Entry entry, boolean up) {
		super(owner);
		this.up = up;
		this.entry = entry;
	}

	/**
	 * Unregister the <code>Vote</code> to both it's owners, the {@link User}
	 * and the {@link Entry}.
	 */
	@Override
	public void unregister() {
		this.entry.unregister(this);
		unregisterUser();
	}

	/**
	 * Check if a <code>Vote</code> is positive or negative.
	 * 
	 * @return true if the <code>Vote</code> is positive
	 */
	public boolean up() {
		return this.up;
	}

	/**
	 * Returns the entry this vote is for.
	 * 
	 * @return a question or answer that's been voted for
	 */
	public Entry getEntry() {
		return this.entry;
	}
}
