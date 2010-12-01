package tests;

import models.Answer;
import models.Comment;
import models.Question;
import models.User;
import models.database.Database;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import play.test.FunctionalTest;
import tests.mocks.SessionMock;
import controllers.CAnswer;
import controllers.CQuestion;
import controllers.Session;

@Ignore
public class UserInteractionTest extends FunctionalTest {

	private User jack;
	private SessionMock session;

	@Before
	public void setUp() throws Exception {
		jack = new User("Jack", "");
		session = new SessionMock();
		session.loginAs(jack);
		Session.mockWith(session);
	}

	@Test
	public void shouldPostQuestion() {
		// Response response = POST("/","","why?");
		// assertStatus(302,response);
		// Question question = null;
		// for (Question q :Question.questions()) {
		// if (q.owner().equals(jack))
		// question = q;
		// }
		controllers.CQuestion.newQuestion("why?", "stupid");
		Question question = Database.get().questions().searchFor("why").get(0);
		assertNotNull(question);
		assertEquals(question.owner(), jack);
		assertTrue(Database.get().questions().all().contains(question));
	}

	@Test
	public void shouldPostAnswer() {
		controllers.CQuestion.newQuestion("why?", "stupid");
		Question question = Database.get().questions().searchFor("why").get(0);
		CAnswer.newAnswer(question.id(), "nevermind");
		Answer answer = question.answers().get(0);
		assertNotNull(answer);
		assertEquals(answer.owner(), jack);
		assertTrue(question.answers().contains(answer));
	}

	@Test
	public void shouldPostComment() {
		controllers.CQuestion.newQuestion("why?", "stupid");
		Question question = Database.get().questions().searchFor("why").get(0);
		controllers.CQuestion.newCommentQuestion(question.id(),
				"Could I specify?");
		Comment comment = question.comments().get(0);
		assertNotNull(comment);
		assertEquals(comment.owner(), jack);
		assertTrue(question.comments().contains(comment));
	}

	@Test
	public void shouldPostAnswerComment() {
		controllers.CQuestion.newQuestion("why?", "stupid");
		Question question = Database.get().questions().searchFor("why").get(0);
		CAnswer.newAnswer(question.id(), "nevermind");
		Answer answer = question.answers().get(0);
		controllers.CAnswer.newCommentAnswer(question.id(), answer.id(),
				"Good Point");
		Comment comment = answer.getComment(0);
		assertNotNull(comment);
		assertEquals(comment.owner(), jack);
		assertTrue(answer.comments().contains(comment));
	}

	@Test
	public void shouldVoteQuestion() {
		controllers.CQuestion.newQuestion("why?", "stupid");
		Question question = Database.get().questions().searchFor("why").get(0);
		User jill = new User("Jill", "");
		session.loginAs(jill);
		CQuestion.voteQuestionDown(question.id());
		assertEquals(-1, question.rating());
		CQuestion.voteQuestionUp(question.id());
		assertEquals(1, question.rating());
	}

	@Test
	public void shouldVoteAnswer() {
		controllers.CQuestion.newQuestion("why?", "stupid");
		Question question = Database.get().questions().searchFor("why").get(0);
		CAnswer.newAnswer(question.id(), "nevermind");
		Answer answer = question.answers().get(0);
		User jill = new User("Jill", "");
		session.loginAs(jill);
		CAnswer.voteAnswerDown(question.id(), answer.id());
		assertEquals(-1, answer.rating());
		controllers.CAnswer.voteAnswerUp(question.id(), answer.id());
		assertEquals(1, answer.rating());
	}

	@Test
	public void shouldDeleteQuestion() {
		controllers.CQuestion.newQuestion("why?", "stupid");
		Question question = Database.get().questions().searchFor("why").get(0);
		CQuestion.deleteQuestion(question.id());
		assertFalse(Database.get().questions().all().contains(question));
	}

	@Test
	public void shouldDeleteAnswer() {
		controllers.CQuestion.newQuestion("why?", "stupid");
		Question question = Database.get().questions().searchFor("why").get(0);
		CAnswer.newAnswer(question.id(), "nevermind");
		Answer answer = question.answers().get(0);
		CQuestion.deleteQuestion(answer.id());
		assertFalse(question.answers().contains(question));
	}

	@Test
	public void shouldDeleteQuestionComment() {
		controllers.CQuestion.newQuestion("why?", "stupid");
		Question question = Database.get().questions().searchFor("why").get(0);
		controllers.CQuestion.newCommentQuestion(question.id(),
				"Could I specify?");
		Comment comment = question.comments().get(0);
		CQuestion.deleteCommentQuestion(comment.id(), question.id());
		assertFalse(question.comments().contains(question));
	}

	@Test
	public void shouldDeleteAnswerComment() {
		controllers.CQuestion.newQuestion("why?", "stupid");
		Question question = Database.get().questions().searchFor("why").get(0);
		CAnswer.newAnswer(question.id(), "nevermind");
		Answer answer = question.getAnswer(0);
		controllers.CAnswer.newCommentAnswer(answer.id(), question.id(),
				"Could I specify?");
		Comment comment = answer.getComment(0);
		CAnswer.deleteCommentAnswer(comment.id(), question.id(), answer.id());
		assertFalse(answer.comments().contains(question));
	}
}
