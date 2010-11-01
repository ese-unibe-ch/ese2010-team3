	package models.database;
/**
 * Provides getters for all databases. There is a separate DB for
 * <ul>
 * <li>questions</li>
 * <li>answers</li>
 * <li>comments</li>
 * <li>votes</li>
 * <li>users</li>
 * </ul>
 * @author Aaron
 */
public interface IDatabase {
	IQuestionDatabase questions();
	
	IAnswerDatabase answers();
	
	ICommentDatabase comments();
	
	IVoteDatabase votes();
	
	IUserDatabase users();
}
