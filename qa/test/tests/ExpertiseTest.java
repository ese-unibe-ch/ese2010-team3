package tests;

import java.util.ArrayList;
import java.util.List;

import models.Answer;
import models.Question;
import models.Tag;
import models.User;
import models.database.Database;
import models.database.IDatabase;
import models.database.HotDatabase.HotDatabase;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import play.test.UnitTest;

public class ExpertiseTest extends UnitTest {

	private static IDatabase origDB;

	@BeforeClass
	public static void mockDB() {
		origDB = Database.get();
		Database.swapWith(new HotDatabase());

	}

	@AfterClass
	public static void restoreDB() {
		Database.swapWith(origDB);
	}

	private User john;
	private List<User> users;
	private Question question;

	@Before
	public void setUp() {
		Database.clear();

		users = new ArrayList();
		for (int i = 0; i < 20; i++)
			users.add(new User("user " + i, ""));
		john = new User("John", "john");
		question = new Question(new User("James", "james"),
				"Question");
	}

	@Test
	public void shouldBeSoleExpert() {
		question.setTagString("sole-expert");
		for (int i = 10; i < 20; i++)
			question.answer(users.get(i), "Answer " + i);
		Answer answer = question.answer(john, "Answer");
		for (int i = 0; i < 10; i++)
			answer.voteUp(users.get(i));

		Tag tag = Database.get().tags().get("sole-expert");
		assertEquals(john.getExpertise().size(), 1);
		assertTrue(john.getExpertise().contains(tag));
		assertFalse(question.owner().getExpertise().contains(tag));
		for (int i = 0; i < 20; i++)
			assertFalse(users.get(i).getExpertise().contains(tag));
	}

	@Test
	public void shouldAlsoBeExpert() {
		question.setTagString("also-expert");
		for (int i = 10; i < 20; i++)
			question.answer(users.get(i), "Answer " + i);
		int k = 6;
		for (Answer uanswer : question.answers()) {
			for (int i = 0; i < k; i++)
				uanswer.voteUp(users.get(i));
			k--;
		}
		Answer answer = question.answer(john, "Answer");
		question.setBestAnswer(answer);

		Tag tag = Database.get().tags().get("also-expert");
		assertEquals(john.getExpertise().size(), 1);
		assertTrue(john.getExpertise().contains(tag));
		assertFalse(question.owner().getExpertise().contains(tag));
		for (int i = 0; i < 20; i++)
			assertEquals(users.get(i).getExpertise().contains(tag), 10 <= i
					&& i < 12);
	}

	@Test
	public void shouldNotBeExpert() {
		question.setTagString("no-expert");
		for (int i = 10; i < 20; i++)
			question.answer(users.get(i), "Answer " + i);
		int k = 7;
		for (Answer uanswer : question.answers()) {
			for (int i = 0; i < k; i++)
				uanswer.voteUp(users.get(i));
			k--;
		}
		Answer answer = question.answer(john, "Answer");
		for (int i = 0; i < 4; i++)
			answer.voteUp(users.get(i));

		Tag tag = Database.get().tags().get("no-expert");
		assertEquals(john.getExpertise().size(), 0);
		assertFalse(question.owner().getExpertise().contains(tag));
		for (int i = 0; i < 20; i++)
			assertEquals(users.get(i).getExpertise().contains(tag), 10 <= i
					&& i < 13);

		assertEquals(john.getExpertise().size(), 0);
		users.get(10).anonymize(true);
		users.get(11).anonymize(true);
		assertEquals(john.getExpertise().size(), 1);
	}

	@Test
	public void shouldNotBeExpertWithoutAnswer() {
		question.setTagString("no-answer");
		for (int i = 10; i < 20; i++)
			question.answer(users.get(i), "Answer " + i);
		int k = 2;
		for (Answer uanswer : question.answers()) {
			for (int i = 0; i < k; i++)
				uanswer.voteUp(users.get(i));
			k--;
		}

		Tag tag = Database.get().tags().get("no-answer");
		assertEquals(john.getExpertise().size(), 0);
		assertFalse(question.owner().getExpertise().contains(tag));
		for (int i = 0; i < 20; i++)
			assertEquals(users.get(i).getExpertise().contains(tag), 10 == i);
	}

	@Test
	public void shouldNotBeExpertForOwnQuestions() {
		User james = question.owner();
		question.setTagString("own-answer");
		Answer answer = question.answer(james, "Answer");
		question.setBestAnswer(answer);

		Tag tag = Database.get().tags().get("own-answer");
		assertEquals(james.getExpertise().size(), 0);
		assertFalse(question.owner().getExpertise().contains(tag));
	}

	@Test
	public void shouldNotBeExpertWithoutTags() {
		Answer answer = question.answer(john, "Answer");
		for (int i = 0; i < 10; i++)
			answer.voteUp(users.get(i));
		assertEquals(john.getExpertise().size(), 0);
	}

	@Test
	public void shouldCumulateVotes() {
		question.setTagString("sole-expert");
		for (int i = 0; i < 10; i++)
			question.answer(john, "Answer " + i);
		for (int i = 0; i < 3; i++)
			john.getAnswers().get(3 + i).voteUp(question.owner());

		Tag tag = Database.get().tags().get("sole-expert");
		assertEquals(john.getExpertise().size(), 1);
		assertTrue(john.getExpertise().contains(tag));
		assertFalse(question.owner().getExpertise().contains(tag));
	}
}
