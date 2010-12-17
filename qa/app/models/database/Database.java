package models.database;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import javax.xml.parsers.ParserConfigurationException;

import models.User;
import models.database.HotDatabase.HotDatabase;
import models.database.importers.Importer;

import org.xml.sax.SAXException;

/**
 * Database accessor. Can be swapped if necessary.
 * 
 */
public class Database {
	private static IDatabase instance = new HotDatabase();

	/**
	 * Exchanges the Database. Useful for mocking or hot replacement to other
	 * engine.
	 * 
	 * @param database
	 *            The fully functional database to take responsibility.
	 * @return the previously active database (in case you want to restore it
	 *         later)
	 */
	public static IDatabase swapWith(IDatabase database) {
		IDatabase previousDB = instance;
		instance = database;
		return previousDB;
	}

	/**
	 * Gain access to the current question database.
	 * 
	 * @return the database that is currently responsible for questions.
	 */
	public static IQuestionDatabase questions() {
		return instance.questions();
	}

	/**
	 * Gain access to the current user database.
	 * 
	 * @return the database that is currently responsible for users.
	 */
	public static IUserDatabase users() {
		return instance.users();
	}

	/**
	 * Gain access to the current tag database.
	 * 
	 * @return the database that is currently responsible for tags.
	 */
	public static ITagDatabase tags() {
		return instance.tags();
	}

	/**
	 * Deletes all data. This ensures that the UserDB, the QuestionDB and the
	 * TagDB are completely empty. Useful for tests.
	 */
	public static void clear() {
		users().clear();
		tags().clear();
		questions().clear();
	}

	public static void clearKeepAdmins() {
		Collection<User> mods = Database.users().allModerators();
		Database.clear();
		for (User mod : mods) {
			Database.users().add(mod);
		}
	}

	/**
	 * Imports data form an XML datasource into the currently running database.
	 * 
	 * @param file
	 *            a file containing XML data in the expected schema.
	 */
	public static void importXML(File file) throws SAXException, IOException,
			ParserConfigurationException {
		new Importer(instance).importXML(file);
	}
}
