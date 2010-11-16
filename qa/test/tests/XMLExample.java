package tests;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import models.database.Database;
import models.database.IDatabase;
import models.database.HotDatabase.HotDatabase;
import models.database.importers.Importer;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.xml.sax.SAXException;

import play.test.FunctionalTest;

public class XMLExample extends FunctionalTest {

	private static IDatabase old;
	private static String loaded;
	private static String incomplete;
	private static String inconsistent;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		old = Database.get();
		Database.swapWith(new HotDatabase());

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

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		Database.swapWith(old);
	}

	@Before
	public void setUp() throws Exception {
		Database.clear();
	}

	@Test
	public void loadFile() throws SAXException, IOException,
			ParserConfigurationException {
		Importer.importXML(loaded);
	}

	@Test
	public void hasCorrectUserCount() throws SAXException, IOException,
			ParserConfigurationException {
		loadFile();
		assertEquals(52, Database.get().users().count());
	}

	@Test
	public void hasCorrectQuestionCount() throws SAXException, IOException,
			ParserConfigurationException {
		loadFile();
		assertEquals(44, Database.get().questions().count());
	}

	@Test
	public void hasCorrectAnswerCount() throws SAXException, IOException,
			ParserConfigurationException {
		loadFile();
		assertEquals(51, Database.get().questions().countAllAnswers());
	}

	@Ignore("Answers are not read correctly. I guess")
	@Test
	public void withIncompleteData() throws SAXException, IOException,
			ParserConfigurationException {
		Importer.importXML(incomplete);
		assertEquals(50, Database.get().users().count());
		assertEquals(41, Database.get().questions().count());
		assertEquals(51, Database.get().questions().countAllAnswers());
	}
}
