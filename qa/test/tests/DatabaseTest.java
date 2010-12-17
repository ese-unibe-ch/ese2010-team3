package tests;

import models.Question;
import models.User;
import models.database.IDatabase;
import models.database.IUserDatabase;
import models.database.HotDatabase.HotDatabase;
import models.database.HotDatabase.HotUserDatabase;

import org.junit.Test;

import controllers.Database;

public class DatabaseTest extends MockedUnitTest {

	@Test
	public void shouldKeepAdmins() {
		IDatabase db = new HotDatabase();
		IUserDatabase userDB = db.users();

		User admin = userDB.register("admin", "admin", "admin@example.com");
		admin.setModerator(true, null);
		User user = userDB.register("user", "user", "user@example.com");
		assertEquals(2, userDB.all().size());
		assertEquals(2, userDB.count());
		assertEquals(1, userDB.allModerators().size());
		assertTrue(userDB.all().contains(user));
		assertTrue(userDB.all().contains(admin));

		db.clear(true);
		assertEquals(1, userDB.all().size());
		assertEquals(1, userDB.allModerators().size());
		assertFalse(userDB.all().contains(user));
		assertTrue(userDB.all().contains(admin));

		db.clear(false);
		assertEquals(0, userDB.all().size());
		assertEquals(0, userDB.allModerators().size());
		assertFalse(userDB.all().contains(user));
		assertFalse(userDB.all().contains(admin));

		db.clear(true);
	}

	@Test
	public void shouldSwapAndBack() {
		IDatabase origDB = Database.swapWith(new HotDatabase());
		IDatabase newDB = new HotDatabase();
		assertNotSame(newDB.users(), Database.users());
		IDatabase current = Database.swapWith(newDB);
		assertNotSame(current, newDB);
		assertEquals(newDB.users(), Database.users());
		assertEquals(newDB.questions(), Database.questions());
		assertEquals(newDB.tags(), Database.tags());
		IDatabase prevDB = Database.swapWith(current);
		assertEquals(prevDB, newDB);
		assertEquals(current.questions(), Database.questions());
		Database.swapWith(origDB);
	}

	@Test
	public void shouldClearDB() {
		IDatabase db = new HotDatabase();
		User user = db.users().register("user", "password", "user@example.com");
		Question question = db.questions().add(user, "question");
		question.setTagString("tag");
		db.users().getModeratorMailbox().notify(null, question);
		assertEquals(1, db.users().all().size());
		assertEquals(1, db.questions().all().size());
		assertEquals(1, db.tags().all().size());
		assertEquals(1, db.users().getModeratorMailbox().getAllNotifications()
				.size());

		db.clear(false);
		assertEquals(0, db.users().all().size());
		assertEquals(0, db.questions().all().size());
		assertEquals(0, db.tags().all().size());
		assertEquals(0, db.users().getModeratorMailbox().getAllNotifications()
				.size());
	}

	@Test
	public void shouldCleanupUser() {
		IUserDatabase userDB = new HotUserDatabase();
		User user = userDB.register("user", "password", "user@example.com");
		assertEquals(1, userDB.all().size());
		user.delete();
		assertEquals(0, userDB.all().size());
	}
}
