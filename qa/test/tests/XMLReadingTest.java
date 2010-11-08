package tests;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import models.database.Database;
import models.database.IDatabase;
import models.database.HotDatabase.HotDatabase;
import models.database.importers.Importer;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.xml.sax.SAXException;

import play.test.UnitTest;

@Ignore("Not yet working")
public class XMLReadingTest extends UnitTest {
	private final String xmlTom = "<user><name>Tom</name><password>123</password></user>";
	private final String xmlHanna = "<user><name>Hanna</name><password>$%+</password></user>";

	private HotDatabase mock = new HotDatabase();
	private IDatabase old;

	@Before
	public void setUp() throws SAXException, IOException,
			ParserConfigurationException {
		old = Database.get();
		Database.swapWith(mock);
		Importer.importXML(xmlTom);
	}

	@After
	public void tearDown() {
		Database.swapWith(old);
	}

	@Test
	public void shouldReadTom() {
		assertFalse(Database.get().users().needSignUp("Tom"));
	}
}
