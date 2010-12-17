package tests;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import models.database.IDatabase;
import models.database.HotDatabase.HotDatabase;
import models.database.importers.Importer;
import models.database.importers.SemanticError;

import org.junit.Test;
import org.xml.sax.SAXException;

public class XMLExample extends MockedUnitTest {

	@Test
	public void hasCorrectUserCount() throws SAXException, IOException,
			ParserConfigurationException {
		IDatabase db = new HotDatabase();
		new Importer(db).importXML(play.Play.getFile("conf/fixtures/QA3.xml"));
		assertEquals(52, db.users().count());
	}

	@Test
	public void hasCorrectQuestionCount() throws SAXException, IOException,
			ParserConfigurationException {
		IDatabase db = new HotDatabase();
		new Importer(db).importXML(play.Play.getFile("conf/fixtures/QA3.xml"));
		assertEquals(44, db.questions().count());
	}

	@Test
	public void hasCorrectAnswerCount() throws SAXException, IOException,
			ParserConfigurationException {
		IDatabase db = new HotDatabase();
		new Importer(db).importXML(play.Play.getFile("conf/fixtures/QA3.xml"));
		assertEquals(51, db.questions().countAllAnswers());
	}

	@Test
	public void withIncompleteData() throws SAXException, IOException,
			ParserConfigurationException {
		IDatabase db = new HotDatabase();
		new Importer(db).importXML(play.Play
				.getFile("conf/fixtures/incompleteData.xml"));
		assertEquals(50, db.users().count());
		assertEquals(47, db.questions().count());
		assertEquals(51, db.questions().countAllAnswers());
	}

	@Test(expected = SemanticError.class)
	public void withInconsistentData() throws SAXException, IOException,
			ParserConfigurationException {
		IDatabase db = new HotDatabase();
		new Importer(db).importXML(play.Play
				.getFile("conf/fixtures/inconsistentData.xml"));
	}
}
