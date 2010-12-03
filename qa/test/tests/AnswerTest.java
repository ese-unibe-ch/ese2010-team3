package tests;

import java.util.Date;

import models.Answer;
import models.ISystemInformation;
import models.Question;
import models.SystemInformation;
import models.User;

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
		savedSysInfo = SystemInformation.get();
		SystemInformationMock sys = new SystemInformationMock();
		SystemInformation.mockWith(sys);
		james = new User("James", "jack");

		sys.year(2000).month(6).day(6).hour(12).minute(0).second(0);
		questionDate = sys.now();
		sys.changeTo(questionDate);
		question = new Question(new User("Jack", "jack"),
				"Why did the chicken cross the road?");
		sys.minute(5);
		answerDate = sys.now();
		sys.changeTo(answerDate);

		question = new Question(new User("Jack", "jack"),
				"Why did the chicken cross the road?");

		answer = question.answer(james, "To get to the other side.");

	}

	@Test
	public void shouldCreateAnswer() {
		assertTrue(answer != null);
	}

	@Test
	public void shouldHaveCorrectContent() {
		assertEquals(answer.content(), "To get to the other side.");
	}

	@Test
	public void shouldHaveOwner() {
		assertEquals(answer.owner(), james);
	}

	@Test
	public void shouldHaveQuestion() {
		assertEquals(answer.getQuestion(), question);
	}

	@Test
	public void shouldHaveTimestamp() {
		assertEquals(answer.timestamp(), answerDate);
	}

	@Test
	public void shouldRegisterItself() {
		assertTrue(james.hasItem(answer));
		assertTrue(question.hasAnswer(answer));
	}

	@Test
	public void shouldFindAnswer() {
		assertEquals(answer, question.getAnswer(answer.id()));
	}

	@After
	public void tearDown() {
		SystemInformation.mockWith(savedSysInfo);
	}

	@Test
	public void shouldBeHighRated() {
		User a = new User("a", "a");
		User b = new User("b", "b");
		User c = new User("c", "c");
		User d = new User("d", "d");
		User e = new User("e", "e");

		answer.voteUp(a);
		answer.voteUp(b);
		answer.voteUp(c);
		answer.voteUp(d);
		answer.voteUp(e);

		assertTrue(answer.isHighRated());

		a.delete();
		b.delete();
		c.delete();
		d.delete();
		e.delete();

		assertFalse(answer.isHighRated());
	}

	@Test
	public void shouldBeBestAnswer() {
		question.setBestAnswer(answer);

		assertTrue(answer.isBestAnswer());
	}

}