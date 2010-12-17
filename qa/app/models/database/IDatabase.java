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
	IQuestionDatabase questions();

	IUserDatabase users();

	ITagDatabase tags();

	void importXML(File file) throws SAXException, IOException,
			ParserConfigurationException;

	void clear(boolean keepAdmins);
}
