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
import play.mvc.Router.ActionDefinition;
import play.mvc.With;

/**
 * The controller for all routes concerning the {@link Answer}'s.
 * 
 * @author Group3
 * 
 */
@With(Secure.class)
public class CAnswer extends Controller {

	/**
	 * Add a new {@link Answer}.
	 * 
	 * @param questionId
	 *            the id of the {@link Question}.
	 * @param content
	 *            the content of the {@link Answer}.
	 */
	public static void newAnswer(int questionId, @Required String content) {
		Question question = Database.get().questions().get(questionId);
		if (!Validation.hasErrors() && question != null) {
			User user = Session.get().currentUser();
			if (!question.isLocked() && user.canPost()) {
				Answer answer = question.answer(user,
						Tools.markdownToHtml(content));
				flash.success("secure.newanswerflash");
				ActionDefinition action = reverse();
				Application.question(questionId);
				redirect(action.addRef("answer-" + answer.id()).toString());
			}
		} else {
			flash.error("secure.emptyanswererror");
		}
		Application.question(questionId);
	}

	/**
	 * Add a new {@link Comment} to an {@link Answer}.
	 * 
	 * @param questionId
	 *            the id of the {@link Question}.
	 * @param answerId
	 *            the id of the {@link Answer}.
	 * @param content
	 *            the content of the {@link Comment}. This field is required to
	 *            be filled out.
	 */
	public static void newCommentAnswer(int questionId, int answerId,
			@Required String content) {
		Question question = Database.get().questions().get(questionId);
		Answer answer = question != null ? question.getAnswer(answerId) : null;
		User user = Session.get().currentUser();
		if (!Validation.hasErrors() && answer != null && !question.isLocked()
				&& user.canPost()) {
			Comment comment = answer.comment(user, Tools.markdownToHtml(content));
			flash.success("secure.newcommentanswerflash");
			ActionDefinition action = reverse();
			Application.question(questionId);
			redirect(action.addRef("comment-" + comment.id()).toString());
		}
	}

	/**
	 * Adds the liker of a {@link Comment} to an {@link Answer}.
	 * 
	 * @param commentId
	 *            the id of the {@link Comment}.
	 * @param questionId
	 *            the id of the {@link Question}.
	 * @param answerId
	 *            the id of the {@link Answer}.
	 */
	public static void addLikerAnswerComment(int commentId, int questionId,
			int answerId) {
		Comment comment = Database.get().questions().get(questionId).getAnswer(
				answerId).getComment(commentId);
		comment.addLiker(Session.get().currentUser());
		flash.success("secure.likecommentflash");
		Application.question(questionId);
	}

	/**
	 * Removes the liker of a {@link Comment} to an {@link Answer}.
	 * 
	 * @param commentId
	 *            the id of the {@link Comment}.
	 * @param questionId
	 *            the id of the {@link Question}.
	 * @param answerId
	 *            the id of the {@link Answer}.
	 */
	public static void removeLikerAnswerComment(int commentId, int questionId,
			int answerId) {
		Comment comment = Database.get().questions().get(questionId).getAnswer(
				answerId).getComment(commentId);
		comment.removeLiker(Session.get().currentUser());
		flash.success("secure.dislikecommentflash");
		Application.question(questionId);
	}

	/**
	 * Vote {@link Answer} up.
	 * 
	 * @param question
	 *            the id of the {@link Question} to which the {@link Answer}.
	 *            belongs.
	 * @param id
	 *            the id of the {@link Answer}.
	 */
	public static void voteAnswerUp(int question, int id) {
		Question q = Database.get().questions().get(question);
		Answer answer = q.getAnswer(id);
		if (answer != null) {
			answer.voteUp(Session.get().currentUser());
			flash.success("secure.upvoteflash");
			Application.question(question);
		} else {
			Application.index(0);
		}
	}

	/**
	 * Vote an {@link Answer} down.
	 * 
	 * @param question
	 *            the id of the {@link Question} to which the {@link Answer}.
	 *            belongs.
	 * @param id
	 *            the id of the {@link Answer}.
	 */
	public static void voteAnswerDown(int question, int id) {
		Question q = Database.get().questions().get(question);
		Answer answer = q.getAnswer(id);
		if (answer != null) {
			answer.voteDown(Session.get().currentUser());
			flash.success("secure.downvoteflash");
			Application.question(question);
		} else {
			Application.index(0);
		}
	}

	/**
	 * Cancel the own vote to an {@link Answer}.
	 * 
	 * @param question
	 *            the id of the {@link Question} to which the {@link Answer}.
	 *            belongs.
	 * @param id
	 *            the id of the {@link Answer}.
	 */
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

	/**
	 * Delete an answer.
	 * 
	 * @param questionId
	 *            the id of the {@link Question} to which the {@link Answer}
	 *            belongs.
	 * @param answerId
	 *            the id of the {@link Answer}
	 */
	public static void deleteAnswer(int questionId, int answerId) {
		Question question = Database.get().questions().get(questionId);
		Answer answer = question.getAnswer(answerId);
		answer.unregister();
		flash.success("secure.answerdeletedflash");
		Application.question(questionId);
	}

	/**
	 * Delete the comment to an {@link Answer}.
	 * 
	 * @param questionId
	 *            the id of the {@link Question}.
	 * @param answerId
	 *            the id of the {@link Answer}.
	 * @param commentId
	 *            the id of the {@link Comment}.
	 */
	public static void deleteCommentAnswer(int questionId, int answerId,
			int commentId) {
		Question question = Database.get().questions().get(questionId);
		Answer answer = question.getAnswer(answerId);
		Comment comment = answer.getComment(commentId);
		answer.unregister(comment);
		flash.success("secure.commentdeletedflash");
		Application.question(questionId);
	}

	/**
	 * Select the best answer to a question.
	 * 
	 * @param questionId
	 *            the question id
	 * @param answerId
	 *            the answer id
	 */
	public static void selectBestAnswer(int questionId, int answerId) {
		Question question = Database.get().questions().get(questionId);
		Answer answer = question.getAnswer(answerId);
		question.setBestAnswer(answer);
		flash.success("secure.bestanswerflash");
		Application.question(questionId);
	}

}
