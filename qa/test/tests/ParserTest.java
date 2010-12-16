package tests;

import models.database.importers.Action;
import models.database.importers.Element;
import models.database.importers.ElementParser;
import models.database.importers.Syntax;

import org.junit.Before;
import org.junit.Test;

public class ParserTest extends MockedUnitTest {
	private ElementParser parser;
	private boolean isUpdated;

	@Before
	public void setUp() {
		isUpdated = false;
		Syntax syntax = new Syntax("users");
		syntax.by("user")
				.read("name")
				.read("password")
				.end();
		syntax.by("question")
				.read("owner")
				.read("content")
				.by("answer")
					.read("content")
					.read("owner")
					.end()
				.call(new Action() {
					public void call(Element e) {
						isUpdated = true;
					}
				})
				.end();
		parser = syntax.done();
	}

	@Test
	public void shouldReadEasyExample() {
		parser.start("user");
		parser.start("name");
		parser.text("Anton");
		parser.end();
		parser.end();
		assertFalse(parser.getElement().get("user").isEmpty());
		Element anton = parser.getElement().get("user").get(0);
		String name = anton.get("name").get(0).getText();
		assertEquals(name, "Anton");
	}

	@Test
	public void shouldReadTwoUsers() {
		parser.start("user");
		parser.start("name");
		parser.text("Anton");
		parser.end();
		parser.end();
		parser.start("user");
		parser.start("name");
		parser.text("Bea");
		parser.end();
		parser.end();
		assertFalse(parser.getElement().get("user").isEmpty());
		Element bea = parser.getElement().get("user").get(1);
		String name = bea.get("name").get(0).getText();
		assertEquals(name, "Bea");
	}

	@Test
	public void shouldCallback() {
		parser.start("question");
		assertFalse(isUpdated);
		parser.end();
		assertTrue(isUpdated);
	}

	@Test
	public void shouldHaveDebugFriendly_toString() {
		parser.start("user");
		parser.start("name");
		parser.text("Arnold");
		parser.end();
		parser.end();
		Element anton = parser.getElement();

		assertEquals(
				parser.toString(),
				"PS[users]({question=S[question]({content=S[content]({}), answer=S[answer]({content=S[content]({}), owner=S[owner]({})}), owner=S[owner]({})}), user=S[user]({name=S[name]({}), password=S[password]({})})})");
		assertEquals(anton.toString(),
				"E[root](\"\",{user=[E[user](\"\",{name=[E[name](\"Arnold\",{})]})]})");
	}
}
