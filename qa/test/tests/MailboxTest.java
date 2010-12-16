package tests;

import java.util.List;

import models.Answer;
import models.IMailbox;
import models.Notification;
import models.Question;
import models.User;
import models.database.Database;

import org.junit.Before;
import org.junit.Test;

public class MailboxTest extends MockedUnitTest {
	private User pete;
	private User susane;
	private IMailbox mailbox;
	private Question question;

	@Before
	public void setUp() {
		Database.clear();
		this.pete = new User("Pete");
		this.susane = new User("Susane");
		this.mailbox = Database.users().getModeratorMailbox();
		this.pete.setModerator(true);
		this.susane.setModerator(true);
		this.question = new Question(this.susane, "ORLY?");
		sysInfo.year(2000).month(4).day(2).hour(9).minute(0).second(0);
	}

	@Test
	public void testReceive() {
		assertEquals(this.mailbox.getName(), "Moderators");
		new Notification(this.mailbox, this.question);
	}

	@Test
	public void testGetAll() {
		new Notification(this.mailbox, this.question);
		List<Notification> petesNotifications = this.pete.getNotifications();
		List<Notification> susanesNotifications = this.susane
				.getNotifications();
		assertEquals(1, petesNotifications.size());
		assertEquals(petesNotifications.get(0).getAbout(), this.question);
		assertEquals(1, susanesNotifications.size());
		assertEquals(susanesNotifications.get(0).getAbout(), this.question);
	}

	@Test
	public void testGetRecent() {
		new Notification(this.mailbox, this.question);
		List<Notification> petesNotifications = this.pete
				.getRecentNotifications();
		List<Notification> susanesNotifications = this.susane
				.getRecentNotifications();
		assertEquals(1, petesNotifications.size());
		assertEquals(petesNotifications.get(0).getAbout(), this.question);
		assertEquals(1, susanesNotifications.size());
		assertEquals(susanesNotifications.get(0).getAbout(), this.question);

		sysInfo.minute(15);
		petesNotifications = this.pete.getRecentNotifications();
		susanesNotifications = this.susane.getRecentNotifications();
		assertEquals(0, petesNotifications.size());
		assertEquals(0, susanesNotifications.size());
	}

	@Test
	public void testGetNew() {
		new Notification(this.mailbox, this.question);
		List<Notification> petesNotifications = this.pete.getNewNotifications();
		List<Notification> susanesNotifications = this.susane
				.getNewNotifications();

		assertEquals(1, petesNotifications.size());
		assertEquals(petesNotifications.get(0).getAbout(), this.question);
		assertEquals(1, susanesNotifications.size());
		assertEquals(susanesNotifications.get(0).getAbout(), this.question);

		petesNotifications.get(0).unsetNew();

		petesNotifications = this.pete.getNewNotifications();
		susanesNotifications = this.susane.getNewNotifications();

		assertEquals(0, petesNotifications.size());
		assertEquals(0, susanesNotifications.size());
	}

	@Test
	public void testPersonalMailbox() {
		Answer answer = this.question.answer(this.susane, "answer?");
		this.pete.observe(this.question, answer);
		List<Notification> petesNotifications = this.pete
				.getMyNewNotifications();
		List<Notification> susanesNotifications = this.susane
				.getMyNewNotifications();
		assertEquals(1, petesNotifications.size());
		assertEquals(petesNotifications.get(0).getAbout(), answer);
		assertEquals(0, susanesNotifications.size());
		petesNotifications = this.pete
				.getMyRecentNotifications();
		susanesNotifications = this.susane
				.getMyRecentNotifications();
		assertEquals(1, petesNotifications.size());
		assertEquals(petesNotifications.get(0).getAbout(), answer);
		assertEquals(0, susanesNotifications.size());
	}

	@Test
	public void shouldHaveNiceDebuggingToString() {
		assertEquals(this.mailbox.toString(), "MB[" + this.mailbox.getName()
				+ "(" + this.mailbox.getAllNotifications().size() + ")" + "]");
	}
}
