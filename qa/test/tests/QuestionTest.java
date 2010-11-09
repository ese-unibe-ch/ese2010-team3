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
		assertEquals(Question.importantWords("abcd abcd abcd abcd abcd d"),
				"abcd");
		String txt = "asdf qwer dyxcv asdf k qwer l yxcv asdf qwer asdf yxcv qwer yxcv qwer yxcv";
		assertEquals(Question.importantWords(txt), "yxcv qwer asdf");
		txt += " hello hello hello hello hello";
		assertEquals(Question.importantWords(txt), "hello yxcv qwer asdf");
		txt += " more text more text more text more text and a lot of more text";
		// remove yxcv from importantWords because there are more important
		// Words
		assertEquals(Question.importantWords(txt), "hello text more qwer asdf");
	}
	
	@Test
	public void shouldLockQuestion() {
		question.lock();
		assertTrue(question.isLocked());
		question.unlock();
		assertFalse(question.isLocked());
	}

}
