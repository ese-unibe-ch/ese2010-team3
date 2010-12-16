package tests;

import models.User;
import models.database.Database;
import models.database.IUserDatabase;

import org.junit.Test;

public class DatabaseTest extends MockedUnitTest {

	@Test
	public void shouldKeepAdmins() {
		IUserDatabase userDB = Database.users();
		userDB.clear();

		User admin = userDB.register("admin", "admin", "admin@example.com");
		admin.setModerator(true);
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
}
