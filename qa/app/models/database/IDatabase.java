package models.database;

/**
 * Provides getters for all databases. There is a separate DB for
 * <ul>
 * <li>questions</li>
 * <li>users</li>
 * <li>tags</li>
 * </ul>
 * Add functionality in these where ever possible and reasonable.
 * 
 * @author Aaron
 */
public interface IDatabase {
	IQuestionDatabase questions();

	IUserDatabase users();

	ITagDatabase tags();
}
