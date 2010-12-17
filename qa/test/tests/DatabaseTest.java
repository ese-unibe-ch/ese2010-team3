package tests;

import models.User;
import models.database.Database;
import models.database.IDatabase;
import models.database.IUserDatabase;
import models.database.HotDatabase.HotDatabase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DatabaseTest extends MockedUnitTest {

	private IDatabase origDB;

	@Before
	public void setUp() {
		this.origDB = Database.swapWith(new HotDatabase());
	}

	@After
	public void tearDown() {
		Database.swapWith(this.origDB);
	}

	@Test
	public void shouldKeepAdmins() {
		IUserDatabase userDB = Database.users();
		userDB.clear();

		User admin = userDB.register("admin", "admin", "admin@example.com");
		admin.setModerator(true, null);
		User user = userDB.register("user", "user", "user@example.com");
		assertEquals(2, userDB.all().size());
		assertEquals(2, userDB.count());
		assertEquals(1, userDB.allModerators().size());
		assertTrue(userDB.all().contains(user));
		assertTrue(userDB.all().contains(admin));

		Database.clearKeepAdmins();
		assertEquals(1, userDB.all().size());
		assertEquals(1, userDB.allModerators().size());
		assertFalse(userDB.all().contains(user));
		assertTrue(userDB.all().contains(admin));

		Database.clear();
		assertEquals(0, userDB.all().size());
		assertEquals(0, userDB.allModerators().size());
		assertFalse(userDB.all().contains(user));
		assertFalse(userDB.all().contains(admin));

		Database.clearKeepAdmins();
	}

	@Test
	public void shouldSwapAndBack() {
		IDatabase newDB = new HotDatabase();
		assertNotSame(newDB.users(), Database.users());
		IDatabase current = Database.swapWith(newDB);
		assertNotSame(current, newDB);
		assertEquals(newDB.users(), Database.users());
		IDatabase prevDB = Database.swapWith(current);
		assertEquals(prevDB, newDB);
		assertEquals(current.questions(), Database.questions());
	}
}
