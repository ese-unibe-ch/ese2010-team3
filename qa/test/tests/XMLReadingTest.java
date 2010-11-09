package tests;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import models.database.Database;
import models.database.IDatabase;
import models.database.HotDatabase.HotDatabase;
import models.database.importers.Importer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import play.test.UnitTest;

public class XMLReadingTest extends UnitTest {
	private final String xmlTom = "<user><name>Tom</name><password>123</password></user>";
	private final String xmlHanna = "<user><name>Hanna</name><password>$%+</password></user>";

	private final String xmlQuestion = "<question><owner>Tom</owner><content>Why?</content></question>";
	private final String xmlAnswer = "<question>" +
			"<owner>Tom</owner><content>Why?</content>" +
			"<answer><owner>Hanna</owner><content>Because</content></answer>" +
			"</question>";

	private HotDatabase mock = new HotDatabase();
	private IDatabase old;

	@Before
	public void setUp() throws SAXException, IOException,
			ParserConfigurationException {
		old = Database.get();
		Database.swapWith(mock);
		Database.clear();
	}

	@After
	public void tearDown() {
		Database.swapWith(old);
	}

	private String xml(String x) {
		return "<data>" + x + "</data>";
	}

	@Test
	public void shouldReadTom() throws SAXException, IOException,
			ParserConfigurationException {
		Importer.importXML(xml(xmlTom));
		assertFalse(Database.get().users().needSignUp("Tom"));
	}

	@Test
	public void shouldReadTomAndHanna() throws SAXException, IOException,
			ParserConfigurationException {
		Importer.importXML(xml(xmlTom + xmlHanna));
		assertFalse(Database.get().users().needSignUp("Tom"));
		assertFalse(Database.get().users().needSignUp("Hanna"));
	}

	@Test
	public void shouldReadQuestion() throws SAXException, IOException,
			ParserConfigurationException {
		assertEquals(0, Database.get().questions().count());
		Importer.importXML(xml(xmlTom + xmlQuestion));
		assertEquals(1, Database.get().questions().count());
	}

	@Test
	public void shouldReadAnswerToo() throws SAXException, IOException,
			ParserConfigurationException {
		assertEquals(0, Database.get().questions().countAllAnswers());
		Importer.importXML(xml(xmlTom + xmlHanna + xmlAnswer));
		assertEquals(1, Database.get().questions().countAllAnswers());
	}
}
