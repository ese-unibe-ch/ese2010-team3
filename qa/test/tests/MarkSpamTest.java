package tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import models.Question;
import models.User;

import org.junit.Before;
import org.junit.Test;

import play.test.UnitTest;

public class MarkSpamTest extends UnitTest {

	private User alex;
	private User pete;
	private Question question;
	private Question otherQuestion;

	@Before
	public void setUp() throws Exception {
		this.alex = new User("Alex", "123");
		this.alex.setModerator(true);
		this.pete = new User("Pete", "789");
		this.question = new Question(this.pete, "SPAMSPAMSPAM!!!1eleven");
		this.otherQuestion = new Question(this.pete, "MOAR SPAM!!!!");
	}

	@Test
	public void shouldInformModerator() {
		this.question.markSpam();
		assertTrue(this.question.isPossiblySpam());
		assertEquals(1, this.alex.getNotifications().size());
	}

	@Test
	public void shouldDeleteSpam() {
		this.question.markSpam();
		this.question.confirmSpam();
		assertTrue(this.question.isDeleted());
		assertTrue(this.pete.isBlocked());
	}

	@Test
	public void shouldEaseProcess() {
		shouldDeleteSpam();
		this.otherQuestion.markSpam();
		assertEquals(this.alex.getNotifications().size(), 1);
		assertTrue(this.otherQuestion.isDeleted());
	}

	@Test
	public void shouldntBotherNonMods() {
		shouldEaseProcess();
		this.alex.setModerator(false);
		assertEquals(this.alex.getNotifications().size(), 0);
	}
}
