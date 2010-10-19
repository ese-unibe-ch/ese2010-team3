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
