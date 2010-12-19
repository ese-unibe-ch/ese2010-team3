package tests;

import models.Question;
import models.User;
import models.helpers.Tools;

import org.junit.Before;
import org.junit.Test;

public class QuestionTest extends MockedUnitTest {
	private User user;
	private Question question;

	@Before
	public void setUp() {
		this.user = new User("Jack");
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
		assertEquals(question.getContentText(),
				"Why did the chicken cross the road?");
		assertEquals(question.content(),
				"<p>Why did the chicken cross the road?</p>");
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
		assertEquals(this.extractWordString(""), "");
		assertEquals(this.extractWordString("a b"), "");
		assertEquals(this.extractWordString("a bc"), "");
		assertEquals(this.extractWordString("ab"), "");
		assertEquals(this.extractWordString("a "), "");
		assertEquals(this.extractWordString("abcde"), "");
		assertEquals(this.extractWordString("abcd abcd abcd abcd d"), "abcd");
		// "some" is a StopWord and should not be suggested
		assertEquals(this.extractWordString("some some some some s"), "");
		String txt = "asdf asdf asdf asdf qwer qwer qwer qwer yxcv yxcv yxcv";
		assertEquals(this.extractWordString(txt), "asdf qwer yxcv");
		txt += " hello hello hello hello";
		assertEquals(this.extractWordString(txt), "asdf hello qwer yxcv");
		txt += "mnbv text mnbv text mnbv text mnbv text asdf hello mnbv";
		// remove "yxcv" because there are more important words
		assertEquals(this.extractWordString(txt),
				"asdf hello mnbv qwer text");
	}

	private String extractWordString(String words) {
		String result = "";
		for (String string : Tools.extractImportantWords(words)) {
			result += " " + string;
		}
		if (result.length() > 0) {
			result = result.substring(1);
		}
		return result;
	}

	@Test
	public void shouldLockQuestion() {
		question.lock();
		assertTrue(question.isLocked());
		question.unlock();
		assertFalse(question.isLocked());
	}

}
