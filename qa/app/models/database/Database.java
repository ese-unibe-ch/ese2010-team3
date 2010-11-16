package models.database;

import java.util.Collection;

import models.User;
import models.database.HotDatabase.HotDatabase;

/**
 * Database accessor. Can be swapped if necessary.
 * 
 */
public class Database {
	private static IDatabase instance = new HotDatabase();

	/**
	 * Gain access to the database.
	 * 
	 * @return the database that is currently responsible.
	 */
	public static IDatabase get() {
		return instance;
	}

	/**
	 * Exchanges the Database. Useful for mocking or hot replacement to other
	 * engine.
	 * 
	 * @param d
	 *            The fully functional database to take responsibility.
	 */
	public static void swapWith(IDatabase d) {
		instance = d;
	}

	/**
	 * Deletes all data. This ensures that the UserDB, the QuestionDB and the
	 * TagDB are completely empty. Useful for tests.
	 */

	public static void clear() {
		get().users().clear();
		get().tags().clear();
		get().questions().clear();
	}

	public static void clearKeepAdmins() {
		Collection<User> mods = Database.get().users().allModerators();
		Database.clear();
		for (User mod : mods) {
			Database.get().users().add(mod);
		}
	}
}
