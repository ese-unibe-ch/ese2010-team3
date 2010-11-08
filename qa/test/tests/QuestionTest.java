package tests;

import models.ISystemInformation;
import models.Question;
import models.SystemInformation;
import models.User;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import play.test.UnitTest;
import tests.mocks.SystemInformationMock;

public class QuestionTest extends UnitTest {
	private User user;
	private Question question;
	private ISystemInformation savedSysInfo;

	@Before
	public void setUp() {
		savedSysInfo = SystemInformation.get();
		this.user = new User("Jack", "jack");
		this.question = new Question(user,
				"Why did the chicken cross the road?");
	}

	@Test
	public void shouldCreateQuestion() {
		assertNotNull(question);
	}

	@Test
	public void shouldHaveOwner() {
		assertEquals(question.owner(), user);
	}

	@Test
	public void shouldHaveCorrectContent() {
		assertEquals(question.content(), "Why did the chicken cross the road?");
	}

	@Test
	public void shouldHaveCorrectSummary() {
		assertEquals(question.summary(), "Why did the chicken cross the road?");
		Question longerQuestion = new Question(this.user,
				"Why did   the chicken\ncross the road\tagain and again?");
		assertEquals(longerQuestion.summary(),
				"Why did the chicken cross the road again ...");
	}

	@Test
	public void shouldRegisterItself() {
		assertTrue(user.hasItem(question));
	}

	@Test
	public void shouldBeOldQuestion() {
		User user2 = new User("User2", "user2");
		SystemInformationMock sys = new SystemInformationMock();
		SystemInformation.mockWith(sys);
		sys.year(2000).month(6).day(6).hour(12).minute(0).second(0);

		Question oldQuestion = new Question(user2, "Why?");
		sys.year(2001);
		assertTrue(oldQuestion.isOldQuestion());
	}

	@Test
	public void shouldNotBeOldQuestion() {
		User user2 = new User("User2", "user2");
		SystemInformationMock sys = new SystemInformationMock();
		SystemInformation.mockWith(sys);
		sys.year(2000).month(6).day(6).hour(12).minute(0).second(0);

		Question oldQuestion = new Question(user2, "Why?");
		sys.year(2000).month(9);
		assertFalse(oldQuestion.isOldQuestion());
	}

	@After
	public void tearDown() {
		SystemInformation.mockWith(this.savedSysInfo);
	}
}
