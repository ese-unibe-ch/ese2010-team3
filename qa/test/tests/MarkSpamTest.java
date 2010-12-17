package tests;

import models.IMailbox;
import models.Mailbox;
import models.Question;
import models.User;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MarkSpamTest extends MockedUnitTest {

	private User alex;
	private User pete;
	private Question question;
	private Question otherQuestion;
	private IMailbox moderatorBox;

	@Before
	public void setUp() {
		this.moderatorBox = new Mailbox("Moderators");
		this.alex = new User("Alex");
		this.alex.setModerator(true, moderatorBox);
		this.pete = new User("Pete");
		this.question = new Question(this.pete, "SPAMSPAMSPAM!!!1eleven");
		this.otherQuestion = new Question(this.pete, "MOAR SPAM!!!!");
	}

	@After
	public void tearDown() {
		this.alex.delete();
		this.pete.delete();
	}

	@Test
	public void shouldInformModerator() {
		this.question.markSpam(this.moderatorBox);
		assertTrue(this.question.isPossiblySpam());
		assertEquals(1, this.alex.getNotifications().size());
	}

	@Test
	public void shouldDeleteSpam() {
		this.question.markSpam(null);
		this.question.confirmSpam();
		assertNull(this.question.owner());
		assertTrue(this.pete.isBlocked());
	}

	@Test
	public void shouldEaseProcess() {
		this.question.markSpam(this.moderatorBox);
		this.question.confirmSpam();
		this.otherQuestion.markSpam(this.moderatorBox);
		assertEquals(this.alex.getNotifications().size(), 0);
		assertNull(this.otherQuestion.owner());
	}

	@Test
	public void shouldntBotherNonMods() {
		this.question.markSpam(this.moderatorBox);
		this.question.markSpam(this.moderatorBox);
		this.question.confirmSpam();
		this.otherQuestion.markSpam(this.moderatorBox);
		this.alex.setModerator(false, null);
		assertEquals(this.alex.getNotifications().size(), 0);
	}

	@Test
	public void shouldNotReblockUser() {
		this.pete.block("for testing");
		assertFalse(this.pete.isSpammer());
		this.question.markSpam(null);
		this.question.confirmSpam();
		assertTrue(this.pete.isSpammer());
		assertEquals("for testing", this.pete.getStatusMessage());
	}

	@Test
	public void shouldNotUnblockRedeemedSpammer() {
		shouldNotReblockUser();
		this.pete.setIsSpammer(false);
		assertFalse(this.pete.isSpammer());
		assertTrue(this.pete.isBlocked());
		assertEquals("for testing", this.pete.getStatusMessage());
	}
}
