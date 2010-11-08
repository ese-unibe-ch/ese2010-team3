package models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import models.helpers.IDTable;

/**
 * A {@link Entry} containing an answer to a {@link Question}
 * 
 * @author Simon Marti
 * @author Mirco Kocher
 * 
 */
public class Answer extends Entry {

	private final Question question;
	private IDTable<Comment> comments;
	private final int id;

	/**
	 * Create an <code>Answer</code> to a {@link Question}.
	 * 
	 * @param id
	 * @param owner
	 *            the {@link User} who posted the <code>Answer</code>
	 * @param question
	 *            the {@link Question} this <code>Answer</code> belongs to
	 * @param content
	 *            the answer
	 */
	public Answer(int id, User owner, Question question, String content) {
		super(owner, content);
		this.question = question;
		comments = new IDTable<Comment>();
		this.id = id;

		// make users aware of this new answer
		question.notifyObservers(this);
	}

	/**
	 * Post a {@link Comment} to a <code>Answer</code>.
	 * 
	 * @param user
	 *            the {@link User} posting the {@link Comment}
	 * @param content
	 *            the comment
	 * @return an {@link Comment}
	 */
	public Comment comment(User user, String content) {
		Comment comment = new Comment(comments.nextID(), user, this, content);
		comments.add(comment);
		return comment;
	}

	/**
	 * Unregisters all {@link Vote}s, {@link Comments} and itself.
	 */
	@Override
	public void unregister() {
		for (Comment comment : comments) {
			comment.unregister();
		}
		comments = new IDTable<Comment>();

		question.unregister(this);
		unregisterVotes();
		unregisterUser();
	}

	/**
	 * Get the {@link Question} belonging to the <code>Answer</code>.
	 * 
	 * @return the {@link Question} this <code>Answer</code> belongs to
	 */

	public Question getQuestion() {
		// if this answer has been removed from its question, no longer
		// claim to belong to a question
		if (!question.hasAnswer(this))
			return null;
		return question;
	}

	/**
	 * Unregisters a deleted {@link Comment}.
	 * 
	 * @param comment
	 *            the {@link Comment} to unregister
	 */
	@Override
	public void unregister(Comment comment) {
		comments.remove(comment.id());
	}

	/**
	 * Checks if a {@link Comment} belongs to an <code>Answer</code>.
	 * 
	 * @param comment
	 *            the {@link Comment} to check
	 * @return true if the {@link Comment} belongs to the <code>Answer</code>
	 */
	public boolean hasComment(Comment comment) {
		return comments.contains(comment);
	}

	/**
	 * Get all {@link Comment}s to an <code>Answer</code>.
	 * 
	 * @return {@link Collection} of {@link Comments}
	 */
	public List<Comment> comments() {
		List<Comment> list = new ArrayList<Comment>(comments.values());
		Collections.sort(list);
		return Collections.unmodifiableList(list);
	}

	/**
	 * Get a specific {@link Comment} to an <code>Answer</code>.
	 * 
	 * @param id
	 *            of the <code>Comment</code>
	 * @return {@link Comment} or null
	 */
	public Comment getComment(int id) {
		return comments.get(id);
	}

	/**
	 * Id.
	 * 
	 * @return the id of the <code>Comment</code>
	 */
	public int id() {
		return id;
	}

	/**
	 * Get the best <code>Answer</code> to a <code>Question</code>
	 * 
	 * @return the best answer of a <code>Question</code>
	 */
	public boolean isBestAnswer() {
		return question.getBestAnswer() == this;
	}

	/**
	 * 
	 * Compares this <code>Answer</code> with another one with respect to their
	 * ratings and their Best-answer state.
	 * 
	 * @return comparison result (-1 = this Answer has more upVotes or is best)
	 */
	@Override
	public int compareTo(Entry other) {
		if (!(other instanceof Answer)
				|| isBestAnswer() == ((Answer) other).isBestAnswer())
			return super.compareTo(other);
		if (isBestAnswer())
			return -1;
		return 1;
	}

	/**
	 * Checks whether the answer is high-rated or not.
	 * 
	 * @return boolean whether the answer is high rated or not
	 */
	public boolean isHighRated() {
		return (rating() >= 5);
	}

}
