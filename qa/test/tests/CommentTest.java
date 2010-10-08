package tests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import models.*;
import play.test.UnitTest;

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
	
	
	
	
	//TODO @Tobias: more Tests

}
