package tests;
import models.Answer;
import models.Comment;
import models.Question;
import models.User;
import models.Vote;

import org.junit.Before;
import org.junit.Test;

import play.test.UnitTest;


public class UnregisterTest extends UnitTest {
	
	private User jack;
	private User john;
	private User bill;
	private User kate;
	private User sahra;
	private User michael;
	private Question question;
	private Answer answer;
	private Comment questionComment;
	private Comment answerComment;
	private Vote questionVote;
	private Vote answerVote;

	@Before
	public void setUp() {
		this.jack = new User("Jack", "jack");
		this.john = new User("John", "john");
		this.bill = new User("Bill", "bill");
		this.kate = new User("Kate", "kate");
		this.sahra = new User("Sahra", "sahra");
		this.michael = new User("Michael", "michael");
		this.question = new Question(this.jack, "Why did the chicken cross the road?");
		this.answer = this.question.answer(this.john, "To get to the other side.");
		this.questionVote = this.question.voteUp(this.kate);
		this.answerVote = this.answer.voteDown(this.bill);
		this.questionComment = this.question.comment(this.michael, "Strange question");
		this.answerComment = this.answer.comment(this.sahra, "Good answer");
	}
	
	@Test
	public void shouldUnregisterAnswer() {
		assertTrue(this.question.hasAnswer(this.answer));
		this.john.delete();
		assertFalse(this.question.hasAnswer(this.answer));
	}
	
	@Test
	public void shouldUnregisterAnswersToQuestion() {
		assertTrue(this.john.hasItem(this.answer));
		this.jack.delete();
		assertFalse(this.john.hasItem(this.answer));
	}
	
	@Test
	public void shouldUnregisterVotesOfUser() {
		assertEquals(this.question.upVotes(), 1);
		this.kate.delete();
		assertEquals(this.question.upVotes(), 0);
		
		assertEquals(this.answer.downVotes(), 1);
		this.bill.delete();
		assertEquals(this.answer.downVotes(), 0);
	}
	
	@Test
	public void shouldUnregisterVotesOfEntry() {
		assertTrue(this.kate.hasItem(this.questionVote));
		assertTrue(this.bill.hasItem(this.answerVote));
		this.jack.delete();
		assertFalse(this.kate.hasItem(this.questionVote));
		assertFalse(this.bill.hasItem(this.answerVote));	
	}
	
	@Test
	public void shouldUnregisterCommentsToQuestion() {
		assertTrue(question.hasComment(questionComment));
		this.michael.delete();
		assertFalse(question.hasComment(questionComment));
	}
	
	@Test
	public void shouldUnregisterCommentsToAnswer() {
		assertTrue(answer.hasComment(answerComment));
		this.sahra.delete();
		assertFalse(answer.hasComment(answerComment));
	}

	public void testUserQuestionAnonymization() {
		this.jack.anonymize(false, false);
		this.john.anonymize(false, false);
		
		assertNull(this.question.owner());
		assertEquals(this.question.upVotes(), 1);
		assertEquals(this.answer.owner(), this.john);
		assertEquals(this.answer.downVotes(), 1);
	}
	
	@Test
	public void testUserAnonymization() {
		this.jack.anonymize(true, false);
		this.jack.delete();
		this.john.anonymize(true, false);
		this.john.delete();
		
		assertNull(this.question.owner());
		assertEquals(this.question.upVotes(), 1);
		assertNull(this.answer.owner());
		assertEquals(this.answer.downVotes(), 1);

		assertNotNull(questionComment.owner());
		assertNotNull(answerComment.owner());
		this.michael.anonymize(true, true);
		this.michael.delete();
		this.sahra.anonymize(true, false);
		this.sahra.delete();
		assertTrue(question.hasComment(questionComment));
		assertNull(questionComment.owner());
		assertFalse(answer.hasComment(answerComment));
	}
}
