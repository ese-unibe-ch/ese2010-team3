package tests;

import java.util.Date;

import models.Answer;
import models.Question;
import models.User;
import models.database.IQuestionDatabase;
import models.database.HotDatabase.HotQuestionDatabase;

import org.junit.Before;
import org.junit.Test;

public class AnswerTest extends MockedUnitTest {

	private User james;
	private Question question;
	private Answer answer;
	private Date questionDate;
	private Date answerDate;
	private IQuestionDatabase questionDB;

	@Before
	public void setUp() {
		this.james = new User("James");

		sysInfo.year(2000).month(6).day(6).hour(12).minute(0).second(0);
		this.questionDate = sysInfo.now();
		sysInfo.changeTo(this.questionDate);
		this.question = new Question(new User("Jack"),
				"Why did the chicken cross the road?");
		sysInfo.minute(5);
		this.answerDate = sysInfo.now();
		sysInfo.changeTo(this.answerDate);

		this.questionDB = new HotQuestionDatabase(null);
		this.question = this.questionDB.add(new User("Jack"),
				"Why did the chicken cross the road?");

		this.answer = this.question.answer(this.james,
				"To get to the other side.");

	}

	@Test
	public void shouldCreateAnswer() {
		assertTrue(this.answer != null);
	}

	@Test
	public void shouldHaveCorrectContent() {
		assertEquals(this.answer.content(), "<p>To get to the other side.</p>");
		assertEquals(this.answer.getContentText(), "To get to the other side.");
	}

	@Test
	public void shouldHaveOwner() {
		assertEquals(this.answer.owner(), this.james);
	}

	@Test
	public void shouldHaveQuestion() {
		assertEquals(this.answer.getQuestion(), this.question);
	}

	@Test
	public void shouldHaveTimestamp() {
		assertEquals(this.answer.timestamp(), this.answerDate);
	}

	@Test
	public void shouldRegisterItself() {
		assertTrue(this.james.hasItem(this.answer));
		assertTrue(this.question.hasAnswer(this.answer));
	}

	@Test
	public void shouldFindAnswer() {
		assertEquals(this.answer, this.question.getAnswer(this.answer.id()));
	}

	@Test
	public void shouldBeHighRated() {
		User a = new User("a");
		User b = new User("b");
		User c = new User("c");
		User d = new User("d");
		User e = new User("e");

		this.answer.voteUp(a);
		this.answer.voteUp(b);
		this.answer.voteUp(c);
		this.answer.voteUp(d);
		this.answer.voteUp(e);

		assertTrue(this.answer.isHighRated());

		a.delete();
		b.delete();
		c.delete();
		d.delete();
		e.delete();

		assertFalse(this.answer.isHighRated());
	}

	@Test
	public void shouldBeBestAnswer() {
		assertEquals(0, this.questionDB.countBestRatedAnswers());
		assertTrue(this.question.isBestAnswerSettable());
		this.question.setBestAnswer(this.answer);
		assertTrue(this.answer.isBestAnswer());
		assertEquals(1, this.questionDB.countBestRatedAnswers());
	}

	@Test
	public void shouldCompareToQuestion() {
		assertEquals(this.answer.compareTo(this.question), 1);
	}

	@Test
	public void shouldNotClaimtoBelongtoQuestion() {
		this.question.unregister(this.answer);
		assertNull(this.answer.getQuestion());
	}

	@Test
	public void shouldDefaultToNoBestAnswers() {
		this.questionDB.clear();
		assertEquals(0, this.questionDB.countBestRatedAnswers());
	}
}