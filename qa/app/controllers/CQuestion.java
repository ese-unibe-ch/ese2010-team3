package controllers;

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
public class CQuestion extends Controller {

	public static void newQuestion(@Required String content, String tags) {
		if (!Validation.hasErrors()) {
			User user = Session.get().currentUser();
			if (!user.isBlocked()) {
				Question question = Database.get().questions().add(user,
						Tools.markdownToHtml(content));
				question.setTagString(tags);
				user.startObserving(question);
				question.setTagString(tags);
				flash.success("Good luck for getting a reasonable answer!");
				Application.question(question.id());
			}
		} else {
			flash.error("Please don't ask empty questions.");
			Application.index(0);
		}
	}

	public static void updateTags(int id, String tags) {
		Question question = Database.get().questions().get(id);
		User user = Session.get().currentUser();
		if (question != null && user.canEdit(question)) {
			flash
					.success("Thanks for keeping this question's labels up-to-date.");
			question.setTagString(tags);
		}
		Application.question(id);
	}

	public static void newCommentQuestion(int questionId,
			@Required String content) {
		Question question = Database.get().questions().get(questionId);
		User user = Session.get().currentUser();
		if (!Validation.hasErrors() && question != null && !question.isLocked()
				&& !user.isBlocked()) {
			question.comment(user, Tools.markdownToHtml(content));
			flash
					.success("May your comment be helpful in clarifying the question!");
			Application.question(questionId);
		}
	}

	public static void addLikerQuestionComment(int cid, int qid) {
		Comment comment = Database.get().questions().get(qid).getComment(cid);
		comment.addLiker(Session.get().currentUser());
		flash.success("You like the comment. We're glad to know.");
		Application.question(qid);
	}

	public static void removeLikerQuestionComment(int cid, int qid) {
		Comment comment = Database.get().questions().get(qid).getComment(cid);
		comment.removeLiker(Session.get().currentUser());
		flash
				.success("You don't like the comment any longer. Hopefully you'll find other comments you like!");
		Application.question(qid);
	}

	public static void voteQuestionUp(int id) {
		Question question = Database.get().questions().get(id);
		if (question != null) {
			question.voteUp(Session.get().currentUser());
			flash.success("Your up-vote has been registered.");
			if (!CUser.redirectToCallingPage()) {
				Application.question(id);
			}
		} else {
			Application.index(0);
		}
	}

	public static void voteQuestionDown(int id) {
		Question question = Database.get().questions().get(id);
		if (question != null) {
			question.voteDown(Session.get().currentUser());
			flash.success("Your down-vote has been registered.");
			if (!CUser.redirectToCallingPage()) {
				Application.question(id);
			}
		} else {
			Application.index(0);
		}
	}

	public static void voteQuestionCancel(int id) {
		Question question = Database.get().questions().get(id);
		if (question != null) {
			question.voteCancel(Session.get().currentUser());
			flash.success("Your vote has been forgotten.");
			if (!CUser.redirectToCallingPage()) {
				Application.question(id);
			}
		} else {
			Application.index(0);
		}
	}

	public static void deleteQuestion(int id) {
		Question question = Database.get().questions().get(id);
		flash
				.success("The question '%s' has been deleted.", question
						.summary());
		question.unregister();
		Application.index(0);
	}

	public static void deleteCommentQuestion(int questionId, int commentId) {
		Question question = Database.get().questions().get(questionId);
		Comment comment = question.getComment(commentId);
		question.unregister(comment);
		flash.success("The comment '%s' has been deleted.", comment.summary());
		Application.question(questionId);
	}

	public static void watchQuestion(int id) {
		Question question = Database.get().questions().get(id);
		User user = Session.get().currentUser();
		if (question != null) {
			user.startObserving(question);
			flash.success("You're now watching this question.");
		}
		Application.question(id);
	}

	public static void unwatchQuestion(int id) {
		Question question = Database.get().questions().get(id);
		User user = Session.get().currentUser();
		if (question != null) {
			user.stopObserving(question);
			flash.success("You're no longer watching this question.");
		}
		Application.question(id);
	}

	public static void unwatchQuestionFromList(int id) {
		Question question = Database.get().questions().get(id);
		User user = Session.get().currentUser();
		if (question != null) {
			user.stopObserving(question);
			flash.success(
					"You're no longer watching <a href='/question/%d'>%s</a>.",
					id, question.summary());
		}
		Application.notifications(1);
	}

	public static void unlockQuestion(int id) {
		User user = Session.get().currentUser();
		if (user.isModerator()) {
			Question question = Database.get().questions().get(id);
			question.unlock();
			flash.success("This question has been unlocked.");
			Application.question(id);
		}
	}

	public static void lockQuestion(int id) {
		User user = Session.get().currentUser();
		if (user.isModerator()) {
			Question question = Database.get().questions().get(id);
			question.lock();
			flash.success("This question has been locked.");
			Application.question(id);
		}
	}

}
