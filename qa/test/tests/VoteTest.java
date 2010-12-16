package tests;

import java.util.Calendar;

import models.Answer;
import models.Question;
import models.User;

import org.junit.Before;
import org.junit.Test;

public class VoteTest extends MockedUnitTest {

	private Question question;
	private Answer answer;
	private User bill;
	private Answer secondAnswer;

	@Before
	public void setUp() {
		question = new Question(new User("Jack", "jack"),
				"Why did the chicken cross the road?");
		answer = question.answer(new User("James", "james"),
				"To get to the other side.");
		bill = new User("Bill", "bill");
		secondAnswer = question.answer(new User("Paul", "paul"), "Because.");
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
		assertEquals(question.getVotes().size(), 1);

		assertEquals(answer.upVotes(), 1);
		assertEquals(answer.downVotes(), 0);
		assertTrue(answer.hasUpVote(bill));
		assertFalse(answer.hasUpVote(answer.owner()));
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
		assertFalse(answer.hasUpVote(bill));
	}

	@Test
	public void shouldCount() {
		for (int i = 0; i < 11; i++) {
			answer.voteUp(new User("up" + i, "pw"));
		}
		for (int i = 0; i < 42; i++) {
			answer.voteDown(new User("down" + i, "down"));
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
		assertTrue(this.answer.compareTo(this.secondAnswer) < 0);
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

	@Test
	public void shouldNotLetVoteForOneself() {
		assertNull(this.question.voteUp(this.question.owner()));
		assertEquals(this.question.rating(), 0);
	}

	@Test
	public void shouldCancelIdempotently() {
		this.answer.voteUp(bill);
		this.answer.voteCancel(bill);
		this.answer.voteCancel(bill);
	}
}
