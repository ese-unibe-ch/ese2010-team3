package tests;

import java.util.Date;

import models.Answer;
import models.ISystemInformation;
import models.Question;
import models.SystemInformation;
import models.User;
import models.database.Database;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import play.test.UnitTest;
import tests.mocks.SystemInformationMock;

public class AnswerTest extends UnitTest {

	private User james;
	private Question question;
	private Answer answer;
	private Date questionDate;
	private Date answerDate;
	private ISystemInformation savedSysInfo;

	@Before
	public void setUp() {
		this.savedSysInfo = SystemInformation.get();
		SystemInformationMock sys = new SystemInformationMock();
		SystemInformation.mockWith(sys);
		this.james = new User("James", "jack");

		sys.year(2000).month(6).day(6).hour(12).minute(0).second(0);
		this.questionDate = sys.now();
		sys.changeTo(this.questionDate);
		this.question = new Question(new User("Jack", "jack"),
				"Why did the chicken cross the road?");
		sys.minute(5);
		this.answerDate = sys.now();
		sys.changeTo(this.answerDate);

		this.question = new Question(new User("Jack", "jack"),
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
		assertEquals(this.answer.content(), "To get to the other side.");
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

	@After
	public void tearDown() {
		SystemInformation.mockWith(this.savedSysInfo);
	}

	@Test
	public void shouldBeHighRated() {
		User a = new User("a", "a");
		User b = new User("b", "b");
		User c = new User("c", "c");
		User d = new User("d", "d");
		User e = new User("e", "e");

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
		assertTrue(this.question.isBestAnswerSettable());
		this.question.setBestAnswer(this.answer);
		assertTrue(this.answer.isBestAnswer());
		assertTrue(Database.get().questions().countBestRatedAnswers() > 0);
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
}