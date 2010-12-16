package models.database.HotDatabase;

import models.database.IDatabase;
import models.database.IQuestionDatabase;
import models.database.ITagDatabase;
import models.database.IUserDatabase;

/**
 * Provides a Database that is kept entirely in Memory. Server's down, data's
 * dead.
 * 
 * @author aaron
 * 
 */
public class HotDatabase implements IDatabase {
	private final HotQuestionDatabase questions = new HotQuestionDatabase();
	private final HotUserDatabase users = new HotUserDatabase();
	private final HotTagDatabase tags = new HotTagDatabase();

	public IQuestionDatabase questions() {
		return questions;
	}

	public IUserDatabase users() {
		return users;
	}

	public ITagDatabase tags() {
		return tags;
	}
}
