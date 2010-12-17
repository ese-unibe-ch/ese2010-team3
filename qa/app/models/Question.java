package models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import models.database.ITagDatabase;
import models.helpers.ICleanup;
import models.helpers.IObservable;
import models.helpers.IObserver;

/**
 * A {@link Entry} containing a question as <code>content</code>, and
 * {@link Answer}s (comments and votes are tracked by the superclass).
 * 
 * @author Simon Marti
 * @author Mirco Kocher
 */
public class Question extends Entry implements IObservable {

	private final HashMap<Integer, Answer> answers;
	private boolean isLocked = false;

	private Answer bestAnswer;
	private Date settingOfBestAnswer;
	private final ArrayList<Tag> tags = new ArrayList<Tag>();
	private final ITagDatabase tagDB;
	private final ICleanup<Question> cleaner;

	protected HashSet<IObserver> observers;

	/**
	 * Create a Question, store it in the database and have its owner notified
	 * about all answers (opt-out).
	 * 
	 * @param owner
	 *            the {@link User} who posted the <code>Question</code>
	 * @param content
	 *            the question
	 * @param tagDB
	 *            an optional tag database in which to store tags associated
	 *            with this question
	 * @param cleaner
	 *            an optional clean-up object that wants to be notified when
	 *            this question is no longer needed
	 */
	public Question(User owner, String content, ITagDatabase tagDB,
			ICleanup<Question> cleaner) {
		super(owner, content);
		this.answers = new HashMap<Integer, Answer>();
		this.observers = new HashSet<IObserver>();
		this.tagDB = tagDB;
		this.cleaner = cleaner;
		// all users watch their own questions by default
		if (owner != null)
			owner.startObserving(this);
	}

	/**
	 * Constructor for questions not registered in any kind of database (for
	 * testing only).
	 * 
	 * @param owner
	 *            the {@link User} who posted the <code>Question</code>
	 * @param content
	 *            the question
	 */
	public Question(User owner, String content) {
		this(owner, content, null, null);
	}

	/**
	 * Unregisters all {@link Answer}s, {@link Tag}s and itself.
	 */
	@Override
	public void unregister() {
		for (Answer answer : new ArrayList<Answer>(this.answers.values())) {
			answer.unregister();
		}
		this.observers.clear();
		setTagString("");
		if (this.cleaner != null) {
			this.cleaner.cleanUp(this);
		}
		super.unregister();
	}

	/**
	 * Unregisters a deleted {@link Answer}.
	 * 
	 * @param answer
	 *            the {@link Answer} to unregister
	 */
	public void unregister(Answer answer) {
		this.answers.remove(answer.id());
	}

	/**
	 * Post a {@link Answer} to a <code>Question</code>.
	 * 
	 * @param user
	 *            the {@link User} posting the {@link Answer}
	 * @param content
	 *            the answer
	 * @return an {@link Answer}
	 */
	public Answer answer(User user, String content) {
		Answer answer = new Answer(user, this, content);
		this.answers.put(answer.id(), answer);
		// make users aware of this new answer
		this.notifyObservers(answer);
		return answer;
	}

	/**
	 * Checks if a {@link Answer} belongs to a <code>Question</code>.
	 * 
	 * @param answer
	 *            the {@link Answer} to check
	 * @return true if the {@link Answer} belongs to the <code>Question</code>
	 */
	public boolean hasAnswer(Answer answer) {
		return this.answers.containsValue(answer);
	}

	/**
	 * Get all {@link Answer}s to a <code>Question</code>.
	 * 
	 * @return {@link Collection} of {@link Answers}
	 */
	public List<Answer> answers() {
		List<Answer> list = new ArrayList<Answer>(this.answers.values());
		Collections.sort(list);
		return Collections.unmodifiableList(list);
	}

