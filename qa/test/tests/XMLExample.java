package tests;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import models.database.IDatabase;
import models.database.HotDatabase.HotDatabase;
import models.database.importers.Importer;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.xml.sax.SAXException;

import play.test.FunctionalTest;

public class XMLExample extends FunctionalTest {

	private static IDatabase db;
	private static String loaded;
	private static String incomplete;
	@SuppressWarnings("unused")
	private static String inconsistent;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		loaded = loadFile("conf/fixtures/QA3.xml");
		incomplete = loadFile("conf/fixtures/incompleteData.xml");
		inconsistent = loadFile("conf/fixtures/inconsistentData.xml");
	}

	private static String loadFile(String filename) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(
				filename));
		StringBuilder build = new StringBuilder();
		String line = reader.readLine();
		while (line != null) {
			build.append(line);
			line = reader.readLine();
		}
		return build.toString();
	}

	@Before
	public void setUp() throws Exception {
		this.db = new HotDatabase();
	}

	@Test
	public void hasCorrectUserCount() throws SAXException, IOException,
			ParserConfigurationException {
		new Importer(this.db).importXML(loaded);
		assertEquals(52, this.db.users().count());
	}

	@Test
	public void hasCorrectQuestionCount() throws SAXException, IOException,
			ParserConfigurationException {
		new Importer(this.db).importXML(loaded);
		assertEquals(44, this.db.questions().count());
	}

	@Test
	public void hasCorrectAnswerCount() throws SAXException, IOException,
			ParserConfigurationException {
		new Importer(this.db).importXML(loaded);
		assertEquals(51, this.db.questions().countAllAnswers());
	}

	@Ignore("Answers are not read correctly. I guess")
	@Test
	public void withIncompleteData() throws SAXException, IOException,
			ParserConfigurationException {
		new Importer(this.db).importXML(incomplete);
		assertEquals(50, this.db.users().count());
		assertEquals(41, this.db.questions().count());
		assertEquals(51, this.db.questions().countAllAnswers());
	}
}
