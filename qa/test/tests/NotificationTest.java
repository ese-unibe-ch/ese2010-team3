package tests;

import java.util.Collections;

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
	private User andrew;

	@Before
	public void setUp() {
		this.norbert = new User("Norbert", "norbert");
		this.question = new Question(this.norbert, "Need I be watched?");
		this.andrew = new User("Andrew", "andrew");
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
		assertEquals(this.norbert.getNotifications().size(), 0);
		new Answer(1, this.andrew, this.question, "Answer one");
		assertEquals(this.norbert.getNotifications().size(), 0);
		this.norbert.startObserving(this.question);
		Answer answer2 = new Answer(2, this.andrew, this.question, "Answer two");
		assertEquals(this.norbert.getNotifications().size(), 1);
		assertEquals(this.norbert.getNotifications().get(0).getAbout(), answer2);
		this.norbert.getNotifications().get(0).unregister();
		assertEquals(this.norbert.getNotifications().size(), 0);
		new Answer(3, this.andrew, this.question, "Answer three");
		assertEquals(this.norbert.getNotifications().size(), 1);
		this.norbert.stopObserving(this.question);
		new Answer(4, this.andrew, this.question, "Answer four");
		assertEquals(this.norbert.getNotifications().size(), 1);
	}

	@Test
	public void shouldHaveRecentNotifications() {
		SystemInformationMock sys = new SystemInformationMock();
		SystemInformation.mockWith(sys);
		sys.hour(12).minute(0);

		this.norbert.startObserving(this.question);
		assertNull(this.norbert.getVeryRecentNewNotification());
		assertEquals(this.norbert.getNewNotifications().size(), 0);

		new Answer(-1, this.andrew, this.question, "recent answer?");
		Notification recent = this.norbert.getVeryRecentNewNotification();
		assertNotNull(recent);
		assertEquals(recent.getAbout().summary(), "recent answer?");

		sys.minute(2);
		assertNotNull(this.norbert.getVeryRecentNewNotification());
		assertEquals(this.norbert.getVeryRecentNewNotification(), recent);
		sys.minute(10);
		assertNull(this.norbert.getVeryRecentNewNotification());

		assertTrue(recent.isNew());
		assertEquals(this.norbert.getNewNotifications().size(), 1);
		recent.unsetNew();
		assertFalse(recent.isNew());
		assertEquals(this.norbert.getNewNotifications().size(), 0);
	}

	@Test
	public void shouldHaveDifferentNotificationIDs() {
		this.norbert.startObserving(this.question);
		for (int i = 0; i < 10; i++)
			new Answer(i, this.andrew, this.question, "Answer " + i);
		assertEquals(this.norbert.getNotifications().size(), 10);

		Notification first = Collections.max(this.norbert
				.getNotifications());
		assertEquals(this.norbert.getNotification(first.getID()), first);
		assertEquals(this.norbert.getNotifications().get(9), first);
		
		for (int i = 0; i < 9; i++)
			assertTrue(this.norbert.getNotifications().get(i).getID() > this.norbert
					.getNotifications().get(i + 1).getID());

		Notification last = Collections.min(this.norbert.getNotifications());
		assertEquals(this.norbert.getNotifications().get(0), last);
		assertEquals(this.norbert.getVeryRecentNewNotification(), last);

		last.unsetNew();
		assertEquals(this.norbert.getVeryRecentNewNotification(), this.norbert
				.getNotifications().get(1));
	}

	@Test
	public void shouldNotGetSelfNotified() {
		this.norbert.startObserving(this.question);
		this.andrew.startObserving(this.question);
		new Answer(1, this.norbert, this.question, "Norbert's answer");
		new Answer(2, this.andrew, this.question, "Andrew's answer");
		assertEquals(this.norbert.getNotifications().size(), 1);
		assertEquals(this.andrew.getNotifications().size(), 1);
		assertEquals(this.norbert.getVeryRecentNewNotification().getAbout()
				.owner(), this.andrew);
		assertEquals(this.andrew.getVeryRecentNewNotification().getAbout()
				.owner(), this.norbert);
	}
}
