package models.database;

import java.util.List;
import java.util.Map;

import models.Question;
import models.Tag;
import models.User;

/**
 * Interaction with the collection of <code>Questions</code> on the server.
 * 
 * @author aaron
 * 
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
	 * Deletes the Question without cleaning up. Does nothing if id doesn't
	 * exist.
	 * 
	 * @param id
	 *            unique identifier.
	 */
	public void remove(int id);

	/**
	 * Adds the Question and returns the assigned id.
	 * 
	 * <code>get(register(q)) == q</code>
	 * 
	 * @return unique identifier for the question.
	 */
	public int register(Question q);

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
	 * Collects for all tags the vote counts for all the users that have
	 * answered a question labeled with that tag.
	 * 
	 * @return a statistics map allowing to either determine the experts for a
	 *         given tag or the tags this user is an expert for
	 */
	public Map<Tag, Map<User, Integer>> collectExpertiseStatistics();

	/**
	 * Empties the Database.
	 */
	public void clear();
}
