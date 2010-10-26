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
