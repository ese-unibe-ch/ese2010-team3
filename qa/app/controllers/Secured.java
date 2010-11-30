package controllers;

import java.io.File;
import java.text.ParseException;

import models.Answer;
import models.Comment;
import models.Entry;
import models.Notification;
import models.Question;
import models.User;
import models.database.Database;
import models.database.importers.Importer;
import models.helpers.Tools;
import play.data.validation.Required;
import play.data.validation.Validation;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.With;

@With(Secure.class)
public class Secured extends Controller {
	public static void newQuestion(@Required String content, String tags) {
		if (!Validation.hasErrors()) {
			User user = Session.get().currentUser();
			if (!user.isBlocked()) {
				Question question = Database.get().questions()
								.add(user, Tools.markdownToHtml(content));
				question.setTagString(tags);
				user.startObserving(question);
				question.setTagString(tags);
				flash.success("secure.newquestionflash");
				Application.question(question.id());
			}
		} else {
			flash.error("secure.emptyquestionerror");
			Application.index(0);
		}
	}

	public static void newAnswer(int questionId, @Required String content) {
		Question question = Database.get().questions().get(questionId);
		if (!Validation.hasErrors() && question != null) {
			User user = Session.get().currentUser();
			if (!question.isLocked() && !user.isBlocked()) {
				question.answer(user, Tools.markdownToHtml(content));
				flash.success("secure.newanswerflash");
			}
		} else {
			flash.error("secure.emptyanswererror");
		}
		Application.question(questionId);
	}

	public static void newCommentQuestion(int questionId,
			@Required String content) {
		Question question = Database.get().questions().get(questionId);
		User user = Session.get().currentUser();
		if (!Validation.hasErrors() && question != null && !question.isLocked() && !user.isBlocked()) {
			question.comment(user, Tools.markdownToHtml(content));
			flash.success("secure.newcommentquestionflash");
			Application.question(questionId);
		}
	}

	public static void newCommentAnswer(int questionId, int answerId,
			@Required String content) {
		Question question = Database.get().questions().get(questionId);
		Answer answer = question != null ? question.getAnswer(answerId) : null;
		User user = Session.get().currentUser();
		if (!Validation.hasErrors() && answer != null && !question.isLocked() && !user.isBlocked()) {
			answer.comment(user, Tools.markdownToHtml(content));
			flash.success("secure.newcommentanswerflash");
			Application.question(questionId);
		}
	}
	
	public static void addLikerQuestionComment(int cid, int qid){
		Comment comment=Database.get().questions().get(qid).getComment(cid);
		comment.addLiker(Session.get().currentUser());
		flash.success("secure.likecommentflash");
		Application.question(qid);
	}
	
	public static void addLikerAnswerComment(int cid, int qid, int aid){
		Comment comment=Database.get().questions().get(qid).getAnswer(aid).getComment(cid);
		comment.addLiker(Session.get().currentUser());
		flash.success("secure.likecommentflash");
		Application.question(qid);
	}
	
	public static void removeLikerQuestionComment(int cid, int qid){
		Comment comment=Database.get().questions().get(qid).getComment(cid);
		comment.removeLiker(Session.get().currentUser());
		flash.success("secure.dislikecommentflash");
		Application.question(qid);
	}
	
	public static void removeLikerAnswerComment(int cid, int qid, int aid){
		Comment comment=Database.get().questions().get(qid).getAnswer(aid).getComment(cid);
		comment.removeLiker(Session.get().currentUser());
		flash.success("secure.dislikecommentflash");
		Application.question(qid);
	}
	
	

