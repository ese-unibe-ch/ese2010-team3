package tests;

import java.text.ParseException;

import models.User;

import org.junit.Test;

import play.test.UnitTest;

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
		assertEquals(user.encrypt("bill"), user.getMd5Password());
	}

	@Test
	public void shouldEditProfileCorrectly() throws ParseException {
		User user = new User("Jack", "jack");
		user.setDateOfBirth("14-09-87");
		user.setBiography("I lived");
		user.setEmail("test@test.tt");
		user.setEmployer("TestInc");
		user.setFullname("Test Tester");
		user.setProfession("tester");
		user.setWebsite("http://www.test.ch");

		assertEquals(user.getAge(), 23);
		assertTrue(user.getBiography().equalsIgnoreCase("I lived"));
		assertTrue(user.getEmail().equals("test@test.tt"));
		assertTrue(user.getEmployer().equals("TestInc"));
		assertTrue(user.getFullname().equals("Test Tester"));
		assertTrue(user.getProfession().equals("tester"));
		assertTrue(user.getWebsite().equals("http://www.test.ch"));
	}
}