	/**
	 * Get a specific {@link Answer} to a <code>Question</code>.
	 * 
	 * @param id
	 *            of the <code>Answer</code>
	 * @return {@link Answer} or null
	 */
	public Answer getAnswer(int id) {
		return this.answers.get(id);
	}

	public boolean isBestAnswerSettable() {
		long thirtyMinutesAgo = SysInfo.now().getTime() - 30 * 60 * 1000;
		return this.settingOfBestAnswer == null
				|| thirtyMinutesAgo <= this.settingOfBestAnswer.getTime();
	}

	/**
	 * Sets the best answer. This answer can not be changed after 30min. This
	 * Method enforces this and fails if it can not be set.
	 * 
	 * @param bestAnswer
	 *            the answer the user chose to be the best for this question.
	 * @return true if setting of best answer was allowed.
	 */
	public boolean setBestAnswer(Answer bestAnswer) {
		if (!isBestAnswerSettable()) {
			return false;
		}
		this.bestAnswer = bestAnswer;
		this.settingOfBestAnswer = SysInfo.now();
		return true;
	}

	public boolean hasBestAnswer() {
		return this.bestAnswer != null;
	}

	public Answer getBestAnswer() {
		return this.bestAnswer;
	}

	/**
	 * Boolean whether a <code>Question</code> is locked or not. Locked
	 * questions
	 * cannot be answered or commented.
	 * 
	 * @return boolean whether the <code>Question</code> is locked or not
	 */
	public boolean isLocked() {
		return this.isLocked;
	}

	/**
	 * Sets a <code>Question</code> to the locked status. Locked questions
	 * cannot be answered or commented.
	 */
	public void lock() {
		this.isLocked = true;
	}

	/**
	 * Unlocks a <code>Question</code>.
	 */
	public void unlock() {
		this.isLocked = false;
	}

	/**
	 * @param tags
	 *            a comma- or whitespace-separated list of tags to be associated
	 *            with this question
	 */
	public void setTagString(String tags) {
		for (Tag tag : this.tags) {
			tag.unregister(this);
		}
		this.tags.clear();

		if (tags == null || tags.equals(""))
			return;

		String bits[] = tags.split("[\\s,]+");
		for (String bit : bits) {
			// make the tag conform to Tag.tagRegex
			bit = bit.toLowerCase();
			if (bit.length() > 32) {
				bit = bit.substring(0, 32);
			}

			Tag tag = this.tagDB.get(bit);
			if (tag != null && !this.tags.contains(tag)) {
				this.tags.add(tag);
				tag.register(this);
			}
		}
		Collections.sort(this.tags);
	}

	/*
	 * Get a List of all tags for a <code>Question</code>.
	 * 
	 * @return List of tags
	 */
	public List<Tag> getTags() {
		return (List<Tag>) this.tags.clone();
	}

	/**
	 * @see models.helpers.IObservable#addObserver(models.IObserver)
	 */
	public void addObserver(IObserver o) {
		if (o == null)
			throw new IllegalArgumentException();
		this.observers.add(o);
	}

	/**
	 * @see models.helpers.IObservable#hasObserver(models.IObserver)
	 */
	public boolean hasObserver(IObserver o) {
		return this.observers.contains(o);
	}

	/**
	 * @see models.helpers.IObservable#removeObserver(models.IObserver)
	 */
	public void removeObserver(IObserver o) {
		this.observers.remove(o);
	}

	/**
	 * @see models.helpers.IObservable#notifyObservers(java.lang.Object)
	 */
	public void notifyObservers(Object arg) {
		for (IObserver o : this.observers) {
			o.observe(this, arg);
		}
	}

	/**
	 * Calculates the age of this question in days.
	 * 
	 * @param now
	 *            a Date representing the current moment (usually SysInfo.now())
	 * @return this question's age in days
	 */
	public long getAgeInDays(Date now) {
		return (now.getTime() - this.timestamp().getTime())
				/ (1000 * 60 * 60 * 24);
	}

	public int countAnswers() {
		return this.answers.size();
	}
}
