package controllers;

import java.util.List;

import models.Answer;
import models.Comment;
import models.Question;
import models.Tag;
import models.User;
import models.database.Database;
import models.database.HotDatabase.HotQuestionDatabase;
import play.data.validation.Required;
import play.mvc.Before;
import play.mvc.Controller;

public class Application extends Controller {

	@Before
	static void setConnectedUser() {
		if (Security.isConnected()) {
			User user = Database.get().users().get(Security.connected());
			renderArgs.put("user", user);
		}
	}

	public static void index() {
		List<Question> questions = Database.get().questions().all();
		render(questions);
	}

	public static void question(int id) {
		Question question = Database.get().questions().get(id);
		if (question == null) {
			render();
		} else {
			List<Answer> answers = question.answers();
			render(question, answers);
		}
	}

	public static void answerQuestion(int id) {
		Question question = Database.get().questions().get(id);
		List<Question> questions = Database.get().questions().all();
		List<Answer> answers = question.answers();
		int count = question.answers().size();
		render(questions, question, answers, count);
	}

	public static void commentQuestion(int id) {
		Question question = Database.get().questions().get(id);
		List<Question> questions = Database.get().questions().all();
		List<Comment> comments = question.comments();
		int count = question.comments().size();
		render(questions, question, comments, count);
	}

	public static void commentAnswer(int questionId, int answerId) {
		Question question = Database.get().questions().get(questionId);
		Answer answer = question.getAnswer(answerId);
		List<Comment> comments = answer.comments();
		render(answer, comments, question);
	}

	public static void deleteuser(User user) {
		render();
	}

	public static void register() {
		render();
	}

	public static void signup(@Required String username, String password,
			String passwordrepeat, String email) {

		if (User.checkEmail(email) && password.equals(passwordrepeat)) {
			User user = Database.get().users().register(username, password);
			user.setEmail(email);
			// Mark user as connected
			session.put("username", username);
			index();
		} else {
			flash.keep("url");
			if (!User.checkEmail(email)) {
				flash.error("secure.emailerror");
			} else {
				flash.error("secure.passworderror");
			}
			params.flash();
			register();
		}
	}
	public static void showprofile(String userName) {
		User showUser = Database.get().users().get(userName);
		render(showUser);
	}

	public static void tags(String term) {
		String tagString = "";
		for (Tag tag : Tag.tags())
			if (term == null || tag.getName().startsWith(term.toLowerCase()))
				tagString += tag.getName() + " ";
		// make sure not to return an array with a single empty string ([""])
		String[] tags = tagString.split("\\s+");
		if (tagString.length() == 0)
			tags = new String[0];
		renderJSON(tags);
	}
	
	public static void search(String term) {
		List<Question> results = Database.get().questions().searchFor(term);
		render(results,term);
	}
}
