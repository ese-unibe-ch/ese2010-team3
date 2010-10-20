package tests;

import java.util.Date;

import models.Answer;
import models.Comment;
import models.Question;
import models.User;

import org.junit.Before;
import org.junit.Test;

import play.test.UnitTest;

/**
 * 
 * Unregistering is tested in {@link UnregisterTest}
 * 
 * @author Tobias Brog
 * 
 */

public class CommentTest extends UnitTest {

	private User james;
	private Question question;
	private Answer answer;
	private Comment commentQuestion;
	private Comment commentAnswer;

	@Before
	public void setUp() {
		this.james = new User("James", "james");
		this.question = new Question(new User("Jack", "jack"),
				"Why did the chicken cross the road?");
		this.answer = this.question.answer(james, "To get to the other side.");
		this.commentQuestion = this.question
				.comment(james, "Strange Question!");
		this.commentAnswer = this.answer.comment(new User("Jill", "jill"),
				"Good point!");

	}

	@Test
	public void shouldCreateComments() {
		assertTrue(commentAnswer != null);
		assertTrue(commentQuestion != null);
	}

	@Test
	public void shouldHaveCorrectContent() {
		assertTrue(commentQuestion.content().equals("Strange Question!"));
		assertTrue(commentAnswer.content().equals("Good point!"));
	}

	@Test
	public void shouldHaveOwner() {
		assertTrue(commentQuestion.owner().equals(james));
	}

	@Test
	public void shouldRegisterItself() {
		assertTrue(question.hasComment(commentQuestion));
		assertTrue(answer.hasComment(commentAnswer));
	}

	@Test
	public void shouldHaveTimestamp() {
		assertTrue(commentQuestion.timestamp() != null);
		assertTrue(commentQuestion.timestamp().compareTo(new Date()) <= 0);
	}

}
