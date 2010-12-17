package models;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * An {@link Entry} containing a comment as its content. Comments can be given
 * to all kinds of entries, although commenting comments is currently not
 * supported by controllers and view.
 * 
 * @author Felix Langenegger
 * @author Tobias Brog (Review)
 */

public class Comment extends Entry {

	/** The entry this comment is about. */
	private Entry entry;
	
	/** A set of Users who like this Comment */
	private final HashSet<User> likers;

	public Comment(User owner, Entry entry, String content) {
		super(owner, content);
		this.entry = entry;
		likers = new HashSet();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see models.Entry#delete()
	 */
	@Override
	public void delete() {
		this.entry.cleanUp(this);
		unregisterUser();
		this.entry = null;
	}

	/**
	 * Returns the <code>Question</code> the <code>Comment</code> belongs to
	 * directly (<code>Comment</code> to a <code>Question</code>) or indirectly
	 * (<code>Comment</code> to an <Code>Answer</code>).
	 * 
	 * @return Entry the comment belongs to
	 */
	public Question getQuestion() {
		Entry entry = this.entry;
		if (entry instanceof Answer)
			return ((Answer) entry).getQuestion();
		return (Question) entry;
	}
	
	/**
	 * Returns the count of Users, who like this comment.
	 * 
	 * @return integer of likers
	 */
	public int countLikers() {
		return this.likers.size();
	}
	
	/**
	 * Adds a user to the List of Users, who like this comment.
	 * 
	 * @param liker - user which is added to the likers list.
	 * 
	 * @return true (as specified by Collection.add(E))
	 */
	public boolean addLiker(User liker) {
		return this.likers.add(liker);
	}
	
	/**
	 * Removes a user from the list of users, who like this comment.
	 * 
	 * @param disliker - user which is removed from the likers list.
	 * 
	 * @return true (as specified by Collection.add(E))
	 */
	public boolean removeLiker(User disliker) {
		return this.likers.remove(disliker);
	}
	
	/**
	 * Gets all Users, who like this comment.
	 * 
	 * @return An ArrayList of Users.
	 */
	public ArrayList<User> getLikers() {
		return new ArrayList<User>(this.likers);
	}
	
	/**
	 * Clears the list of users, who like this comment.
	 */
	public void clearAllLikers() {
		likers.clear();
	}

}
