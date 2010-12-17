package tests;

import java.util.ArrayList;
import java.util.List;

import models.Answer;
import models.Question;
import models.Tag;
import models.User;
import models.database.IQuestionDatabase;
import models.database.ITagDatabase;
import models.database.HotDatabase.HotQuestionDatabase;
import models.database.HotDatabase.HotTagDatabase;

import org.junit.Before;
import org.junit.Test;

public class ExpertiseTest extends MockedUnitTest {

	private User john;
	private List<User> users;
	private Question question;
	private IQuestionDatabase questionDB;
	private ITagDatabase tagDB;

	@Before
	public void setUp() {
		this.tagDB = new HotTagDatabase();
		this.questionDB = new HotQuestionDatabase(this.tagDB);
		users = new ArrayList();
		for (int i = 0; i < 20; i++)
			users.add(new User("user " + i));
		john = new User("John");
		question = this.questionDB.add(new User("James"), "Question");
	}

	@Test
	public void shouldBeSoleExpert() {
		question.setTagString("sole-expert");
		for (int i = 10; i < 20; i++)
			question.answer(users.get(i), "Answer " + i);
		Answer answer = question.answer(john, "Answer");
		for (int i = 0; i < 10; i++)
			answer.voteUp(users.get(i));

		Tag tag = this.tagDB.get("sole-expert");
		assertEquals(this.questionDB.getExpertise(john).size(), 1);
		assertTrue(this.questionDB.getExpertise(john).contains(tag));
		assertFalse(this.questionDB.getExpertise(question.owner())
				.contains(tag));
		for (int i = 0; i < 20; i++)
			assertFalse(this.questionDB.getExpertise(users.get(i))
					.contains(tag));
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

		Tag tag = this.tagDB.get("also-expert");
		assertEquals(this.questionDB.getExpertise(john).size(), 1);
		assertTrue(this.questionDB.getExpertise(john).contains(tag));
		assertFalse(this.questionDB.getExpertise(question.owner())
				.contains(tag));
		for (int i = 0; i < 20; i++)
			assertEquals(
					this.questionDB.getExpertise(users.get(i)).contains(tag),
					10 <= i && i < 12);
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

		Tag tag = this.tagDB.get("no-expert");
		assertEquals(this.questionDB.getExpertise(john).size(), 0);
		assertFalse(this.questionDB.getExpertise(question.owner())
				.contains(tag));
		for (int i = 0; i < 20; i++)
			assertEquals(
					this.questionDB.getExpertise(users.get(i)).contains(tag),
					10 <= i && i < 13);

		assertEquals(this.questionDB.getExpertise(john).size(), 0);
		users.get(10).anonymize(true);
		users.get(11).anonymize(true);
		assertEquals(this.questionDB.getExpertise(john).size(), 1);
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

		Tag tag = this.tagDB.get("no-answer");
		assertEquals(this.questionDB.getExpertise(john).size(), 0);
		assertFalse(this.questionDB.getExpertise(question.owner())
				.contains(tag));
		for (int i = 0; i < 20; i++)
			assertEquals(
					this.questionDB.getExpertise(users.get(i)).contains(tag),
					10 == i);
	}

	@Test
	public void shouldNotBeExpertForOwnQuestions() {
		User james = question.owner();
		question.setTagString("own-answer");
		Answer answer = question.answer(james, "Answer");
		question.setBestAnswer(answer);

		Tag tag = this.tagDB.get("own-answer");
		assertEquals(this.questionDB.getExpertise(james).size(), 0);
		assertFalse(this.questionDB.getExpertise(question.owner())
				.contains(tag));
	}

	@Test
	public void shouldNotBeExpertWithoutTags() {
		Answer answer = question.answer(john, "Answer");
		for (int i = 0; i < 10; i++)
			answer.voteUp(users.get(i));
		assertEquals(this.questionDB.getExpertise(john).size(), 0);
	}

	@Test
	public void shouldCumulateVotes() {
		question.setTagString("sole-expert");
		for (int i = 0; i < 10; i++)
			question.answer(john, "Answer " + i);
		for (int i = 0; i < 3; i++)
			john.getAnswers().get(3 + i).voteUp(question.owner());

		Tag tag = this.tagDB.get("sole-expert");
		assertEquals(this.questionDB.getExpertise(john).size(), 1);
		assertTrue(this.questionDB.getExpertise(john).contains(tag));
		assertFalse(this.questionDB.getExpertise(question.owner())
				.contains(tag));
	}
}
