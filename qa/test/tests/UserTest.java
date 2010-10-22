package tests;

import java.text.ParseException;

import models.Question;
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
		assertTrue(user.checkEmail("john@gmx.com"));
		assertTrue(user.checkEmail("john.smith@students.unibe.ch"));
		assertFalse(user.checkEmail("john@gmx.c"));
		assertFalse(user.checkEmail("john@info.museum"));
		assertFalse(user.checkEmail("john@...com"));
	}
	
	@Test
	public void checkMailAssertion(){
		User user = new User("Bill", "bill");
		user.setEmail("bill@aol.com");
		assertEquals(user.getEmail(), "bill@aol.com");
	}
	
	@Test
	public void checkPassw(){
		User user = new User("Bill", "bill");
		assertEquals(user.encrypt("bill"), user.getMd5Password());
		assertFalse(user.encrypt("password").equals(user.encrypt("Password")));
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
	
	@Test
	public void RightNumberOfItems(){
		User user = new User("miko", "miko");
		assertTrue(user.howManyItems() == 0);
		Question question = new Question(user, "Why did the chicken cross the road?");
		assertTrue(user.howManyItems() == 1);
		Question quest = new Question(user, "Does anybody know?");
		assertFalse(user.howManyItems() == 1);
	}
	
}
