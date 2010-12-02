package tests;

import java.util.Calendar;

import models.Answer;
import models.Question;
import models.User;

import org.junit.Before;
import org.junit.Test;

import play.test.UnitTest;

public class VoteTest extends UnitTest {

	private Question question;
	private Answer answer;
	private User bill;
	private Answer secondAnswer;

	@Before
	public void setUp() {
		question = new Question(new User("Jack", "jack", "jack@jack.com"),
				"Why did the chicken cross the road?");
		answer = question.answer(new User("James", "james", "james@james.com"),
				"To get to the other side.");
		bill = new User("Bill", "bill", "bill@bill.com");
		secondAnswer = question.answer(new User("Paul", "paul", "paul@paul.com"), "Because.");
	}

	@Test
	public void shoulHaveNoVotes() {
		assertEquals(question.upVotes(), 0);
		assertEquals(question.downVotes(), 0);
		assertEquals(answer.upVotes(), 0);
		assertEquals(answer.downVotes(), 0);
	}

	@Test
	public void shouldVoteUp() {
		question.voteUp(bill);
		answer.voteUp(bill);
		assertEquals(question.upVotes(), 1);
		assertEquals(question.downVotes(), 0);
		assertTrue(question.hasUpVote(bill));
		assertEquals(answer.upVotes(), 1);
		assertEquals(answer.downVotes(), 0);
		assertTrue(answer.hasUpVote(bill));
	}

	@Test
	public void shouldVoteDown() {
		question.voteDown(bill);
		answer.voteDown(bill);
		assertEquals(question.upVotes(), 0);
		assertEquals(question.downVotes(), 1);
		assertTrue(question.hasDownVote(bill));
		assertEquals(answer.upVotes(), 0);
		assertEquals(answer.downVotes(), 1);
		assertTrue(answer.hasDownVote(bill));
	}

	@Test
	public void shouldCount() {
		for (int i = 0; i < 11; i++) {
			answer.voteUp(new User("up" + i, "pw", "up@up.com"));
		}
		for (int i = 0; i < 42; i++) {
			answer.voteDown(new User("down" + i, "pw", "down@down.com"));
		}
		assertEquals(answer.upVotes(), 11);
		assertEquals(answer.downVotes(), 42);
	}

	@Test
	public void shouldDeleteOldVote() {
		question.voteDown(bill);
		question.voteUp(bill);
		question.voteUp(bill);
		assertEquals(question.upVotes(), 1);
		assertEquals(question.downVotes(), 0);
		assertTrue(question.hasUpVote(bill));
		assertFalse(question.hasDownVote(bill));

		question.voteCancel(bill);
		assertEquals(question.upVotes(), 0);
		assertEquals(question.downVotes(), 0);
		assertFalse(question.hasUpVote(bill));
		assertFalse(question.hasDownVote(bill));
	}

	@Test
	public void testBestAnswerSetting() {
		Calendar now = Calendar.getInstance();
		assertTrue(question.isBestAnswerSettable(now));
		question.setBestAnswer(answer);
		assertEquals(question.getBestAnswer(), answer);
		question.setBestAnswer(secondAnswer);
		assertEquals(question.getBestAnswer(), secondAnswer);
	}

	@Test
	public void shouldNotAllowBestAnswerSetAfterOneHour() {
		Calendar now = Calendar.getInstance();

		Calendar inAnHour = (Calendar) now.clone();
		inAnHour.add(Calendar.HOUR, 1);

		question.setBestAnswer(answer);
		assertTrue(question.isBestAnswerSettable(now));
		assertFalse(question.isBestAnswerSettable(inAnHour));

		assertFalse(question.setBestAnswer(secondAnswer, inAnHour));
		assertEquals(question.getBestAnswer(), answer);
	}

	@Test
	public void shouldSortCorrectly() {
		// for equal ratings, sort the older answer first
		assertEquals(this.answer.compareTo(this.secondAnswer), -1);
		this.answer.voteUp(bill);
		assertEquals(this.answer.compareTo(this.secondAnswer), -1);
		assertSame(this.question.answers().get(0), this.answer);
		this.answer.voteDown(bill);
		assertEquals(this.answer.compareTo(this.secondAnswer), 1);
		assertNotSame(this.question.answers().get(0), this.answer);
		this.question.setBestAnswer(this.answer);
		assertEquals(this.answer.compareTo(this.secondAnswer), -1);
		assertSame(this.question.answers().get(0), this.answer);
		this.question.setBestAnswer(this.secondAnswer);
		assertEquals(this.answer.compareTo(this.secondAnswer), 1);
		assertSame(this.question.answers().get(0), this.secondAnswer);
	}
}
