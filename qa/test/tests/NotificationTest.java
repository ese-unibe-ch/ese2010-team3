package tests;

import java.util.Collections;
import java.util.List;

import models.Answer;
import models.ISystemInformation;
import models.Notification;
import models.Question;
import models.SystemInformation;
import models.User;
import models.database.Database;
import models.helpers.IObservable;
import models.helpers.IObserver;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import play.test.UnitTest;
import tests.mocks.SystemInformationMock;

public class NotificationTest extends UnitTest {

	private ISystemInformation savedSysInfo;
	private Question question;
	private User norbert;
	private User andrew;

	@Before
	public void setUp() {
		this.savedSysInfo = SystemInformation.get();
		this.norbert = new User("Norbert", "norbert");
		this.question = new Question(this.norbert, "Need I be watched?");
		this.andrew = new User("Andrew", "andrew");
	}

	@After
	public void tearDown() {
		SystemInformation.mockWith(this.savedSysInfo);
	}

	@Test
	public void shouldBeObserving() {
		assertTrue(this.norbert.isObserving(this.question));
		this.norbert.stopObserving(this.question);
		assertFalse(this.norbert.isObserving(this.question));
		this.norbert.startObserving(this.question);
		assertTrue(this.norbert.isObserving(this.question));
	}

	@Test
	public void shouldBeNotified() {
		this.norbert.stopObserving(this.question);
		assertEquals(this.norbert.getNotifications().size(), 0);
		this.question.answer(this.andrew, "Answer one");
		assertEquals(this.norbert.getNotifications().size(), 0);
		this.norbert.startObserving(this.question);
		Answer answer2 = this.question.answer(this.andrew, "Answer two");
		assertEquals(this.norbert.getNotifications().size(), 1);
		assertEquals(this.norbert.getNotifications().get(0).getAbout(), answer2);
		this.norbert.getNotifications().get(0).unregister();
		assertEquals(this.norbert.getNotifications().size(), 0);
		this.question.answer(this.andrew, "Answer three");
		assertEquals(this.norbert.getNotifications().size(), 1);
		this.norbert.stopObserving(this.question);
		this.question.answer(this.andrew, "Answer four");
		assertEquals(this.norbert.getNotifications().size(), 1);
	}

	@Test
	public void shouldHaveRecentNotifications() {
		SystemInformationMock sys = new SystemInformationMock();
		SystemInformation.mockWith(sys);
		sys.hour(12).minute(0);

		assertNull(this.norbert.getVeryRecentNewNotification());
		assertEquals(this.norbert.getNewNotifications().size(), 0);

		this.question.answer(this.andrew, "recent answer?");
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
		for (int i = 0; i < 10; i++) {
			this.question.answer(this.andrew, "Answer " + i);
		}
		assertEquals(this.norbert.getNotifications().size(), 10);

		Notification first = Collections.max(this.norbert.getNotifications());
		assertEquals(this.norbert.getNotification(first.id()), first);
		// What is this about and why is it tested here? and #get(9) should
		// equal first?
		assertEquals(this.norbert.getNotifications().get(9), first);

		for (int i = 0; i < 9; i++) {
			assertTrue(this.norbert.getNotifications().get(i).id() > this.norbert
					.getNotifications().get(i + 1).id());
		}

		Notification last = Collections.min(this.norbert.getNotifications());
		assertEquals(this.norbert.getNotifications().get(0), last);
		assertEquals(this.norbert.getVeryRecentNewNotification(), last);

		last.unsetNew();
		assertEquals(this.norbert.getVeryRecentNewNotification(), this.norbert
				.getNotifications().get(1));

		assertNull(this.norbert.getNotification(-1));
	}

	@Test
	public void shouldNotGetSelfNotified() {
		this.andrew.startObserving(this.question);
		this.question.answer(this.norbert, "Norbert's answer");
		this.question.answer(this.andrew, "Andrew's answer");
		assertEquals(this.norbert.getNotifications().size(), 1);
		assertEquals(this.andrew.getNotifications().size(), 1);
		assertEquals(this.norbert.getVeryRecentNewNotification().getAbout()
				.owner(),
				this.andrew);
		assertEquals(this.andrew.getVeryRecentNewNotification().getAbout()
				.owner(),
				this.norbert);
	}

	@Test
	public void shouldNotNotifyAboutDeletedEntries() {
		this.question.answer(this.andrew, "soon to be gone");
		assertEquals(this.norbert.getNotifications().size(), 1);
		assertNotNull(this.norbert.getVeryRecentNewNotification());
		this.andrew.delete();
		assertEquals(this.norbert.getNotifications().size(), 0);
		assertNull(this.norbert.getVeryRecentNewNotification());
	}

	@Test
	public void shouldNotifyAboutAnonymousEntries() {
		this.question.answer(this.andrew, "soon to be gone");
		assertEquals(this.norbert.getNotifications().size(), 1);
		assertNotNull(this.norbert.getVeryRecentNewNotification());
		this.andrew.anonymize(true);
		this.andrew.delete();
		assertEquals(this.norbert.getNotifications().size(), 1);
		assertNotNull(this.norbert.getVeryRecentNewNotification());
		assertNull(this.norbert.getVeryRecentNewNotification().getAbout()
				.owner());
	}

	@Test
	public void shouldUnregisterNotifications() {
		this.norbert.observe(this.question, this.question.answer(this.andrew,
				"???"));
		Notification notification = this.norbert.getNotifications().get(0);
		this.norbert.anonymize(true);
		this.norbert.delete();
		assertNull(notification.owner());
	}

	@Test
	public void shouldOnlyNotifyAboutAnswersForNow() {
		IObservable unobservable = new IObservable() {
			public void addObserver(IObserver o) {
			}

			public void removeObserver(IObserver o) {
			}

			public boolean hasObserver(IObserver o) {
				return false;
			}

			public void notifyObservers(Object arg) {
			}
		};
		this.norbert.observe(unobservable, null);
		this.norbert.observe(this.question, null);
		assertEquals(this.norbert.getNotifications().size(), 0);

		// keep Cobertura happy
		unobservable.addObserver(this.norbert);
		unobservable.removeObserver(this.norbert);
		assertFalse(unobservable.hasObserver(this.norbert));
		unobservable.notifyObservers(this.norbert);
	}

	@Test
	public void shouldRequireObserver() {
		boolean hasThrown = false;
		try {
			this.question.addObserver(null);
		} catch (IllegalArgumentException ex) {
			hasThrown = true;
		}
		assertTrue(hasThrown);
	}

	@Test
	public void shouldBeOnWatchList() {
		Question question2 = new Question(this.andrew, "another question");
		List<Question> watchList = Database.get().questions()
				.getWatchList(this.norbert);
		assertTrue(watchList.contains(this.question));
		assertFalse(watchList.contains(question2));
	}

	@Test
	public void shouldMakeCoberturaHappy() {
		this.norbert.observe(this.question, this.question.answer(this.andrew,
				"???"));
		Notification notification = this.norbert.getNotifications().get(0);
		assertEquals("N[" + this.question.owner().toString()
				+ notification.getAbout().toString() + "]",
				notification.toString());
	}
}
