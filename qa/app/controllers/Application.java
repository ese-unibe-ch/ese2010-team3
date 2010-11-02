package controllers;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import models.Answer;
import models.Comment;
import models.Notification;
import models.Question;
import models.Tag;
import models.TimeTracker;
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
			List<Question> similarQuestions = question.getSimilarQuestions();
			if (similarQuestions.size() > 3) {
				similarQuestions = similarQuestions.subList(0, 3);
			}
			List<Answer> answers = question.answers();
			render(question, answers, similarQuestions);
		}
	}

	public static void relatedQuestions(int id) {
		Question question = Question.get(id);

		render(question);
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
		boolean canEdit = true;
		if(session.get("username") != null && session.get("username").equals(userName)) {
			render(showUser, canEdit);
		}
		else {
			canEdit = false;
			render(showUser, canEdit);
		}
	}
	
		public static void editProfile(String userName) {
		User user = User.get(userName);
		render(user);
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

	public static void notifications() {
		User user = Session.get().currentUser();
		if (user != null) {
			ArrayList<Notification> notifications = user.getNotifications();
			render(notifications);
		} else
			Application.index();
	}

	public static void showStatisticalOverview() {
		GregorianCalendar now = new GregorianCalendar();
		TimeTracker t = TimeTracker.getRealTimeTracker();
		int numberOfQuestions;
		int numberOfAnswers;
		int numberOfUsers;
		int numberOfHighRatedAnswers;
		int numberOfBestAnswers;
		float questionsPerDay;
		float questionsPerWeek;
		float questionsPerMonth;
		float answersPerDay;
		float answersPerWeek;
		float answersPerMonth;

		numberOfUsers = User.getUserCount();
		numberOfQuestions = Question.questions().size();
		numberOfAnswers = Question.getAnswers().size();
		numberOfHighRatedAnswers = Question.getHighRatedAnswers().size();
		numberOfBestAnswers = Question.getBestRatedAnswers().size();
		questionsPerDay = (float) numberOfQuestions / (float) t.getDays(now);
		questionsPerWeek = (float) numberOfQuestions / (float) t.getWeeks(now);
		questionsPerMonth = (float) numberOfQuestions
				/ (float) t.getMonths(now);
		answersPerDay = (float) numberOfAnswers / (float) t.getDays(now);
		answersPerWeek = (float) numberOfAnswers / (float) t.getWeeks(now);
		answersPerMonth = (float) numberOfAnswers / (float) t.getMonths(now);

		render(numberOfQuestions, numberOfAnswers, numberOfUsers,
				numberOfHighRatedAnswers, numberOfBestAnswers, questionsPerDay,
				questionsPerWeek, questionsPerMonth, answersPerDay,
				answersPerWeek, answersPerMonth);
	}

}
