package tests;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import models.Question;
import models.database.Database;
import models.database.importers.Importer;
import models.database.importers.SemanticError;

import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

public class XMLReadingTest extends MockedUnitTest {

	static final String xml = "<?xml version=\"1.0\"?>\n"
			+
			"<QA>"
			+
			"  <users>"
			+
			"    <user id=\"277826\">"
			+
			"      <displayname>sdaau</displayname>"
			+
			"      <age>-1</age>"
			+
			"      <ismoderator>false</ismoderator>"
			+
			"      <email>sdaau@gmail.com</email>"
			+
			"      <password>secret</password>"
			+
			"      <aboutme>My name is sdaau</aboutme>"
			+
			"      <location/>"
			+
			"      <website/>"
			+
			"    </user>"
			+
			"  </users>"
			+
			""
			+
			"  <questions>"
			+
			"    <question id=\"4119991\">"
			+
			"      <ownerid>277826</ownerid>"
			+
			"      <creationdate>1289168092</creationdate>"
			+
			"      <lastactivity>1289176685</lastactivity>"
			+
			"      <body><![CDATA[The content with &lt; HTML &gt; tags]]></body>"
			+
			"      <title>Bash: call script with customized keyboard shortcuts?</title>"
			+
			"      <lastedit>1289176685</lastedit>"
			+
			"      <acceptedanswer>-1</acceptedanswer>"
			+
			"      <tags>"
			+
			"        <tag id=\"0\">linux</tag>						"
			+
			"        <tag id=\"1\">bash</tag>"
			+
			"        <tag id=\"2\">keyboard-shortcuts</tag>"
			+
			"        <tag id=\"3\">readline</tag>"
			+
			"        <tag id=\"4\">customize</tag>"
			+
			"      </tags>"
			+
			"    </question>"
			+
			"  </questions>"
			+
			""
			+
			"  <answers>"
			+
			"    <answer id=\"4120453\">							"
			+
			"      <ownerid>277826</ownerid>"
			+
			"      <questionid>4119991</questionid>"
			+
			"      <creationdate>1289175652</creationdate>"
			+
			"      <lastactivity>1289175652</lastactivity>"
			+
			"      <body><![CDATA[The content with &lt; HTML &gt; tags]]></body>"
			+
			"      <title>Bash: call script with customized keyboard shortcuts?</title>"
			+
			"      <lastedit>-1</lastedit>" +
			"      <accepted>false</accepted>" +
			"    </answer>" +
			"  </answers>" +
			"" +
			"</QA>";

	@Before
	public void clean() {
		Database.clear();
	}

	@Test
	public void shouldReadTom() throws SAXException, IOException,
			ParserConfigurationException {
		Importer.importXML(this.xml);
		assertFalse(Database.users().isAvailable("sdaau"));
	}

	@Test
	public void shouldReadQuestion() throws SAXException, IOException,
			ParserConfigurationException {
		assertEquals(0, Database.questions().count());
		Importer.importXML(this.xml);
		assertEquals(1, Database.questions().count());
	}

	@Test
	public void shouldReadAnswerToo() throws SAXException, IOException,
			ParserConfigurationException {
		assertEquals(0, Database.questions().countAllAnswers());
		Importer.importXML(this.xml);
		assertEquals(1, Database.questions().countAllAnswers());
	}

	@Test
	public void shouldNotContainCDATA() throws SAXException, IOException,
			ParserConfigurationException {
		Importer.importXML(this.xml);
		Question question = Database.questions().all().get(0);
		assertFalse(question.content().startsWith("<![CDATA["));
		assertFalse(question.answers().get(0).content().contains("<![CDATA["));
	}

	@Test
	public void shouldCheckSemantics() throws SAXException, IOException,
			ParserConfigurationException {
		boolean hasThrown = false;
		try {
			Importer.importXML("<invalid />");
		} catch (SemanticError err) {
			hasThrown = true;
		}
		assertTrue(hasThrown);

		hasThrown = false;
		try {
			Importer.importXML("<QA><answers><answer><ownerid>666</ownerid><questionid>999</questionid></answer></answers></QA>");
		} catch (SemanticError err) {
			hasThrown = true;
		}
		assertTrue(hasThrown);

		hasThrown = false;
		try {
			Importer.importXML("<QA><questions><question/></questions></QA>");
		} catch (SemanticError err) {
			hasThrown = true;
		}
		assertTrue(hasThrown);

		hasThrown = false;
		try {
			Importer.importXML(xml.replace("title>", "ignored>"));
		} catch (SemanticError err) {
			hasThrown = true;
		}
		assertTrue(hasThrown);

		hasThrown = false;
		try {
			Importer.importXML(xml.replace("<ownerid>277826</ownerid>",
					"<ownerid>13</ownerid>"));
		} catch (SemanticError err) {
			hasThrown = true;
		}
		assertTrue(hasThrown);

		hasThrown = false;
		try {
			Importer.importXML(xml.replace("<questionid>4119991</questionid>",
					"<questionid>37</questionid>"));
		} catch (SemanticError err) {
			hasThrown = true;
		}
		assertTrue(hasThrown);
	}

	@Test
	public void shouldTolerateSomeMissingValues() throws SAXException,
			IOException, ParserConfigurationException {
		Importer.importXML(xml.replace("<ownerid>277826</ownerid>",
				"<ownerid/>").replace("<answer id=\"4120453\">", "<answer>"));
		Question question = Database.questions().all().get(0);
		assertNull(question.owner());
		assertNull(question.answers().get(0).owner());
	}

	@Test
	public void shouldMakeCoberturaHappy() {
		new Importer();
	}
}
