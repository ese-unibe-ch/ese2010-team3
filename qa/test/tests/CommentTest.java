package tests;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import models.*;
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
		this.james = new User("James");
		this.question = new Question(new User("Jack"), "Why did the chicken cross the road?");
		this.answer = this.question.answer(james, "To get to the other side.");
		this.commentQuestion = this.question.comment(james, "Strange Question!");
		this.commentAnswer = this.answer.comment(new User("Jill"), "Good point!");
		
	}
	
	@Test
	public void shouldCreateComments() {
		assertTrue(commentAnswer != null);
		assertTrue(commentQuestion != null);
	}
	
	@Test
	public void shouldReturnCorrectType() {
		assertTrue(question.getComment(commentQuestion.id()).type().equals("Comment"));
		assertTrue(answer.getComment(commentAnswer.id()).type().equals("Comment"));
		
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
	//TODO @Tobias: more Tests

}
