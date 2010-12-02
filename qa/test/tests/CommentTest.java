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
		james = new User("James", "james", "james@james.com");
		question = new Question(new User("Jack", "jack", "jack@jack.com"),
				"Why did the chicken cross the road?");
		answer = question.answer(james, "To get to the other side.");
		generateComments();
	}

	private void generateComments() {
		commentQuestion = question.comment(james, "Strange Question!");
		commentAnswer = answer.comment(new User("Jill", "jill", "jill@jill.com"), "Good point!");
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
		assertEquals(2, comments.size());
	}

	@Test
	public void answerShouldHaveCompleteListOfComments() {
		Comment newComment = answer.comment(james, "Blubb");
		List<Comment> comments = answer.comments();
		assertTrue(comments.contains(commentAnswer));
		assertTrue(comments.contains(newComment));
		assertEquals(2, comments.size());
	}

	@Test
	public void shouldGetQuestionBelongToQuestioncomment() {
		Comment newComment = question.comment(james, "Blubb");
		assertEquals(newComment.getQuestion(), question);
	}

	@Test
	public void shouldGetQuestionBelongToAnswercomment() {
		Comment newComment = answer.comment(james, "Blubb");
		assertEquals(newComment.getQuestion(), question);
	}
	
	@Test
	public void shouldAddUserToLikers() {
		assertTrue(commentAnswer.getLikers().isEmpty());
		commentAnswer.addLiker(james);
		assertEquals(james,commentAnswer.getLikers().get(0));
	}
	
	@Test
	public void shouldRemoveUserFromLikers() {
		commentAnswer.addLiker(james);
		assertEquals(1, commentAnswer.getLikers().size());
		commentAnswer.removeLiker(james);
		assertTrue(commentAnswer.getLikers().isEmpty());
	}
	
	@Test
	public void shouldClearLikersList() {
		commentAnswer.addLiker(james);
		commentAnswer.addLiker(new User("anonym", "1234", "an@an.com"));
		assertTrue(commentAnswer.getLikers().size() > 0);
		commentAnswer.clearAllLikers();
		assertTrue(commentAnswer.getLikers().isEmpty());
	}
	
	@Test
	public void shouldCountLikersRight() {
		assertEquals(0,commentAnswer.countLikers());
		commentAnswer.addLiker(james);
		assertEquals(1,commentAnswer.countLikers());
	}
}
