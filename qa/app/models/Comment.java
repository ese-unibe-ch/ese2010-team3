package models;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * 
 * An {@link Entry} containing a comment as its content.
 * 
 * @author Felix Langenegger
 * @author Tobias Brog (Review)
 * 
 */

public class Comment extends Entry {

	/** The entry. */
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
	 * @see models.Entry#unregister()
	 */
	@Override
	public void unregister() {
		this.entry.unregister(this);
		unregisterUser();
		this.entry = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see models.Entry#unregister(models.Comment)
	 */
	@Override
	/**
	 * Just a stub. Should be removed with refactoring
	 */
	public void unregister(Comment comment) {
		throw new IllegalArgumentException("There are no nested comments!");
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
		ArrayList<User> likers = new ArrayList<User>();
		likers.addAll(this.likers);
		return likers;
	}
	
	/**
	 * Clears the list of users, who like this comment.
	 */
	public void clearAllLikers() {
		likers.clear();
	}

}
