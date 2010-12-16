package models;

import java.util.ArrayList;
import java.util.HashMap;

import models.database.Database;
import models.helpers.Tools;

/**
 * An {@link Item} which has a content and can be voted up and down.
 * 
 * @author Simon Marti
 * @author Mirco Kocher
 */
public abstract class Entry extends Item implements Comparable<Entry> {

	private final String content;
	private String contentText, contentHtml;
	private final HashMap<User, Vote> votes;
	private boolean possiblySpam;
	private int cachedRating;

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
		if (content == null) {
			content = "";
		}
		this.content = content;
		this.votes = new HashMap<User, Vote>();
		this.cachedRating = 0;
		this.possiblySpam = false;
	}

	/**
	 * Unregisters a deleted {@link Comment} to its {@link Entry}.
	 * 
	 * @param comment
	 *            the <code> Comment </code> to be unregistered
	 */
	public abstract void unregister(Comment comment);

	/**
	 * Delete all {@link Vote}s if the <code>Entry</code> gets deleted.
	 */
	protected void unregisterVotes() {
		for (Vote vote : new ArrayList<Vote>(this.votes.values())) {
			vote.unregister();
		}
	}

	/**
	 * Unregisters a deleted {@link Vote}.
	 * 
	 * @param vote
	 *            the {@link Vote} to unregister
	 */
	public void unregister(Vote vote) {
		this.votes.remove(vote.owner());
		this.cachedRating -= vote.up() ? 1 : -1;
	}

	/**
	 * 
	 * Get the content of an <code>Entry</code>.
	 * 
	 * @return the content of the <code>Entry</code>
	 */
	public String content() {
		if (this.contentHtml == null)
			this.contentHtml = Tools.markdownToHtml(this.content);
		return this.contentHtml;
	}

	public String getContentText() {
		if (this.contentText == null)
			this.contentText = Tools.htmlToText(this.content());
		return this.contentText;
	}

	/**
	 * Count all positive {@link Vote}s on an <code>Entry</code>.
	 * 
	 * @return number of positive {@link Vote}s
	 */
	public int upVotes() {
		return this.countVotes(true);
	}

	/**
	 * Count all negative {@link Vote}s on an <code>Entry</code>.
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
		return this.cachedRating;
	}

	/**
	 * Compares this <code>Entry</code> with another one with respect to their
	 * ratings (or their age, if they've got identical ratings).
	 * 
	 * @return comparison result (-1 = this Entry has more upVotes)
	 */
	public int compareTo(Entry e) {
		int diff = e.rating() - this.rating();
		if (diff == 0)
			// compare by ID instead of - potentially identical - timestamp
			// for a guaranteed stable sorting (makes testing easier)
			return this.id() - e.id();
		return diff;
	}

	/**
	 * Counts the number of <code>Votes</code> of an <code>Entry</code>.
	 * 
	 * @param up
	 *            boolean whether there is a <code>Vote</code> to this
	 *            <code>Entry</code> or not
	 * @return counter number of <code>Votes</code>
	 */
	private int countVotes(boolean up) {
		int counter = 0;
		for (Vote vote : this.votes.values())
			if (vote.up() == up) {
				counter++;
			}
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

	/**
	 * Cancel a vote for an <code>Entry</code> (if there was one).
	 * 
	 * @param user
	 *            the {@link User} who voted
	 * @return the {@link Vote} that was removed (or <code>null</code>)
	 */
	public Vote voteCancel(User user) {
		if (this.hasVote(user)) {
			Vote oldVote = this.votes.get(user);
			oldVote.unregister();
		}
		return this.votes.remove(user);
	}

	/**
	 * Checks for an up-vote for a specific user
	 * 
	 * @param user
	 *            the {@link User} to check for
	 * @return true, if the given user has indeed voted for this
	 *         <code>Entry</code>
	 */
	public boolean hasUpVote(User user) {
		return this.hasVote(user) && this.votes.get(user).up();
	}

	/**
	 * Checks for a down-vote for a specific user
	 * 
	 * @param user
	 *            the {@link User} to check for
	 * @return true, if the given user has indeed voted for this
	 *         <code>Entry</code>
	 */
	public boolean hasDownVote(User user) {
		return this.hasVote(user) && !this.votes.get(user).up();
	}

	/**
	 * Checks for vote for a specific user
	 * 
	 * @param user
	 *            the {@link User} to check for
	 * @return true, if the given user has indeed voted for this
	 *         <code>Entry</code>
	 */
	private boolean hasVote(User user) {
		return this.votes.containsKey(user);
	}

	/**
	 * Let an <code>User</code> vote for an <code>Entry</code>.
	 * 
	 * @param user
	 *            who is voting
	 * @return vote of the <code>User</code>
	 */
	private Vote vote(User user, boolean up) {
		if (user == this.owner())
			return null;
		this.voteCancel(user);

		Vote vote = new Vote(user, this, up);
		this.votes.put(user, vote);
		this.cachedRating += up ? 1 : -1;
		return vote;
	}

	/**
	 * Turns this Entry into an anonymous (user-less) one.
	 */
	public void anonymize() {
		this.unregisterUser();
	}

	/**
	 * Produces a one-line summary of an Entry: the first 75 to 85 characters,
	 * if possible cut off at a word boundary, and an ellipsis, if the content
	 * is longer.
	 * 
	 * @return a one-line summary of an <code>Entry</code>.
	 * */
	public String summary() {
		return this.getContentText().replaceAll("\\s+", " ")
				.replaceFirst("^(.{75}\\S{0,9} ?).{5,}", "$1...");
	}

	/**
	 * Declare this post to be probably spam. Sends a notice to the moderating
	 * staff to check if this really is spam.
	 */
	public void markSpam() {
		if (this.owner().isSpammer()) {
			this.confirmSpam();
		} else if (!this.possiblySpam) {
			new Notification(Database.users()
					.getModeratorMailbox(), this);
			this.possiblySpam = true;
		}
	}

	/**
	 * A moderator declares this to be definitively spam. It deletes the post
	 * and blocks the user that posted it in the first place.
	 */
	public void confirmSpam() {
		this.owner().block("Declared Spammer");
		this.owner().setIsSpammer(true);
		this.unregister();
	}

	public boolean isPossiblySpam() {
		return this.possiblySpam;
	}

	@Override
	public String toString() {
		String className = this.getClass().getName();
		className = className.substring(className.lastIndexOf(".") + 1);
		return className + "(" + this.summary() + ")";
	}
}
