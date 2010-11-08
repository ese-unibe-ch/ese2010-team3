package models.database.importers;

import java.util.List;

import models.Question;
import models.User;
import models.database.Database;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class XMLParser extends DefaultHandler {
	private final ElementParser parser = getSyntax();
	private static final Action createUser = new Action() {
		public void call(Element e) {
			createUser(e);
		}
	};
	private static final Action createQuestion = new Action() {
		public void call(Element e) {
			createQuestion(e);
		}
	};

	private static ElementParser getSyntax() {
		Syntax syntax = new Syntax("syntax");
		syntax.by("user")
				.read("name")
				.read("password")
				.call(createUser)
				.end();
		syntax.by("question")
				.read("owner")
				.read("content")
				.by("answer")
					.read("content")
					.read("owner")
					.end()
				.call(createQuestion)
				.end();
		return new ElementParser(syntax);
	}

	public void startElement(String uri, String localName, String qName,
			Attributes atts) {
		parser.start(qName, atts);
	}

	private static void createUser(Element e) {
		String name = e.getText("name");
		String password = e.getText("password");
		Database.get().users().register(name, password);
	}

	private static void createQuestion(Element e) {
		String content = e.getText("content");
		String ownername = e.getText("owner");
		User owner = Database.get().users().get(ownername);
		Question question = Database.get().questions().add(owner, content);
		List<Element> answerEntries = e.get("answer");
		for (Element answerEntry : answerEntries) {
			content = answerEntry.getText("content");
			ownername = e.getText("owner");
			owner = Database.get().users().get(ownername);
			question.answer(owner, content);
		}
	}
}
