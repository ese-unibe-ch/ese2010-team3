package tests;

import models.Question;
import models.User;
import models.helpers.Tools;

import org.junit.Before;
import org.junit.Test;

import play.test.UnitTest;

public class QuestionTest extends UnitTest {
	private User user;
	private Question question;

	@Before
	public void setUp() {
		user = new User("Jack", "jack");
		question = new Question(user, "Why did the chicken cross the road?");
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
				"Why did the chicken cross the road again ...");
	}

	@Test
	public void shouldRegisterItself() {
		assertTrue(user.hasItem(question));
	}

	@Test
	public void getOnlyImportantWords() {
		assertEquals(Tools.extractImportantWords(""), "");
		assertEquals(Tools.extractImportantWords("abcde"), "");
		assertEquals(Tools.extractImportantWords("abcd abcd abcd abcd abcd d"),
				"abcd");
		String txt = "asdf qwer dyxcv asdf k qwer l yxcv asdf qwer asdf yxcv qwer yxcv qwer yxcv";
		assertEquals(Tools.extractImportantWords(txt), "yxcv qwer asdf");
		txt += " hello hello hello hello hello";
		assertEquals(Tools.extractImportantWords(txt), "hello yxcv qwer asdf");
		txt += " more text more text more text more text and a lot of more text";
		// remove yxcv from importantWords because there are more important
		// Words
		assertEquals(Tools.extractImportantWords(txt),
				"hello text more qwer asdf");
	}

}
