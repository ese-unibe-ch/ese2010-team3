package models.database;

import java.util.Collection;
import java.util.List;

import models.Question;
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
	 * @return a <code>Question</code> or null if the given id doesn't exist.
	 */
	public Question get(int id); 
	
	/**
	 * Get a <@link Collection} of all <code>Questions</code>.
	 * 
	 * @return all <code>Questions</code>
	 */
	public List<Question> all();
	
	/**
	 * Search the DB for the term. 
	 * @param term words to be searched. Only alphabetic characters are taken into account.
	 * @return A list sorted descending by relevance.
	 */
	public List<Question> searchFor(String term);

	/**
	 * Deletes the Question without cleaning up.
	 * @param id
	 */
	public void remove(int id);
	
	/**
	 * Adds the Question and returns the assigned id.
	 */
	public int register(Question q);
	
	/**
	 * Adds a question to the DB.
	 * @param owner 
	 * @param content
	 * @return the fully set up question
	 */
	public Question add(User owner, String content);
}
