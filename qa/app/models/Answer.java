package models;

/**
 * A {@link Entry} containing an answer to a {@link Question}
 * 
 * @author Simon Marti
 * @author Mirco Kocher
 * 
 */
public class Answer extends Entry {

	private final Question question;

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
	}

	/**
	 * Unregisters the associated question and itself.
	 */
	@Override
	public void delete() {
		this.question.cleanUp(this);
		super.delete();
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
	 * Returns whether the <code>Answer</code> has been selected as the best
	 * <code>Answer</code> for the <code>Question</code>.
	 * 
	 * There can only be one best answer per question.
	 * 
	 * @see Question#setBestAnswer(Answer)
	 * 
	 * @return whether this is the best <code>Answer</code>
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
