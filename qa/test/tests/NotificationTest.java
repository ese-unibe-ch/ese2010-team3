package tests;

import models.Answer;
import models.Notification;
import models.Question;
import models.SystemInformation;
import models.User;

import org.junit.Before;
import org.junit.Test;

import play.test.UnitTest;
import tests.mocks.SystemInformationMock;

public class NotificationTest extends UnitTest {

	private Question question;
	private User norbert;

	@Before
	public void setUp() {
		this.norbert = new User("Norbert", "norbert");
		this.question = new Question(this.norbert, "Need I be watched?");
	}

	@Test
	public void shouldBeObserving() {
		assertFalse(this.norbert.isObserving(this.question));
		this.norbert.startObserving(this.question);
		assertTrue(this.norbert.isObserving(this.question));
		this.norbert.stopObserving(this.question);
		assertFalse(this.norbert.isObserving(this.question));
	}

	@Test
	public void shouldBeNotified() {
		User answerer = new User("answerer", "answerer");
		assertEquals(this.norbert.getNotifications().size(), 0);
		new Answer(1, answerer, this.question, "Answer one");
		assertEquals(this.norbert.getNotifications().size(), 0);
		this.norbert.startObserving(this.question);
		Answer answer2 = new Answer(2, answerer, this.question, "Answer two");
		assertEquals(this.norbert.getNotifications().size(), 1);
		assertEquals(this.norbert.getNotifications().get(0).getAbout(), answer2);
		this.norbert.getNotifications().get(0).unregister();
		assertEquals(this.norbert.getNotifications().size(), 0);
		new Answer(3, answerer, this.question, "Answer three");
		assertEquals(this.norbert.getNotifications().size(), 1);
		this.norbert.stopObserving(this.question);
		new Answer(4, answerer, this.question, "Answer four");
		assertEquals(this.norbert.getNotifications().size(), 1);
	}

	@Test
	public void shouldHaveRecentNotifications() {
		SystemInformationMock sys = new SystemInformationMock();
		SystemInformation.mockWith(sys);
		sys.hour(12).minute(0);

		this.norbert.startObserving(this.question);
		assertNull(this.norbert.getVeryRecentNotification());
		new Answer(-1, this.norbert, this.question, "recent answer?");
		Notification recent = this.norbert.getVeryRecentNotification();
		assertNotNull(recent);
		assertEquals(recent.getAbout().summary(), "recent answer?");

		sys.minute(2);
		assertNotNull(this.norbert.getVeryRecentNotification());
		assertEquals(this.norbert.getVeryRecentNotification(), recent);
		sys.minute(10);
		assertNull(this.norbert.getVeryRecentNotification());

		assertTrue(recent.isNew());
		recent.unsetNew();
		assertFalse(recent.isNew());
	}
}
