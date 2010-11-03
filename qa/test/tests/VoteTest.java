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
		assertEquals(answer.upVotes(), 1);
		assertEquals(answer.downVotes(), 0);
	}

	@Test
	public void shouldVoteDown() {
		question.voteDown(bill);
		answer.voteDown(bill);
		assertEquals(question.upVotes(), 0);
		assertEquals(question.downVotes(), 1);
		assertEquals(answer.upVotes(), 0);
		assertEquals(answer.downVotes(), 1);
	}

	@Test
	public void shouldCount() {
		for (int i = 0; i < 11; i++) {
			answer.voteUp(new User("up" + i, "pw"));
		}
		for (int i = 0; i < 42; i++) {
			answer.voteDown(new User("down" + i, "pw"));
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
		assertEquals(answer.compareTo(secondAnswer), 0);
		answer.voteUp(bill);
		assertEquals(answer.compareTo(secondAnswer), -1);
		assertSame(question.answers().get(0), answer);
		answer.voteDown(bill);
		assertEquals(answer.compareTo(secondAnswer), 1);
		assertNotSame(question.answers().get(0), answer);
		question.setBestAnswer(answer);
		assertEquals(answer.compareTo(secondAnswer), -1);
		assertSame(question.answers().get(0), answer);
	}
}
