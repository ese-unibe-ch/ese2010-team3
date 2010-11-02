	package models.database;
/**
 * Provides getters for all databases. There is a separate DB for
 * <ul>
 * <li>questions</li>
 * <li>users</li>
 * </ul>
 * @author Aaron
 */
public interface IDatabase {
	IQuestionDatabase questions();
	
	IUserDatabase users();
	
	ITagDatabase tags();
}
