package models.database;

import java.util.Collection;

import models.Tag;

public interface ITagDatabase {
	
	/**
	 * All tags in the system. This means that if <code>all().contains(tag)</code>
	 * then <code>tag.getQuestions().isEmpty() == false</code>, and if there is a question q
	 * <code>q.getTags().contains(tag)</code> then <code>all().contains(tag)</code>.
	 * @return Collection of all tags referenced by at least one question.
	 */
	public Collection<Tag> all();
	
	/**
	 * Get the tag with this name. This ensures, that the name is present in the DB 
	 * and that there is only one
	 * @param name
	 * @return 
	 */

	public Tag get(String name);
	
	/**
	 * Removes a Tag from the grid. Call only if <code>tag.getQuestions().isEmpty()</code>.
	 * @param tag to be removed
	 */
	public void remove(Tag tag);
	
	/**
	 * Removes each and every Tag from the DB. This means, that 
	 * <ul>
	 * <li>No questions can be in the DB anymore
	 * <li>or all questions are untagged
	 * </ul>
	 */

	public void clear();
}
