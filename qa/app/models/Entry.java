package models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import models.helpers.ICleanup;
import models.helpers.Tools;

/**
 * An {@link Item} which has a content, can be commented and can be voted up and
 * down.
 * 
 * @author Simon Marti
 * @author Mirco Kocher
 */
public abstract class Entry extends Item implements Comparable<Entry>,
		ICleanup<Item> {

	private final String content;
	private String contentText, contentHtml;
	private final HashMap<Integer, Comment> comments;
	private final HashMap<User, Vote> votes;
	private final Set<Notification> notifications;
	private boolean possiblySpam;
	private int cachedRating;

	/**
	 * Create an <code>Entry</code> with Markdown or HTML content. The content
	 * will be sanitized before being used, the returned HTML should be safe to
	 * include without further processing in any web page.
	 * 
	 * @param owner
	 *            the {@link User} who owns the <code>Entry</code>
	 * @param content
	 *            the content of the <code>Entry</code> (Markdown/HTML)
	 */
	public Entry(User owner, String content) {
		super(owner);
		if (content == null) {
			content = "";
		}
		this.content = content;
		this.comments = new HashMap<Integer, Comment>();
		this.votes = new HashMap<User, Vote>();
		this.notifications = new HashSet<Notification>();
		this.cachedRating = 0;
		this.possiblySpam = false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see models.Item#delete()
	 */
	@Override
	public void delete() {
		for (Comment comment : new ArrayList<Comment>(this.comments.values())) {
			comment.delete();
		}
		for (Vote vote : new ArrayList<Vote>(this.votes.values())) {
			vote.delete();
		}
		for (Notification notification : new ArrayList<Notification>(
				this.notifications)) {
			notification.delete();
		}
		super.delete();
	}

	/**
	 * Called by the various <code>Item</code>s associated with this
	 * <code>Entry</code> when they're deleted so that the references to them
	 * kept by this <code>Entry</code> can be removed.
	 * 
	 * @see models.helpers.ICleanup#cleanUp(java.lang.Object)
	 */
	public void cleanUp(Item item) {
		if (item instanceof Comment) {
			this.comments.remove(item.id());
		} else if (item instanceof Vote) {
			this.votes.remove(item.owner());
			this.cachedRating -= ((Vote) item).up() ? 1 : -1;
		} else if (item instanceof Notification) {
			this.notifications.remove(item);
		}
	}

	/**
	 * Gets the content of an <code>Entry</code> as cleaned up HTML.
	 * 
	 * @return the HTML-content of the <code>Entry</code>
	 */
	public String content() {
		if (this.contentHtml == null)
			this.contentHtml = Tools.markdownToHtml(this.content);
		return this.contentHtml;
	}

	/**
	 * Gets this <code>Entry</code>'s content stripped of all HTML tags. Use
	 * this e.g. for searching.
	 * 
	 * @return the text extracted from this <code>Entry</code>'s content
	 */
	public String getContentText() {
		if (this.contentText == null)
			this.contentText = Tools.htmlToText(this.content());
		return this.contentText;
	}

	/**
	 * This is a comment-Factory method that creates a new {@link Comment} to
	 * this <code>Entry</code> and adds it to the <code>Entry</code>'s list of
	 * comments. To remove the {@link Comment}, call its delete method. The
	 * comment's content can be either Markdown or HTML, both of which will be
	 * converted to a safe subset of HTML.
	 * 
	 * @param user
	 *            the {@link User} posting the {@link Comment}
	 * @param content
	 *            the comment's content as Markdown or HTML
	 * @return the created {@link Comment}
	 */
	public Comment comment(User user, String content) {
		Comment comment = new Comment(user, this, content);
		this.comments.put(comment.id(), comment);
		return comment;
	}

	/**
	 * Checks if a {@link Comment} belongs to an <code>Entry</code>.
	 * 
	 * @param comment
	 *            the {@link Comment} to check
	 * @return true if the {@link Comment} belongs to the <code>Entry</code>
	 */
	public boolean hasComment(Comment comment) {
		return this.comments.containsValue(comment);
	}

	/**
	 * Get all {@link Comment}s to an <code>Entry</code> sorted by age (oldest
	 * first).
	 * 
	 * @return {@link Collection} of {@link Comments}
	 */
	public List<Comment> comments() {
		List<Comment> list = new ArrayList<Comment>(this.comments.values());
		Collections.sort(list);
		return list;
	}

	/**
	 * Get a specific {@link Comment} to an <code>Entry</code>.
	 * 
	 * @param id
	 *            of the <code>Comment</code>
	 * @return {@link Comment} or null
	 */
	public Comment getComment(int id) {
		return this.comments.get(id);
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

	public void registerNotification(Notification notification) {
		this.notifications.add(notification);
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
			oldVote.delete();
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
	 * Declare this post to be probably spam. Optionally sends a notice to the
	 * moderating staff to check if this really is spam.
	 * 
	 * @param moderatorMailbox
	 *            an optional reference to a mailbox to dump the spam
	 *            notification in
	 */
	public void markSpam(IMailbox moderatorMailbox) {
		if (this.owner().isSpammer()) {
			this.confirmSpam();
		} else if (!this.possiblySpam) {
			if (moderatorMailbox != null) {
				moderatorMailbox.notify(null, this);
			}
			this.possiblySpam = true;
		}
	}

	/**
	 * A moderator declares this to be definitively spam. It deletes the post
	 * and blocks the user that posted it in the first place.
	 */
	public void confirmSpam() {
		this.owner().setIsSpammer(true);
		this.delete();
	}

	/**
	 * Return whether this <code>Entry</code> has been marked as possibly being
	 * spam by a user but this status has not yet been confirmed by a moderator
	 * (in which case this <code>Entry</code> would already have been deleted).
	 * 
	 * @return true, if the <code>Entry</code> has been marked as being spam by
	 *         any user
	 */
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
