package models;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

/**
 * An {@link Item} which has a content and can be voted up and down.
 * 
 * @author Simon Marti
 * @author Mirco Kocher
 */
public abstract class Entry extends Item implements Comparable {

	private String content;
	private HashMap<String, Vote> votes;

	/**
	 * Create an <code>Entry</code>.
	 * 
	 * @param owner
	 *            the {@link User} who owns the <code>Entry</code>
	 * @param content
	 *            the content of the <code>Entry</code>
	 */
	public Entry(User owner, String content) {
		super(owner);
		this.content = content;
		this.votes = new HashMap();
	}

	/**
	 * Unregisters a deleted {@link Comment} to its {@link Entry}
	 * @param comment the <code> Comment </code> to be unregistered.
	 */
	public abstract void unregister(Comment comment);
	
	/**
	 * Unregisters the <code>Entry</code> if it gets deleted.
	 */
	@Override
	public void unregister() {
		this.unregisterVotes();
		this.unregisterUser();
	}

	/**
	 * Delete all {@link Vote}s if the <code>Entry</code> gets deleted.
	 */
	protected void unregisterVotes() {
		Collection<Vote> votes = this.votes.values();
		this.votes = new HashMap();
		for (Vote vote : votes)
			vote.unregister();
	}

	/**
	 * Unregisters a deleted {@link Vote}.
	 * 
	 * @param vote
	 *            the {@link Vote} to unregister
	 */
	public void unregister(Vote vote) {
		this.votes.remove(vote.owner().name());
	}

	/**

	 * Get the content of an <code>Entry</code>.
	 * 
	 * @return the content of the <code>Entry</code>
	 */
	public String content() {
		return this.content;
	}

	/**
	 * Count all positive {@link Vote}s on an <code>Entry</code>
	 * 
	 * @return number of positive {@link Vote}s
	 */
	public int upVotes() {
		return this.countVotes(true);
	}

	/**
	 * Count all negative {@link Vote}s on an <code>Entry</code>
	 * 
	 * @return number of negative {@link Vote}s
	 */
	public int downVotes() {
		return this.countVotes(false);
	}

	/**
	 * Get the current rating of the <code>Entry</code>.
	 * 
	 * @return rating as an <code>Integer</code>
	 */
	public int rating() {
		return this.upVotes() - this.downVotes();
	}

	/**
	 * Compares this <code>Entry</code> with another one with respect to their
	 * ratings.
	 * 
	 * @return comparison result (-1 = this Entry has more upVotes)
	 */
	public int compareTo(Object o) {
		return ((Entry) o).rating() - this.rating();
	}

	private int countVotes(boolean up) {
		int counter = 0;
		for (Vote vote : this.votes.values())
			if (vote.up() == up)
				counter++;
		return counter;
	}

	/**
	 * Vote an <code>Entry</code> up.
	 * 
	 * @param user
	 *            the {@link User} who voted
	 * @return the {@link Vote}
	 */
	public Vote voteUp(User user) {
		return this.vote(user, true);
	}

	/**
	 * Vote an <code>Entry</code> down.
	 * 
	 * @param user
	 *            the {@link User} who voted
	 * @return the {@link Vote}
	 */
	public Vote voteDown(User user) {
		return this.vote(user, false);
	}

	private Vote vote(User user, boolean up) {
		if (user == this.owner())
			return null;
		if (this.votes.containsKey(user.name()))
			this.votes.get(user.name()).unregister();
		Vote vote = new Vote(user, this, up);
		this.votes.put(user.name(), vote);
		return vote;
	}
	
	 /**
	  * Turns this Entry into an anonymous (user-less) one 
	  */
	 public void anonymize() {
		 this.unregisterUser();
	 }
	 
	 /**
	  * @return a one-line summary of an <code>Entry</code>.
	  * */
	 public String summary() {
		 if (this.content.length() <= 20)
			 return this.content.replaceAll("[\r\n]+", " ");
		 return this.content.substring(0, 20).replaceAll("[\r\n]+", " ") + "...";
	 }

	/**
	 * Get all <code>Votes</code>
	 * 
	 * @return votes
	 */
	public Collection<Vote> getVotes() {
		return votes.values();
	}

}
