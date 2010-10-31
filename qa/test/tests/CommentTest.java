package tests;

import java.util.Date;
import java.util.List;

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
		generateComments();
	}
	
	private void generateComments() {
		this.commentQuestion = this.question
				.comment(james, "Strange Question!");
		this.commentAnswer = this.answer.comment(new User("Jill", "jill"),
				"Good point!");
	}

	@Test
	public void shouldCreateComments() {
		generateComments();
		Comment comment = answer.comment(james, "O RLY?");
		assertNotNull(comment);
		assertNotNull(commentAnswer);
		assertNotNull(commentQuestion);
	}

	/* testing getters is useless. */
	@Test
	public void shouldHaveCorrectContent() {
		generateComments();
		assertTrue(commentQuestion.content().equals("Strange Question!"));
		assertTrue(commentAnswer.content().equals("Good point!"));
	}

	@Test
	public void shouldHaveOwner() {
		generateComments();
		assertTrue(commentQuestion.owner().equals(james));
	}

	@Test
	public void shouldRegisterItself() {
		generateComments();
		assertTrue(question.hasComment(commentQuestion));
		assertTrue(answer.hasComment(commentAnswer));
	}

	@Test
	public void shouldHaveTimestamp() {
		assertTrue(commentQuestion.timestamp() != null);
		assertTrue(commentQuestion.timestamp().compareTo(new Date()) <= 0);
	}
	
	@Test 
	public void questionShouldHaveCompleteListOfComments() {
		Comment newComment = question.comment(james, "Blubb");
		List<Comment> comments = question.comments();
		assertTrue(comments.contains(commentQuestion));
		assertTrue(comments.contains(newComment));
		assertEquals(2,comments.size());
	}
	
	@Test 
	public void answerShouldHaveCompleteListOfComments() {
		Comment newComment = answer.comment(james, "Blubb");
		List<Comment> comments = answer.comments();
		assertTrue(comments.contains(commentAnswer));
		assertTrue(comments.contains(newComment));
		assertEquals(2,comments.size());
	}
}
