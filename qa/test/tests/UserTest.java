package tests;

import java.text.ParseException;

import models.Question;
import models.User;
import models.helpers.Tools;
import models.database.Database;

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
		assertEquals(user.getName(), "Jack");
	}

	@Test
	public void checkUsernameAvailable() {
		assertTrue(User.isAvailable("JaneSmith"));
		Database.get().users().register("JaneSmith", "janesmith");
		assertFalse(User.isAvailable("JaneSmith"));
		assertFalse(User.isAvailable("janesmith"));
		assertFalse(User.isAvailable("jAnEsMiTh"));
	}

	@Test
	public void shouldCheckeMailValidation(){
		assertTrue(Tools.checkEmail("john@gmx.com"));
		assertTrue(Tools.checkEmail("john.smith@students.unibe.ch"));
		assertFalse(Tools.checkEmail("john@gmx.c"));
		assertFalse(Tools.checkEmail("john@info.museum"));
		assertFalse(Tools.checkEmail("john@...com"));
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
		assertEquals(Tools.encrypt("bill"), user.getSHA1Password());
		assertEquals(Tools.encrypt(""),
				"da39a3ee5e6b4b0d3255bfef95601890afd80709"); // Source:
																// wikipedia.org/wiki/Examples_of_SHA_digests
		assertFalse(Tools.encrypt("password").equals(Tools.encrypt("Password")));
	}

	@Test
	public void shouldEditProfileCorrectly() throws ParseException {
		User user = new User("Jack", "jack");
		user.setDateOfBirth("14.9.1987");
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
		assertFalse(user.isBlocked());
		assertEquals(user.getStatusMessage(), "");
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
		assertEquals(user.getStatusMessage(), "User is a Spammer");
		assertTrue(user.isBlocked());
	}
	
	@Test
	public void checkForCheater() {
		User user = new User("TheSupported", "supported");
		User user2 = new User("Cheater", "cheater");
		assertFalse(user.isBlocked());
		assertFalse(user2.isBlocked());
		assertEquals(user.getStatusMessage(), "");
		assertEquals(user2.getStatusMessage(), "");
		for (int i = 0; i < 5; i++) {
			new Question(user, "This is my " + i + ". question").voteUp(user2);
		}
		assertTrue(user2.isMaybeCheater());
		assertTrue(user2.isCheating());
		assertTrue(user2.isBlocked());
		assertEquals(user2.getStatusMessage(), "User voted up somebody");
		assertFalse(user.isMaybeCheater());
		assertFalse(user.isCheating());
		assertFalse(user.isBlocked());
		assertEquals(user.getStatusMessage(), "");
	}



	@Test
	public void shouldHaveOneQuestion() {
		User user = new User("Jack", "jack");
		Question q = new Question(user, "Why?");
		assertEquals(1, user.getQuestions().size());
		q.unregister();
	}

	@Test
	public void shouldHaveNoQuestion() {
		User user = new User("Jack", "jack");
		Question q = new Question(user, "Why?");
		q.unregister();
		assertEquals(0, user.getQuestions().size());
	}

	@Test
	public void shouldHaveOneAnswer() {
		User user = new User("Jack", "jack");
		Question q = new Question(user, "Why?");
		q.answer(user, "Because");
		assertEquals(1, user.getAnswers().size());
	}

	@Test
	public void shouldHaveNoAnswer() {
		User user = new User("Jack", "jack");
		Question q = new Question(user, "Why?");
		q.answer(user, "Because");
		q.answers().get(0).unregister();
		assertEquals(0, user.getAnswers().size());
	}

	@Test
	public void shouldHaveOneBestAnswer() {
		User user = new User("Jack", "jack");
		Question q = new Question(user, "Why?");
		q.answer(user, "Because");
		q.setBestAnswer(q.answers().get(0));
		assertEquals(1, user.bestAnswers().size());
	}

	@Test
	public void shouldHaveNoBestAnswer() {
		User user = new User("Jack", "jack");
		Question q = new Question(user, "Why?");
		q.answer(user, "Because");
		q.setBestAnswer(q.answers().get(0));
		q.answers().get(0).unregister();
		assertEquals(0, user.bestAnswers().size());
	}

	@Test
	public void testModerator() {
		User user = new User("Jack", "jack");
		assertFalse(user.isModerator());
		user.setModerator(true);
		assertTrue(user.isModerator());
	}

	@Test
	public void testBlock() {
		User user = new User("Jack", "jack");
		assertFalse(user.isBlocked());
		assertEquals(user.getStatusMessage(), "");
		user.block("offending comments");
		assertTrue(user.isBlocked());
		assertEquals(user.getStatusMessage(), "offending comments");
		user.unblock();
		assertFalse(user.isBlocked());
		assertEquals(user.getStatusMessage(), "");

	}

	@Test
	public void shouldHaveOneHighRatedAnswer() {
		User user = new User("Jack", "jack");
		Question q = new Question(user, "Why?");
		q.answer(user, "Because");

		User a = new User("a", "a");
		User b = new User("b", "b");
		User c = new User("c", "c");
		User d = new User("d", "d");
		User e = new User("e", "e");

		q.answers().get(0).voteUp(a);
		q.answers().get(0).voteUp(b);
		q.answers().get(0).voteUp(c);
		q.answers().get(0).voteUp(d);
		q.answers().get(0).voteUp(e);

		assertEquals(1, user.highRatedAnswers().size());

		a.delete();
		b.delete();
		c.delete();
		d.delete();
		e.delete();

		assertEquals(0, user.highRatedAnswers().size());
	}

}
