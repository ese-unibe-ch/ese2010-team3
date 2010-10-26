package controllers;

import java.util.GregorianCalendar;
import java.util.List;

import models.Answer;
import models.Comment;
import models.Question;
import models.TimeTracker;
import models.User;
import play.data.validation.Required;
import play.mvc.Before;
import play.mvc.Controller;

public class Application extends Controller {

	@Before
	static void setConnectedUser() {
		if (Security.isConnected()) {
			User user = User.get(Security.connected());
			renderArgs.put("user", user);
		}
	}

	public static void index() {
		List<Question> questions = Question.questions();
		render(questions);
	}

	public static void question(int id) {
		Question question = Question.get(id);
		if (question == null) {
			render();
		} else {
			List<Answer> answers = question.answers();
			render(question, answers);
		}
	}

	public static void answerQuestion(int id) {
		Question question = Question.get(id);
		List<Question> questions = Question.questions();
		List<Answer> answers = question.answers();
		int count = question.answers().size();
		render(questions, question, answers, count);
	}

	public static void commentQuestion(int id) {
		Question question = Question.get(id);
		List<Question> questions = Question.questions();
		List<Comment> comments = question.comments();
		int count = question.comments().size();
		render(questions, question, comments, count);
	}

	public static void commentAnswer(int questionId, int answerId) {
		Question question = Question.get(questionId);
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
			String email) {
		User user = User.register(username, password);
		if (email != null && email.matches("\\S+@\\w+\\.\\w+"))
			user.setEmail(email);
		// Mark user as connected
		session.put("username", username);
		index();
	}

	public static void showprofile() {
		render();
	}

	public static void editprofile(User user) {
		render();
	}

	public static void showStatisticalOverview() {
		TimeTracker t = TimeTracker.getInstance();
		GregorianCalendar now = new GregorianCalendar();
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
		System.out.println(t.getWeeks(now));
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
