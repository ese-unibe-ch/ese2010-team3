package tests;

import java.util.List;

import models.Answer;
import models.Entry;
import models.Question;
import models.User;
import models.database.IQuestionDatabase;
import models.database.HotDatabase.HotQuestionDatabase;

import org.junit.Before;
import org.junit.Test;

public class EntryOrderingTest extends MockedUnitTest {

	private Question badQuestion;
	private Question goodQuestion;
	private Answer badAnswer;
	private Answer notQuiteAsBadAnswer;
	private Answer goodAnswer;
	private Answer bestAnswer;
	private User jack;
	private IQuestionDatabase questionDB;

	@Before
	public void setUp() {
		this.questionDB = new HotQuestionDatabase(null);

		jack = new User("jack");
		badQuestion = this.questionDB.add(jack, "bad");
		goodQuestion = this.questionDB.add(jack, "good");
		badAnswer = badQuestion.answer(jack, "");
		notQuiteAsBadAnswer = badQuestion.answer(jack, "");
		goodAnswer = badQuestion.answer(jack, "");
		bestAnswer = badQuestion.answer(jack, "");

		voteDownNTimes(badQuestion, 5);
		voteUpNTimes(goodQuestion, 2);
		voteUpNTimes(goodAnswer, 10);
		voteUpNTimes(bestAnswer, 5);
		badQuestion.setBestAnswer(bestAnswer);
		voteDownNTimes(notQuiteAsBadAnswer, 3);
		voteDownNTimes(badAnswer, 5);
	}

	private void voteUpNTimes(Entry entry, int n) {
		for (int i = 0; i < n; i++) {
			entry.voteUp(new User("user" + i));
		}
	}

	private void voteDownNTimes(Entry entry, int n) {
		for (int i = 0; i < n; i++) {
			entry.voteDown(new User("user" + i));
		}
	}

	@Test
	public void shouldPreferMoreRecentQuestionEventhoughTheyMightBeWorse() {
		// TODO: this doesn't do what the tests expects it to!
		List<Question> questions = this.questionDB.all();
		assertEquals(goodQuestion, questions.get(0));
		assertEquals(badQuestion, questions.get(1));
	}

	@Test
	public void shouldPreferBestAnswerToGoodToBadToVeryBad() {
		List<Answer> answers = badQuestion.answers();
		assertEquals(bestAnswer, answers.get(0));
		assertEquals(goodAnswer, answers.get(1));
		assertEquals(notQuiteAsBadAnswer, answers.get(2));
		assertEquals(badAnswer, answers.get(3));
	}
}
