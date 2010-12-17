package models.database.HotDatabase;

import models.database.IDatabase;
import models.database.IQuestionDatabase;
import models.database.ITagDatabase;
import models.database.IUserDatabase;

/**
 * Provides a Database that is kept entirely in Memory. Server down, data dead.
 * 
 * @author aaron
 * 
 */
public class HotDatabase implements IDatabase {
	private final HotQuestionDatabase questions;
	private final HotUserDatabase users;
	private final HotTagDatabase tags;

	public HotDatabase() {
		this.users = new HotUserDatabase();
		this.tags = new HotTagDatabase();
		this.questions = new HotQuestionDatabase(this.tags);
	}

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
