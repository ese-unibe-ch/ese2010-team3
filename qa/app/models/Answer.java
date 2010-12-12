package models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * A {@link Entry} containing an answer to a {@link Question}
 * 
 * @author Simon Marti
 * @author Mirco Kocher
 * 
 */
public class Answer extends Entry {

	private final Question question;
	private HashMap<Integer, Comment> comments;

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
	public Answer(User owner, Question question, String content) {
		super(owner, content);
		this.question = question;
		this.comments = new HashMap<Integer, Comment>();

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
		Comment comment = new Comment(user, this, content);
		this.comments.put(comment.id(), comment);
		return comment;
	}

	/**
	 * Unregisters all {@link Vote}s, {@link Comments} and itself.
	 */
	@Override
	public void unregister() {
		for (Comment comment : this.comments.values()) {
			comment.unregister();
		}
		this.comments.clear();

		this.question.unregister(this);
		unregisterVotes();
		unregisterUser();
		super.unregister();
	}

	/**
	 * Get the {@link Question} belonging to the <code>Answer</code>.
	 * 
	 * @return the {@link Question} this <code>Answer</code> belongs to
	 */

	public Question getQuestion() {
		// if this answer has been removed from its question, no longer
		// claim to belong to a question
		if (!this.question.hasAnswer(this))
			return null;
		return this.question;
	}

	/**
	 * Unregisters a deleted {@link Comment}.
	 * 
	 * @param comment
	 *            the {@link Comment} to unregister
	 */
	@Override
	public void unregister(Comment comment) {
		this.comments.remove(comment.id());
	}

	/**
	 * Checks if a {@link Comment} belongs to an <code>Answer</code>.
	 * 
	 * @param comment
	 *            the {@link Comment} to check
	 * @return true if the {@link Comment} belongs to the <code>Answer</code>
	 */
	public boolean hasComment(Comment comment) {
		return this.comments.containsValue(comment);
	}

	/**
	 * Get all {@link Comment}s to an <code>Answer</code>.
	 * 
	 * @return {@link Collection} of {@link Comments}
	 */
	public List<Comment> comments() {
		List<Comment> list = new ArrayList<Comment>(this.comments.values());
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
		return this.comments.get(id);
	}

	/**
	 * Get the best <code>Answer</code> to a <code>Question</code>
	 * 
	 * @return the best answer of a <code>Question</code>
	 */
	public boolean isBestAnswer() {
		return this.question.getBestAnswer() == this;
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
