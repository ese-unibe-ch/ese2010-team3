package models.database.importers;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import models.Answer;
import models.Question;
import models.User;
import models.database.IDatabase;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class XMLParser extends DefaultHandler {
	private final Map<Integer, User> idUserBase;
	private final Map<Integer, Question> idQuestionBase;
	private final IDatabase db;

	private final List<ProtoAnswer> protoanswers;
	private final List<ProtoQuestion> protoquestions;

	private final ElementParser parser;

	private final Action createUser;
	private final Action createQuestion;
	private final Action createAnswer;
	private final Action makeConnections;

	public XMLParser(IDatabase db) {
		this.idUserBase = new HashMap<Integer, User>();
		this.idQuestionBase = new HashMap<Integer, Question>();
		this.db = db;

		this.protoanswers = new LinkedList();
		this.protoquestions = new LinkedList();

		this.makeConnections = new Action() {
			public void call(Element e) {
				makeConnections();
			}
		};

		this.createAnswer = new Action() {
			public void call(Element e) {
				createAnswer(e);
			}
		};

		this.createUser = new Action() {
			public void call(Element e) {
				createUser(e);
			}
		};

		this.createQuestion = new Action() {
			public void call(Element e) {
				createQuestion(e);
			}
		};

		this.parser = getSyntax();
	}

	/**
	 * Defines the standard syntax for the xml import.
	 * 
	 * @return
	 */
	private ElementParser getSyntax() {
		Syntax syntax = new Syntax("syntax");
		Syntax qa = syntax.by("QA");
		qa
				.by("users")
					.by("user")
						.read("displayname")
						.read("age")
						.read("ismoderator")
						.read("email")
						.read("password")
						.read("aboutme")
						.read("location")
						.read("website")
						.call(this.createUser);
		qa
				.by("questions")
					.by("question")
						.read("ownerid")
						.read("creationdate")
						.read("lastactivity")
						.read("body")
						.read("title")
						.read("lastedit")
						.read("acceptedanswer")
						.by("tags")
							.read("tag")
						.end()
						.call(this.createQuestion);
		qa
				.by("answers")
					.by("answer")
						.read("ownerid")
						.read("questionid")
						.read("creationdate")
						.read("lastactivity")
						.read("body")
						.read("title")
						.read("lastedit")
						.read("accepted")
						.call(this.createAnswer);
		qa.call(this.makeConnections);
		return new ElementParser(syntax);
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes atts) {
		Map<String, String> attributes = new HashMap();
		for (int i = 0; i < atts.getLength(); i++) {
			String attrname = atts.getLocalName(i);
			attributes.put(attrname, atts.getValue(i));
		}
		this.parser.start(qName, attributes);
	}

	@Override
	public void characters(char[] str, int start, int length) {
		this.parser.text(String.copyValueOf(str, start, length));
	}

	@Override
	public void endElement(String uri, String localName, String qName) {
		this.parser.end();
	}

	/**
	 * Create an User from the given element.
	 * 
	 * @param e
	 */
	private void createUser(Element e) {
		String name = e.getText("displayname");
		String password = e.getText("password");
		String email = e.getText("email");
		User user = this.db.users().register(name, password, email);
		Integer age = new Integer(e.getText("age"));
		if (age != -1) {
			Calendar pseudobirthday = GregorianCalendar.getInstance();
			pseudobirthday.add(Calendar.YEAR, -age);
			user.setDateOfBirth(pseudobirthday.getTime());
		}
		user.setBiography(e.getText("aboutme"));
		user.setWebsite(e.getText("website"));
		user.setModerator(e.getText("ismoderator").equals("true"), this.db
				.users().getModeratorMailbox());
		int id = new Integer(e.getArg("id"));
		this.idUserBase.put(id, user);
	}

	/**
	 * Create a question or throw a SemanticError, if it does
	 * not define a valid question.
	 * 
	 * @param e
	 */
	private void createQuestion(Element e) {
		ProtoQuestion question = new ProtoQuestion();
		try {
			question.title = e.getText("title");
			question.body = e.getText("body");
			question.id = this.toIntValue(e.getArg("id"));
			question.creation = new Date((new Long(e.getText("creationdate")))*1000);
			question.ownerid = this.toIntValue(e.getText("ownerid"));
			for (Element tagE : e.get("tags").get(0).get("tag")) {
				question.tags += " " + tagE.getText();
			}
			this.protoquestions.add(question);
		} catch (Throwable t) {
			throw new SemanticError("Question #" + e.getArg("id")
					+ " is invalid");
		}
	}

	/**
	 * Create a answer or throw a SemanticError, if it does not define a valid
	 * question.
	 * 
	 * @param e
	 */
	protected void createAnswer(Element e) {
		ProtoAnswer answer = new ProtoAnswer();
		try {
			answer.ownerid = this.toIntValue(e.getText("ownerid"));
			answer.questionid = this.toIntValue(e.getText("questionid"));
			answer.title = e.getText("title");
			answer.body = e.getText("body");
			answer.accepted = e.getText("accepted").equals("true");
			answer.id = this.toIntValue(e.getArg("id"));
			answer.creation = new Date((new Long(e.getText("creationdate")))*1000);
			this.protoanswers.add(answer);
		} catch (Throwable t) {
			throw new SemanticError("Answer #" + e.getArg("id")
					+ " is invalid");
		}
	}

	protected int toIntValue(String maybeInt) {
		if (maybeInt == null || maybeInt.equals(""))
			return -1;
		return new Integer(maybeInt).intValue();
	}

	protected void makeConnections() {
		for (ProtoQuestion protoquestion : this.protoquestions) {
			User owner = this.idUserBase.get(protoquestion.ownerid);

			if (owner == null && protoquestion.ownerid != -1)
				throw new SemanticError("No valid user: "
						+ protoquestion.ownerid);

			String content = protoquestion.body;
			content = "<h3>" + protoquestion.title + "</h3>\n" + content;
			Question question = this.db.questions().add(owner, content);
			question.setTimestamp(protoquestion.creation);
			this.idQuestionBase.put(protoquestion.id, question);
			question.setTagString(protoquestion.tags);
		}

		for (ProtoAnswer ans : this.protoanswers) {
			Question question = this.idQuestionBase.get(ans.questionid);
			if (question == null) {
				question = this.db.questions().add(null,
						"Anonymous question for imported answer #" + ans.id);
			}

			User owner = this.idUserBase.get(ans.ownerid);

			String content = ans.body;
			content = "<h3>" + ans.title + "</h3>\n" + content;
			Answer answer = question.answer(owner, content);
			answer.setTimestamp(ans.creation);
			if (ans.accepted) {
				question.setBestAnswer(answer);
			}
		}
	}

	private class ProtoQuestion {
		private String tags = "";
		private int ownerid;
		private Date creation;
		private String title;
		private String body;
		private int id;
	}

	private class ProtoAnswer {
		private int ownerid, questionid;
		private Date creation;
		private String title;
		private String body;
		private boolean accepted;
		private int id;
	}
}
