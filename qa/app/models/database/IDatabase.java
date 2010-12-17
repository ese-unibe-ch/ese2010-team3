package models.database;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

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

	/**
	 * @return the active questions database
	 */
	IQuestionDatabase questions();

	/**
	 * @return the active users database
	 */
	IUserDatabase users();

	/**
	 * @return the active tags database
	 */
	ITagDatabase tags();

	/**
	 * Import all the data contained in an XML file into the database. See
	 * XMLParser.getSyntax for the syntax such an XML file is supposed to adhere
	 * to.
	 * 
	 * @param file
	 *            the XML file
	 */
	void importXML(File file) throws SAXException, IOException,
			ParserConfigurationException;

	/**
	 * Clears all the data from the various datastores, optionally with the
	 * exception of administrative users so that they don't have to lock
	 * themselves out of the system.
	 * 
	 * @param keepAdmins
	 *            whether to keep all the administrative users or whether to
	 *            delete them as well
	 */
	void clear(boolean keepAdmins);
}
