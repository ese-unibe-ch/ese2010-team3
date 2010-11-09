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
		this.question = new Question(new User("Jack", "jack"), "Why did the chicken cross the road?");
		this.answer = question.answer(new User("James", "james"), "To get to the other side.");
		this.bill = new User("Bill", "bill");
		this.secondAnswer = question.answer(new User("Paul", "paul"), "Because.");
	}
	
	@Test
	public void shoulHaveNoVotes() {
		assertEquals(this.question.upVotes(), 0);
		assertEquals(this.question.downVotes(), 0);
		assertEquals(this.answer.upVotes(), 0);
		assertEquals(this.answer.downVotes(), 0);
	}
	
	@Test
	public void shouldVoteUp() {
		this.question.voteUp(bill);
		this.answer.voteUp(bill);
		assertEquals(this.question.upVotes(), 1);
		assertEquals(this.question.downVotes(), 0);
		assertEquals(this.answer.upVotes(), 1);
		assertEquals(this.answer.downVotes(), 0);
	}
	
	@Test
	public void shouldVoteDown() {
		this.question.voteDown(bill);
		this.answer.voteDown(bill);
		assertEquals(this.question.upVotes(), 0);
		assertEquals(this.question.downVotes(), 1);
		assertEquals(this.answer.upVotes(), 0);
		assertEquals(this.answer.downVotes(), 1);
	}
	
	@Test
	public void shouldCount() {
		for(int i=0; i<11; i++) {
			this.answer.voteUp(new User("up" + i, "pw"));
		}
		for(int i=0; i<42; i++) {
			this.answer.voteDown(new User("down" + i, "pw"));
		}
		assertEquals(this.answer.upVotes(), 11);
		assertEquals(this.answer.downVotes(), 42);
	}
	
	@Test
	public void shouldDeleteOldVote() {
		this.question.voteDown(bill);
		this.question.voteUp(bill);
		this.question.voteUp(bill);
		assertEquals(this.question.upVotes(), 1);
		assertEquals(this.question.downVotes(), 0);
	}
	
	@Test
	public void testBestAnswerSetting() {
		Calendar now = Calendar.getInstance();
		assertTrue(this.question.isBestAnswerSettable(now));
		this.question.setBestAnswer(answer);
		assertEquals(this.question.getBestAnswer(),answer);
		this.question.setBestAnswer(secondAnswer);
		assertEquals(this.question.getBestAnswer(),secondAnswer);
	}
	
	@Test
	public void shouldNotAllowBestAnswerSetAfterOneHour() {
		Calendar now = Calendar.getInstance();

		Calendar inAnHour = (Calendar) now.clone();
		inAnHour.add(Calendar.HOUR, 1);
		
		this.question.setBestAnswer(answer);
		assertTrue(this.question.isBestAnswerSettable(now));
		assertFalse(this.question.isBestAnswerSettable(inAnHour));
		
		assertFalse(this.question.setBestAnswer(secondAnswer,inAnHour));
		assertEquals(this.question.getBestAnswer(),answer);
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
