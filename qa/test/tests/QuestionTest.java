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
		assertEquals(question.summary(), "Why did the chicken ...");
	}

	@Test
	public void shouldRegisterItself() {
		assertTrue(user.hasItem(question));
	}

}
