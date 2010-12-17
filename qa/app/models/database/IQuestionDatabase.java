package models.database;

import java.util.List;
import java.util.Map;

import models.Question;
import models.Tag;
import models.User;

/**
 * A question database is both a factory for creating new tags and a container
 * tracking all the created questions, also providing basic statistical data
 * about the tracked questions.
 * 
 * @author aaron
 */
public interface IQuestionDatabase {
	/**
	 * Get the <code>Question</code> with the given id.
	 * 
	 * @param id
	 *            unique identifier for a question.
	 * @return a <code>Question</code> or null if the given id doesn't exist.
	 */
	public Question get(int id);

	/**
	 * Get a {@link List} of all <code>Questions</code>.
	 * 
	 * @return all <code>Questions</code> in the database.
	 */
	public List<Question> all();

	/**
	 * Search the <code>Questions</code> in the DB for the term. This includes
	 * their content as well as their tags.
	 * 
	 * @param term
	 *            words to be searched. Only alphabetic characters are taken
	 *            into account.
	 * @return A list sorted descending by relevance.
	 */
	public List<Question> searchFor(String term);

	/**
	 * Adds a question to the DB.
	 * 
	 * @param owner
	 *            author of the question
	 * @param content
	 *            text of the question
	 * @return the fully set up question
	 */
	public Question add(User owner, String content);

	public int count();

	/**
	 * Number of answers voted to be the best in the DB.
	 * 
	 * @return int n >= 0
	 */

	public int countBestRatedAnswers();

	/**
	 * Number of answers a, that declare themselves to be
	 * <code>a.isHighRated()</code>
	 * 
	 * @return int n >= 0
	 */
	public int countHighRatedAnswers();

	/**
	 * Number of answers in the DB.
	 * 
	 * @return int n >= 0
	 */
	public int countAllAnswers();

	/**
	 * Returns a list of similar questions. This means, that they have at least
	 * one tag in common. Further, the returned list is sorted by relevance. <br/>
	 * It is anti-reflexive, ie <br/>
	 * <code>findSimilar(q).contains(q) == false</code>,<br/>
	 * symmetric, ie <br/>
	 * <code>findSimilar(q).contains(p)</code> iff
	 * <code>findSimilar(p).contains(q)</code><br/>
	 * but not necessarily transitive.
	 * 
	 * @param q
	 *            reference question
	 * @return a list of similar questions sorted desc by relevance.
	 */
	public List<Question> findSimilar(Question q);

	/**
	 * Get a list of all questions that the given user might also know to
	 * answer, calculated by similarity to questions the user has given the best
	 * answer to and by the recency and lack of answers of the questions to
	 * suggest.
	 * 
	 * @param user
	 *            the user to suggest questions for
	 * @return List<Question>
	 */
	public List<Question> suggestQuestions(User user);

	/**
	 * Collects for all tags the vote counts for all the users that have
	 * answered a question labeled with that tag.
	 * 
	 * @return a statistics map allowing to either determine the experts for a
	 *         given tag or the tags this user is an expert for
	 */
	public Map<Tag, Map<User, Integer>> collectExpertiseStatistics();

	/**
	 * Determines all the topics this user is an expert in. Each tag is
	 * considered a topic and a user is considered an expert if he's got a
	 * minimum of two positive votes (or one accepted best answer) to one of the
	 * questions with the tag and if his vote count is in the first quintile
	 * (i.e. at most 20 % of all the answerers for a given topic can be
	 * experts).
	 * 
	 * @param user
	 *            the user to determine the expertise for
	 * @return the list of tags for which this user is an expert
	 */
	public List<Tag> getExpertise(User user);

	/**
	 * Returns the list of all the questions the given user is currently
	 * observing.
	 * 
	 * @param user
	 *            the user who must be watching all the returned questions
	 * @return the user's watch list
	 */
	public List<Question> getWatchList(User user);

	/**
	 * Empties the Database.
	 */
	public void clear();
}
