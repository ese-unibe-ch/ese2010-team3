package tests;

import models.Answer;
import models.Comment;
import models.Question;
import models.User;
import models.Vote;

import org.junit.Before;
import org.junit.Test;

public class UnregisterTest extends MockedUnitTest {

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
		jack = new User("Jack");
		john = new User("John");
		bill = new User("Bill");
		kate = new User("Kate");
		sahra = new User("Sahra");
		michael = new User("Michael");
		question = new Question(jack, "Why did the chicken cross the road?");
		answer = question.answer(john, "To get to the other side.");
		questionVote = question.voteUp(kate);
		answerVote = answer.voteDown(bill);
		questionComment = question.comment(michael, "Strange question");
		answerComment = answer.comment(sahra, "Good answer");
	}

	@Test
	public void shouldUnregisterAnswer() {
		assertTrue(question.hasAnswer(answer));
		john.delete();
		assertFalse(question.hasAnswer(answer));
	}

	@Test
	public void shouldUnregisterAnswersToQuestion() {
		assertTrue(john.hasItem(answer));
		jack.delete();
		assertFalse(john.hasItem(answer));
	}

	@Test
	public void shouldUnregisterVotesOfUser() {
		assertEquals(question.upVotes(), 1);
		kate.delete();
		assertEquals(question.upVotes(), 0);

		assertEquals(answer.downVotes(), 1);
		bill.delete();
		assertEquals(answer.downVotes(), 0);
	}

	@Test
	public void shouldUnregisterVotesOfEntry() {
		assertTrue(kate.hasItem(questionVote));
		assertTrue(bill.hasItem(answerVote));
		jack.delete();
		assertFalse(kate.hasItem(questionVote));
		assertFalse(bill.hasItem(answerVote));
	}

	@Test
	public void shouldUnregisterCommentsToQuestion() {
		assertTrue(question.hasComment(questionComment));
		michael.delete();
		assertFalse(question.hasComment(questionComment));
	}

	@Test
	public void shouldUnregisterCommentsToAnswer() {
		assertTrue(answer.hasComment(answerComment));
		sahra.delete();
		assertFalse(answer.hasComment(answerComment));
	}

	@Test
	public void shouldDeleteAllCommentsOnQuestionDelete() {
		assertNotNull(questionComment.getQuestion());
		question.delete();
		assertNull(questionComment.getQuestion());
	}

	@Test
	public void shouldDeleteAllCommentsOnAnswerDelete() {
		assertNotNull(answerComment.getQuestion());
		answer.delete();
		assertNull(answerComment.getQuestion());
	}

	@Test
	public void testUserQuestionAnonymization() {
		jack.anonymize(false);
		john.anonymize(false);

		assertNull(question.owner());
		assertEquals(question.upVotes(), 1);
		assertEquals(answer.owner(), john);
		assertEquals(answer.downVotes(), 1);
	}

	@Test
	public void testUserAnonymization() {
		jack.anonymize(true);
		jack.delete();
		john.anonymize(true);
		john.delete();

		assertNull(question.owner());
		assertEquals(question.upVotes(), 1);
		assertNull(answer.owner());
		assertEquals(answer.downVotes(), 1);

		assertNotNull(questionComment.owner());
		assertNotNull(answerComment.owner());
		michael.anonymize(true);
		michael.delete();
		sahra.anonymize(false);
		sahra.delete();
		assertTrue(question.hasComment(questionComment));
		assertNull(questionComment.owner());
		assertFalse(answer.hasComment(answerComment));
	}

	@Test
	public void shouldAllowRepeatedUnregistration() {
		answer.delete();
		answer.delete();

		jack.anonymize(true);
		question.delete();
		question.delete();
	}
}
