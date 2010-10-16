package tests;
import static org.junit.Assert.*;
import org.junit.Test;
import models.*;
import play.test.*;


public class UserTest extends UnitTest {

	@Test
	public void shouldCreateUser() {
		User user = new User("Jack", "jack");
		assertTrue(user != null);
	}
	
	@Test
	public void shouldBeCalledJack() {
		User user = new User("Jack", "jack");
		assertEquals(user.name(), "Jack");
	}

}
