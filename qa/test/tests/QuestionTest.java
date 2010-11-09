package tests;

import models.Question;
import models.User;

import org.junit.Before;
import org.junit.Test;

import play.test.UnitTest;

public class QuestionTest extends UnitTest {
	private User user;
	private Question question;

	@Before
	public void setUp() {
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
	public void getOnlyImportantWords() {
		assertEquals(Question.importantWords(""), "");
		assertEquals(Question.importantWords("abcde"), "");
		assertEquals(Question.importantWords("abcd abcd abcd abcd d"), "abcd");
		// "more" is a StopWord and should not be suggested
		assertEquals(Question.importantWords("some some some some s"), "");
		String txt = "asdf asdf asdf asdf qwer qwer qwer qwer yxcv yxcv yxcv";
		assertEquals(Question.importantWords(txt), "yxcv qwer asdf");
		txt += " hello hello hello hello";
		assertEquals(Question.importantWords(txt), "hello yxcv qwer asdf");
		txt += "mnbv text mnbv text mnbv text mnbv text ";
		// remove "yxcv" because there are more important words
		assertEquals(Question.importantWords(txt), "mnbv hello text qwer asdf");
		txt += " more text more text more text more text and a lot of more text";
	}

}
