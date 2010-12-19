package models.database.HotDatabase;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import models.database.IDatabase;
import models.database.IQuestionDatabase;
import models.database.ITagDatabase;
import models.database.IUserDatabase;
import models.database.importers.Importer;

import org.xml.sax.SAXException;

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

	public void importXML(File file) throws SAXException, IOException,
			ParserConfigurationException {
		new Importer(this).importXML(file);
	}

	public void clear(boolean keepAdmins) {
		this.users.clear(keepAdmins);
		this.tags.clear();
		this.questions.clear();
	}
}
