package tests;

import java.util.List;

import models.Answer;
import models.IMailbox;
import models.Mailbox;
import models.Notification;
import models.Question;
import models.User;

import org.junit.Before;
import org.junit.Test;

public class MailboxTest extends MockedUnitTest {
	private User pete;
	private User susane;
	private IMailbox mailbox;
	private Question question;

	@Before
	public void setUp() {
		this.pete = new User("Pete");
		this.susane = new User("Susane");
		this.mailbox = new Mailbox("We're married");
		this.pete.setModerator(true, this.mailbox);
		this.susane.setModerator(true, this.mailbox);
		this.question = new Question(this.susane, "ORLY?");
		sysInfo.year(2000).month(4).day(2).hour(9).minute(0).second(0);
	}

	@Test
	public void testReceive() {
		assertEquals(this.mailbox.getName(), "We're married");
		this.mailbox.notify(null, this.question);
	}

	@Test
	public void testGetAll() {
		this.mailbox.notify(null, this.question);
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
		this.mailbox.notify(null, this.question);
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
		this.mailbox.notify(null, this.question);
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
				.getNewNotifications();
		List<Notification> susanesNotifications = this.susane
				.getNewNotifications();
		assertEquals(1, petesNotifications.size());
		assertEquals(petesNotifications.get(0).getAbout(), answer);
		assertEquals(0, susanesNotifications.size());
		petesNotifications = this.pete.getRecentNotifications();
		susanesNotifications = this.susane.getRecentNotifications();
		assertEquals(1, petesNotifications.size());
		assertEquals(petesNotifications.get(0).getAbout(), answer);
		assertEquals(0, susanesNotifications.size());
	}

	@Test
	public void shouldDeleteNotifications() {
		Mailbox mailbox = new Mailbox("test box");
		User user = new User("user");
		Question question = new Question(user, "question");
		mailbox.notify(user, question);
		Notification notification = mailbox.getAllNotifications().get(0);
		assertEquals(notification.owner(), user);
		mailbox.delete();
		assertNull(notification.owner());
	}

	@Test
	public void shouldHaveNiceDebuggingToString() {
		assertEquals(this.mailbox.toString(), "MB[" + this.mailbox.getName()
				+ "(" + this.mailbox.getAllNotifications().size() + ")" + "]");
	}
}
