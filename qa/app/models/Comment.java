package models;

/**
 * 
 * An {@link Entry} containing a comment as its content.
 * 
 * @author Felix Langenegger
 * @author Tobias Brog (Review)
<<<<<<< HEAD
 *
 */

public class Comment extends Entry {
	
	private int id;
	private Entry entry;

	public Comment(int id, User owner, Entry entry, String content) {
		super(owner, content);
		this.entry = entry;
		this.id = id;
	}

	@Override
	public String type() {
		return "Comment";
	}

	@Override
	public void unregister() {
		this.entry.unregister(this);
		this.unregisterUser();
	}

	public int id() {
		return this.id;
	}

	@Override
	/**
	 * Just a stub. Should be removed with refactoring
	 */
	public void unregister(Comment comment) {
		// TODO Auto-generated method stub
	}

}
