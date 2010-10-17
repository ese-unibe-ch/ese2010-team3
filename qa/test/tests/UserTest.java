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
	
	@Test
	public void shouldCheckeMailValidation(){
		User user = new User("John", "john");
		user.setEmail("john@gmx.com");
		assertTrue(user.checkeMail("john@gmx.com"));
		user.setEmail("john.smith@students.unibe.ch");
		assertTrue(user.checkeMail("john.smith@students.unibe.ch"));
		user.setEmail("john@gmx.c");
		assertFalse(user.checkeMail("john@gmx.c"));
		user.setEmail("john@info.museum");
		assertFalse(user.checkeMail("john@info.museum"));
		user.setEmail("john@...com");
		assertFalse(user.checkeMail("john@...com"));
	}
	
	@Test
	public void checkMailAssertion(){
		User user = new User("Bill", "bill");
		user.setEmail("bill@aol.com");
		assertEquals(user.email(), "bill@aol.com");
	}
	
	@Test
	public void checkPassw(){
		User user = new User("Bill", "bill");
		assertEquals(user.password, user.hashofpassword());
	}

}
