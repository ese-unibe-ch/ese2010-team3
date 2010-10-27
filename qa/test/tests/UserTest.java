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
	public void checkUsernameAvailable() {
		assertTrue(User.isAvailable("JaneSmith"));
		User.register("JaneSmith", "janesmith");
		assertFalse(User.isAvailable("JaneSmith"));
		assertFalse(User.isAvailable("jAnEsMiTh"));
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
	public void checkPassw() {
		User user = new User("Bill", "bill");
		assertTrue(user.checkPW("bill"));
		assertEquals(user.encrypt("bill"), user.getSHA1Password());
		assertEquals(user.encrypt(""),
				"da39a3ee5e6b4b0d3255bfef95601890afd80709"); // Source:
																// wikipedia.org/wiki/Examples_of_SHA_digests
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
	public void checkForSpammer() {
		User user = new User("Spammer", "spammer");
		assertTrue(user.howManyItemsPerHour() == 0);
		new Question(user, "Why did the chicken cross the road?");
		assertTrue(user.howManyItemsPerHour() == 1);
		new Question(user, "Does anybody know?");
		assertFalse(user.howManyItemsPerHour() == 1);
		for (int i = 0; i < 57; i++) {
			new Question(user, "This is my " + i + ". question");
		}
		assertTrue(!user.isSpammer());
		assertTrue(user.howManyItemsPerHour() == 59);
		assertTrue(!user.isCheating());
		new Question(user, "My last possible Post");
		assertTrue(user.isSpammer());
		assertTrue(user.isCheating());
	}
	
	@Test
	public void checkForCheater() {
		User user = new User("TheSupported", "supported");
		User user2 = new User("Cheater", "cheater");
		for (int i = 0; i < 4; i++) {
			new Question(user, "This is my " + i + ". question").voteUp(user2);
		}
		assertTrue(user2.isMaybeCheater());
		assertTrue(user2.isCheating());
		assertTrue(!user.isMaybeCheater());
		assertTrue(!user.isCheating());
	}

}
