package tests;

import models.ISystemInformation;
import models.Question;
import models.SystemInformation;
import models.User;
import models.helpers.Tools;

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
		Question longerQuestion = new Question(user,
				"Why did   the chicken\ncross the road\tagain and again?");
		assertEquals(longerQuestion.summary(),
				"Why did the chicken cross the road again and again?");
		String longQuestion = "bla ";
		while (longQuestion.length() < 1024)
			longQuestion += longQuestion;
		longerQuestion = new Question(user, longQuestion);
		assertTrue(longerQuestion.summary().endsWith("bla ..."));
	}

	@Test
	public void shouldRegisterItself() {
		assertTrue(user.hasItem(question));
	}

	@Test
	public void getOnlyImportantWords() {
		assertEquals(Tools.extractImportantWords(""), "");
		assertEquals(Tools.extractImportantWords("abcde"), "");
		assertEquals(Tools.extractImportantWords("abcd abcd abcd abcd d"),
				"abcd");
		// "some" is a StopWord and should not be suggested
		assertEquals(Tools.extractImportantWords("some some some some s"), "");
		String txt = "asdf asdf asdf asdf qwer qwer qwer qwer yxcv yxcv yxcv";
		assertEquals(Tools.extractImportantWords(txt), "yxcv qwer asdf");
		txt += " hello hello hello hello";
		assertEquals(Tools.extractImportantWords(txt), "hello yxcv qwer asdf");
		txt += "mnbv text mnbv text mnbv text mnbv text ";
		// remove "yxcv" because there are more important words
		assertEquals(Tools.extractImportantWords(txt),
				"mnbv hello text qwer asdf");
	}

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
	
	@Test
	public void shouldLockQuestion() {
		question.lock();
		assertTrue(question.isLocked());
		question.unlock();
		assertFalse(question.isLocked());
	}

}
