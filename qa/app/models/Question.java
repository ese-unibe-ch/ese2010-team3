package models;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import models.database.Database;
import models.database.HotDatabase.HotQuestionDatabase;
import models.helpers.Pair;
import models.helpers.Mapper;

/**
 * A {@link Entry} containing a question as <code>content</code>, {@link Answer}
 * s and {@link Comments}.
 * 
 * @author Simon Marti
 * @author Mirco Kocher
 * 
 */
public class Question extends Entry implements IObservable {

	private IDTable<Answer> answers;
	private IDTable<Comment> comments;
	private final int id;

	private Answer bestAnswer;
	private Calendar settingOfBestAnswer;
	private final ArrayList<Tag> tags = new ArrayList<Tag>();
	
	protected HashSet<IObserver> observers;

	/**
	 * Create a Question.
	 * 
	 * @param owner
	 *            the {@link User} who posted the <code>Question</code>
	 * @param content
	 *            the question
	 */
	public Question(User owner, String content) {
		super(owner, content);
		this.answers = new IDTable<Answer>();
		this.comments = new IDTable<Comment>();
		this.observers = new HashSet<IObserver>();
		this.id = Database.get().questions().register(this);
	}

	/**
	 * Unregisters all {@link Answer}s, {@link Comment}s, {@link Vote}s,
	 * {@link Tag}s and itself.
	 */
	@Override
	public void unregister() {
		Collection<Answer> answers = this.answers.values();
		Collection<Comment> comments = this.comments.values();
		this.answers = new IDTable<Answer>();
		this.comments = new IDTable<Comment>();
		for (Answer answer : answers)
			answer.unregister();
		for (Comment comment : comments)
			comment.unregister();
		this.observers.clear();
		if (this.id != -1)
			Database.get().questions().remove(this.id);
		this.unregisterVotes();
		this.unregisterUser();
		this.setTagString("");
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
	 * Post a {@link Answer} to a <code>Question</code>
	 * 
	 * @param user
	 *            the {@link User} posting the {@link Answer}
	 * @param content
	 *            the answer
	 * @return an {@link Answer}
	 */
	public Answer answer(User user, String content) {
		Answer answer = new Answer(this.answers.nextID(), user, this, content);
		this.answers.add(answer);
		return answer;
	}

	/**
	 * Post a {@link Comment} to a <code>Question</code>
	 * 
	 * @param user
	 *            the {@link User} posting the {@link Comment}
	 * @param content
	 *            the comment
	 * @return an {@link Comment}
	 */
	public Comment comment(User user, String content) {
		Comment comment = new Comment(this.comments.nextID(), user, this,
				content);
		this.comments.add(comment);
		return comment;
	}

	/**
	 * Checks if a {@link Answer} belongs to a <code>Question</code>
	 * 
	 * @param answer
	 *            the {@link Answer} to check
	 * @return true if the {@link Answer} belongs to the <code>Question</code>
	 */
	public boolean hasAnswer(Answer answer) {
		return this.answers.contains(answer);
	}

	/**
	 * Checks if a {@link Comment} belongs to a <code>Question</code>
	 * 
	 * @param comment
	 *            the {@link Comment} to check
	 * @return true if the {@link Comment} belongs to the <code>Question</code>
	 */
	public boolean hasComment(Comment comment) {
		return this.comments.contains(comment);
	}

	/**
	 * Get the <code>id</code> of the <code>Question</code>. The <code>id</code>
	 * does never change.
	 * 
	 * @return id of the <code>Question</code>
	 */
	public int id() {
		return this.id;
	}

	/**
	 * Get all {@link Answer}s to a <code>Question</code>
	 * 
	 * @return {@link Collection} of {@link Answers}
	 */
	public List<Answer> answers() {
		List<Answer> list = new ArrayList<Answer>(answers.values());
		Collections.sort(list);
		return Collections.unmodifiableList(list);
	}

	/**
	 * Get all {@link Comment}s to a <code>Question</code>
	 * 
	 * @return {@link Collection} of {@link Comments}
	 */
	public List<Comment> comments() {
		List<Comment> list = new ArrayList<Comment>(comments.values());
		Collections.sort(list);
		return Collections.unmodifiableList(list);
	}

	/**
	 * Get a specific {@link Answer} to a <code>Question</code>
	 * 
	 * @param id
	 *            of the <code>Answer</code>
	 * @return {@link Answer} or null
	 */
	public Answer getAnswer(int id) {
		return this.answers.get(id);
	}

	/**
	 * Get a specific {@link Comment} to a <code>Question</code>
	 * 
	 * @param id
	 *            of the <code>Comment</code>
	 * @return {@link Comment} or null
	 */
	public Comment getComment(int id) {
		return this.comments.get(id);
	}

	public boolean isBestAnswerSettable(Calendar now) {
		Calendar thirtyMinutesAgo = ((Calendar) now.clone());
		thirtyMinutesAgo.add(Calendar.MINUTE, -30);
		return this.settingOfBestAnswer == null
				|| !thirtyMinutesAgo.getTime().after(
						this.settingOfBestAnswer.getTime());
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
		Calendar now = Calendar.getInstance();
		return setBestAnswer(bestAnswer, now);
	}

	public boolean setBestAnswer(Answer bestAnswer, Calendar now) {
		if (this.isBestAnswerSettable(now)) {
			this.bestAnswer = bestAnswer;
			this.settingOfBestAnswer = now;
			return true;
		} else
			return false;
	}

	public boolean hasBestAnswer() {
		return bestAnswer != null;
	}
	
	public Answer getBestAnswer() {
		return bestAnswer;
	}

	/**
	 * @param tags
	 *            a comma- or whitespace-separated list of tags to be associated
	 *            with this question
	 */
	public void setTagString(String tags) {
		for (Tag tag : this.tags)
			tag.unregister(this);
		this.tags.clear();

		if (tags == null)
			return;

		String bits[] = tags.split("[\\s,]+");
		for (String bit : bits) {
			// make the tag conform to Tag.tagRegex
			bit = bit.toLowerCase();
			if (bit.length() > 32)
				bit = bit.substring(0, 32);

			Tag tag = Tag.get(bit);
			if (tag != null && !this.tags.contains(tag)) {
				this.tags.add(tag);
				tag.register(this);
			}
		}
		Collections.sort(this.tags);
	}

	public ArrayList<Tag> getTags() {
		return (ArrayList<Tag>) this.tags.clone();
	}
	/**
	 * @see models.IObservable#addObserver(models.IObserver)
	 */
	public void addObserver(IObserver o) {
		if (o == null)
			throw new IllegalArgumentException();
		this.observers.add(o);
	}

	/**
	 * @see models.IObservable#hasObserver(models.IObserver)
	 */
	public boolean hasObserver(IObserver o) {
		return this.observers.contains(o);
	}

	/**
	 * @see models.IObservable#removeObserver(models.IObserver)
	 */
	public void removeObserver(IObserver o) {
		this.observers.remove(o);
	}

	/**
	 * @see models.IObservable#notifyObservers(java.lang.Object)
	 */
	public void notifyObservers(Object arg) {
		for (IObserver o : this.observers)
			o.observe(this, arg);
	}

	/**
	 * Sorts an ArrayList in descending order of questions by comparing the
	 * ratios of matching tags and the overall number of tags per question. <br>
	 * Calculation:<br>
	 * 
	 * (CountOfMatches / SizeOfTagsArrayQuestionOne) * (CountOfMatches /
	 * SizeOfTagsArrayQuestionTwo)
	 * 
	 * @param q
	 *            the ArrayList of questions to be sorted
	 * @return ArrayList<Question> the sorted ArrayList
	 */
	private ArrayList<Question> sortQuestionsByMatchRatio(ArrayList<Question> q) {
		ArrayList<Question> questions = q;
		ArrayList<Question> sorted;
		int matchCount;
		Map<Question, Double> map = new HashMap<Question, Double>();
		for (Question qu : questions) {
			List<Tag> tags = this.getTags();
			tags.retainAll(qu.getTags());
			matchCount = tags.size();
			double questionOneRatio = ((double) matchCount / (double) this
					.getTags().size());
			double questionTwoRatio = ((double) matchCount / (double) qu
					.getTags().size());
			double ratio = questionOneRatio * questionTwoRatio;
			map.put(qu, ratio);
		}
		sorted = new ArrayList<Question>(this.sortMapByValue(map));
		Collections.reverse(sorted);

		return sorted;
	}

	/**
	 * Sorts a map by comparing the values and returns a set of the
	 * corresponding keys <br>
	 * 
	 * @param map
	 *            the map to be sorted
	 * @return Set the set of the keys
	 */
	public static Set sortMapByValue(Map map) {
		List list = new LinkedList(map.entrySet());
		Collections.sort(list, new Comparator() {
			public int compare(Object o1, Object o2) {
				return ((Comparable) ((Map.Entry) (o1)).getValue())
						.compareTo(((Map.Entry) (o2)).getValue());
			}
		});
		Map result = new LinkedHashMap();
		for (Iterator it = list.iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			result.put(entry.getKey(), entry.getValue());
		}
		return result.keySet();
	}

	// From <a
	// href=http://www.programmersheaven.com/download/49349/download.aspx
	// 01.11.2010

	/**
	 * Get all questions that containing at least one of the tags of the
	 * original question.
	 * 
	 * @return ArrayList<Question> the ArrayList containing all questions that
	 *         contain at least one of the first question.
	 */
	public ArrayList<Question> getSimilarQuestions() {
		ArrayList<Question> questions = new ArrayList<Question>();
		for (Tag t : this.getTags()) {
			for (Question q : t.getQuestions()) {
				if (!questions.contains(q) && !q.equals(this)) {
					questions.add(q);
				}
			}
		}
		questions = this.sortQuestionsByMatchRatio(questions);
		return questions;

	}

	public int countAnswers() {
		return answers.size();
	}
}