	public static void voteQuestionUp(int id) {
		Question question = Database.get().questions().get(id);
		if (question != null) {
			question.voteUp(Session.get().currentUser());
			flash.success("secure.upvoteflash");
			if (!redirectToCallingPage()) {
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
			flash.success("secure.downvoteflash");
			if (!redirectToCallingPage()) {
				Application.question(id);
			}
		} else {
			Application.index(0);
		}
	}

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

	public static void deleteQuestion(int id) {
		Question question = Database.get().questions().get(id);
		flash
				.success("secure.questiondeletedflash");
		question.unregister();
		Application.index(0);
	}

	public static void deleteAnswer(int questionId, int answerId) {
		Question question = Database.get().questions().get(questionId);
		Answer answer = question.getAnswer(answerId);
		answer.unregister();
		flash.success("secure.answerdeletedflash");
		Application.question(questionId);
	}

	public static void deleteCommentQuestion(int questionId, int commentId) {
		Question question = Database.get().questions().get(questionId);
		Comment comment = question.getComment(commentId);
		question.unregister(comment);
		flash.success("secure.commentdeletedflash");
		Application.question(questionId);
	}

	public static void deleteCommentAnswer(int questionId, int answerId,
			int commentId) {
		Question question = Database.get().questions().get(questionId);
		Answer answer = question.getAnswer(answerId);
		Comment comment = answer.getComment(commentId);
		answer.unregister(comment);
		flash.success("secure.commentdeletedflash");
		Application.question(questionId);
	}

	public static void deleteUser(String name) throws Throwable {
		User user = Database.get().users().get(name);
		if (hasPermissionToDelete(Session.get().currentUser(), user)) {
			user.delete();
			flash.success("secure.userdeletedflash");
			Secure.logout();
			Application.index(0);
		}
		flash.error("secure.userdeleteerror");
		if (!redirectToCallingPage()) {
			Application.index(0);
		}
	}

	public static void anonymizeUser(String name) throws Throwable {
		User user = Database.get().users().get(name);
		if (hasPermissionToDelete(Session.get().currentUser(), user)) {
			user.anonymize(true, false);
		}
		deleteUser(name);
	}

	public static void selectBestAnswer(int questionId, int answerId) {
		Question question = Database.get().questions().get(questionId);
		Answer answer = question.getAnswer(answerId);
		question.setBestAnswer(answer);
		flash.success("secure.bestanswerflash");
		Application.question(questionId);
	}

	private static boolean hasPermissionToDelete(User currentUser, User user) {
		return currentUser == user;
	}

	private static boolean redirectToCallingPage() {
		Http.Header referer = request.headers.get("referer");
		if (referer == null)
			return false;
		redirect(referer.value());
		return true;
	}

	public static void saveProfile(String name, String email, String fullname,
			String birthday, String website, String profession,
			String employer, String biography) throws ParseException {
		User user = Database.get().users().get(name);
		if (!Application.mayLoggedInUserEditProfileOf(user)) {
			flash.error("secure.editprofileerror");
			Application.showprofile(user.getName());
		}
		if (email != null) {
			user.setEmail(email);
		}
		if (fullname != null) {
			user.setFullname(fullname);
		}
		if (birthday != null) {
			user.setDateOfBirth(birthday);
		}
		if (website != null) {
			user.setWebsite(website);
		}
		if (profession != null) {
			user.setProfession(profession);
		}
		if (employer != null) {
			user.setEmployer(employer);
		}
		if (biography != null) {
			user.setBiography(biography);
		}
		flash.success("secure.editprofileflash");
		Application.showprofile(user.getName());
	}

	public static void updateTags(int id, String tags) {
		Question question = Database.get().questions().get(id);
		User user = Session.get().currentUser();
		if (question != null && user.canEdit(question)) {
			flash
					.success("secure.editprofileflash");
			question.setTagString(tags);
		}
		Application.question(id);
	}

	public static void watchQuestion(int id) {
		Question question = Database.get().questions().get(id);
		User user = Session.get().currentUser();
		if (question != null) {
			user.startObserving(question);
			flash.success("secure.startwatchquestionflash");
		}
		Application.question(id);
	}

	public static void unwatchQuestion(int id) {
		Question question = Database.get().questions().get(id);
		User user = Session.get().currentUser();
		if (question != null) {
			user.stopObserving(question);
			flash.success("secure.stopwatchquestionflash");
		}
		Application.question(id);
	}

	public static void unwatchQuestionFromList(int id) {
		Question question = Database.get().questions().get(id);
		User user = Session.get().currentUser();
		if (question != null) {
			user.stopObserving(question);
			flash.success(
					"secure.stopwatchquestionflash",
					id, question.summary());
		}
		Application.notifications(1);
	}

	public static void followNotification(int id) {
		User user = Session.get().currentUser();
		Notification notification = user.getNotification(id);
		if (notification != null) {
			notification.unsetNew();
		}
		if (notification != null && notification.getAbout() instanceof Answer) {
			Application.question(((Answer) notification.getAbout())
					.getQuestion().id());
		} else if (!redirectToCallingPage()) {
			Application.notifications(0);
		}
	}

	public static void clearNewNotifications() {
		User user = Session.get().currentUser();
		for (Notification n : user.getNewNotifications()) {
			n.unsetNew();
		}
		flash.success("secure.notificationsmarkedasreadflash");
		Application.notifications(0);
	}

	public static void deleteNotification(int id) {
		User user = Session.get().currentUser();
		Notification n = user.getNotification(id);
		if (n != null) {
			n.unregister();
			flash.success("secure.deletenotificationflash");
		}
		Application.notifications(0);
	}

	public static void blockUser(String username, String reason) {
		User user = Database.get().users().get(username);
		User mod = Session.get().currentUser();
		if (mod.isModerator() && mod != user) {
			if (reason.equals("")) {
				reason = "secure.blockreasonerror";
			}
			user.block(reason);
			flash.success("secure.blockuserflash");
		}
		Application.showprofile(user.getName());
	}

	public static void unblockUser(String username) {
		User user = Database.get().users().get(username);
		User mod = Session.get().currentUser();
		if (mod.isModerator() && mod != user) {
			user.unblock();
			flash.success("secure.unlockuserflash");
		}
		Application.showprofile(user.getName());
	}

	public static void lockQuestion(int id) {
		User user = Session.get().currentUser();
		if (user.isModerator()) {
			Question question = Database.get().questions().get(id);
			question.lock();
			flash.success("secure.lockquestionflash");
			Application.question(id);
		}
	}

	public static void unlockQuestion(int id) {
		User user = Session.get().currentUser();
		if (user.isModerator()) {
			Question question = Database.get().questions().get(id);
			question.unlock();
			flash.success("secure.unlockquestionflash");
			Application.question(id);
		}
	}

	public static void loadXML(@Required File xml) {
		if (!Session.get().currentUser().isModerator()) {
			Application.index(0);
		}
		if (xml == null) {
			flash.error("secure.xmlselecterror");
			Application.admin();
		}

		try {
			Importer.importXML(xml);
			flash.success("secure.xmlloadflash");
		} catch (Throwable e) {
			flash.error("secure.xmlloaderror", e.getMessage());
			e.printStackTrace();
			Application.admin();
		}
		if (xml != null) {
			xml.delete();
		}
		Application.index(0);
	}

	public static void clearDB() {
		if (!Session.get().currentUser().isModerator()) {
			flash.error("secure.cleardberror");
			Application.index(0);
		}
		Database.clearKeepAdmins();
		flash.success("secure.cleardbflash");
		Application.admin();
	}
}
