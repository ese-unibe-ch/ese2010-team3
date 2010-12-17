package models;

import java.util.HashSet;
import java.util.Set;

import models.helpers.ICleanup;

/**
 * A <code>Tag</code> can belong to several questions, allowing to associate
 * them thematically.
 * 
 * @author sbuenzli
 */
public class Tag implements Comparable<Tag> {

	/** The name of this tag (all lowercase and without whitespace). */
	private final String name;

	/** The questions associated with this tag. */
	private final HashSet<Question> questions = new HashSet<Question>();

	/** A cleaner to call once this tag isn't referenced by any questions. */
	private final ICleanup<Tag> cleaner;

	/** A regex a valid tag name has to match. */
	private final String tagRegex = "^[^A-Z\\s]{1,32}$";

	/**
	 * Instantiates a new Tag.
	 * 
	 * @param name
	 *            the name of this tag (should be all lowercase and not contain
	 *            whitespace)
	 * @param cleaner
	 *            an optional clean-up object that wants to be notified when
	 *            this tag is no longer needed
	 */
	public Tag(String name, ICleanup<Tag> cleaner) {
		if (name == null || !name.matches(this.tagRegex))
			throw new IllegalArgumentException();
		this.name = name;
		this.cleaner = cleaner;
	}

	/**
	 * @return the name of this Tag.
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @return a list of all the questions associated with this tag.
	 */
	public Set<Question> getQuestions() {
		return (Set<Question>) this.questions.clone();
	}

	/**
	 * @param question
	 *            the question to associate with this Tag.
	 */
	public void register(Question question) {
		this.questions.add(question);
	}

	/**
	 * @param question
	 *            the question to de-associate from this Tag.
	 */
	public void unregister(Question question) {
		this.questions.remove(question);

		// remove this tag from the database
		if (this.questions.isEmpty() && this.cleaner != null) {
			this.cleaner.cleanUp(this);
		}
	}

	public int compareTo(Tag other) {
		return this.name.compareTo(other.name);
	}

	@Override
	public String toString() {
		return "Tag(" + this.name + ")";
	}
}
