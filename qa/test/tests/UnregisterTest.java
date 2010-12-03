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
		jack = new User("Jack", "jack", "jack@jack.com");
		john = new User("John", "john", "john@john.com");
		bill = new User("Bill", "bill", "bill@bill.com");
		kate = new User("Kate", "kate", "kate@kate.com");
		sahra = new User("Sahra", "sahra", "sahra@sahra.com");
		michael = new User("Michael", "michael", "michel@michael.com");
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
		assertTrue(questionComment.isRegistered());
		question.unregister();
		assertFalse(questionComment.isRegistered());
	}

	@Test
	public void shouldDeleteAllCommentsOnAnswerDelete() {
		assertTrue(answerComment.isRegistered());
		answer.unregister();
		assertFalse(answerComment.isRegistered());
	}

	@Test
	public void testUserQuestionAnonymization() {
		jack.anonymize(false, false);
		john.anonymize(false, false);

		assertNull(question.owner());
		assertEquals(question.upVotes(), 1);
		assertEquals(answer.owner(), john);
		assertEquals(answer.downVotes(), 1);
	}

	@Test
	public void testUserAnonymization() {
		jack.anonymize(true, false);
		jack.delete();
		john.anonymize(true, false);
		john.delete();

		assertNull(question.owner());
		assertEquals(question.upVotes(), 1);
		assertNull(answer.owner());
		assertEquals(answer.downVotes(), 1);

		assertNotNull(questionComment.owner());
		assertNotNull(answerComment.owner());
		michael.anonymize(true, true);
		michael.delete();
		sahra.anonymize(true, false);
		sahra.delete();
		assertTrue(question.hasComment(questionComment));
		assertNull(questionComment.owner());
		assertFalse(answer.hasComment(answerComment));
	}
}
