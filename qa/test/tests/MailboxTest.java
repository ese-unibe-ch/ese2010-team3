package tests;

import java.util.List;

import models.ISystemInformation;
import models.Mailbox;
import models.Notification;
import models.Question;
import models.SystemInformation;
import models.User;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import play.test.UnitTest;
import tests.mocks.SystemInformationMock;

public class MailboxTest extends UnitTest {
	private User pete;
	private User susane;
	private Mailbox mailbox;
	private Question question;
	private SystemInformationMock sys;
	private static ISystemInformation old;

	@BeforeClass
	public static void setUpClass() {
		old = SystemInformation.get();
	}

	@AfterClass
	public static void tearDownClass() {
		SystemInformation.mockWith(old);
	}

	@Before
	public void setUp() {
		this.pete = new User("Pete", "");
		this.susane = new User("Susane", "");
		this.mailbox = new Mailbox("We're married");
		this.pete.addMailbox(this.mailbox);
		this.susane.addMailbox(this.mailbox);
		this.question = new Question(this.susane, "ORLY?");
		this.sys = new SystemInformationMock();
		this.sys.year(2000).month(4).day(2).hour(9).minute(0).second(0);

		SystemInformation.mockWith(this.sys);
	}

	@Test
	public void testReceive() {
		assertEquals(this.mailbox.getName(), "We're married");
		new Notification(this.mailbox, this.question);
	}

	@Test
	public void testGetAll() {
		new Notification(this.mailbox, this.question);
		List<Notification> petesNotifications = this.pete.getAllNotifications();
		List<Notification> susanesNotifications = this.susane
				.getAllNotifications();
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

		this.sys.minute(15);
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
		new Notification(this.pete, this.question);
		List<Notification> petesNotifications = this.pete
				.getMyNewNotifications();
		List<Notification> susanesNotifications = this.susane
				.getMyNewNotifications();
		assertEquals(1, petesNotifications.size());
		assertEquals(petesNotifications.get(0).getAbout(), this.question);
		assertEquals(0, susanesNotifications.size());
		petesNotifications = this.pete
				.getMyRecentNotifications();
		susanesNotifications = this.susane
				.getMyRecentNotifications();
		assertEquals(1, petesNotifications.size());
		assertEquals(petesNotifications.get(0).getAbout(), this.question);
		assertEquals(0, susanesNotifications.size());
	}

	@Test
	public void shouldHaveNiceDebuggingToString() {
		assertEquals(this.mailbox.toString(), "MB[" + this.mailbox.getName()
				+ "(" + this.mailbox.getAllNotifications().size() + ")" + "]");
	}
}
