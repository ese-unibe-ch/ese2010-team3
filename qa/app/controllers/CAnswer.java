package controllers;

import models.Answer;
import models.Comment;
import models.Question;
import models.User;
import models.database.Database;
import models.helpers.Tools;
import play.data.validation.Required;
import play.data.validation.Validation;
import play.mvc.Controller;
import play.mvc.With;

@With(Secure.class)
public class CAnswer extends Controller {

	public static void newAnswer(int questionId, @Required String content) {
		Question question = Database.get().questions().get(questionId);
		if (!Validation.hasErrors() && question != null) {
			User user = Session.get().currentUser();
			if (!question.isLocked() && !user.isBlocked()) {
				question.answer(user, Tools.markdownToHtml(content));
				flash.success("Thanks for posting an answer.");
			}
		} else {
			flash.error("Please don't give empty answers.");
		}
		Application.question(questionId);
	}

	public static void newCommentAnswer(int questionId, int answerId,
			@Required String content) {
		Question question = Database.get().questions().get(questionId);
		Answer answer = question != null ? question.getAnswer(answerId) : null;
		User user = Session.get().currentUser();
		if (!Validation.hasErrors() && answer != null && !question.isLocked()
				&& !user.isBlocked()) {
			answer.comment(user, Tools.markdownToHtml(content));
			flash
					.success("May your comment be helpful in clarifying the answer!");
			Application.question(questionId);
		}
	}

	public static void addLikerAnswerComment(int cid, int qid, int aid) {
		Comment comment = Database.get().questions().get(qid).getAnswer(aid)
				.getComment(cid);
		comment.addLiker(Session.get().currentUser());
		flash.success("You like the comment. We're glad to know.");
		Application.question(qid);
	}

	public static void removeLikerAnswerComment(int cid, int qid, int aid) {
		Comment comment = Database.get().questions().get(qid).getAnswer(aid)
				.getComment(cid);
		comment.removeLiker(Session.get().currentUser());
		flash
				.success("You don't like the comment any longer. Hopefully you'll find other comments you like!");
		Application.question(qid);
	}

	public static void voteAnswerUp(int question, int id) {
		Question q = Database.get().questions().get(question);
		Answer answer = q.getAnswer(id);
		if (answer != null) {
			answer.voteUp(Session.get().currentUser());
			flash.success("Your up-vote has been registered");
			Application.question(question);
		} else {
			Application.index(0);
		}
	}

	public static void voteAnswerDown(int question, int id) {
		Question q = Database.get().questions().get(question);
		Answer answer = q.getAnswer(id);
		if (answer != null) {
			answer.voteDown(Session.get().currentUser());
			flash.success("Your down-vote has been registered.");
			Application.question(question);
		} else {
			Application.index(0);
		}
	}

	public static void voteAnswerCancel(int question, int id) {
		Question q = Database.get().questions().get(question);
		Answer answer = q.getAnswer(id);
		if (answer != null) {
			answer.voteCancel(Session.get().currentUser());
			flash.success("Your vote has been forgotten.");
			Application.question(question);
		} else {
			Application.index(0);
		}
	}

	public static void deleteAnswer(int questionId, int answerId) {
		Question question = Database.get().questions().get(questionId);
		Answer answer = question.getAnswer(answerId);
		answer.unregister();
		flash.success("The answer '%s' has been deleted.", answer.summary());
		Application.question(questionId);
	}

	public static void deleteCommentAnswer(int questionId, int answerId,
			int commentId) {
		Question question = Database.get().questions().get(questionId);
		Answer answer = question.getAnswer(answerId);
		Comment comment = answer.getComment(commentId);
		answer.unregister(comment);
		flash.success("The comment '%s' has been deleted.", comment.summary());
		Application.question(questionId);
	}

	public static void selectBestAnswer(int questionId, int answerId) {
		Question question = Database.get().questions().get(questionId);
		Answer answer = question.getAnswer(answerId);
		question.setBestAnswer(answer);
		flash.success("We're glad that you've been helped!");
		Application.question(questionId);
	}

}
