package tests;

import java.util.List;

import models.Answer;
import models.Entry;
import models.Question;
import models.User;
import models.database.Database;

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

	@Before
	public void setUp() throws Exception {
		jack = new User("jack", "b");
		createEntries();
		voting();
	}

	private void voting() {
		voteDownNTimes(badQuestion, 5);
		voteUpNTimes(goodQuestion, 2);
		voteUpNTimes(goodAnswer, 10);
		voteUpNTimes(bestAnswer, 5);
		badQuestion.setBestAnswer(bestAnswer);
		voteDownNTimes(notQuiteAsBadAnswer, 3);
		voteDownNTimes(badAnswer, 5);
	}

	private void voteUpNTimes(Entry entry, int n) {
		for (Integer i = 0; i < n; i++) {
			entry.voteUp(new User(i.toString(), i.toString()));
		}
	}

	private void voteDownNTimes(Entry entry, int n) {
		for (Integer i = 0; i < n; i++) {
			entry.voteDown(new User(i.toString(), i.toString()));
		}
	}

	private void createEntries() {
		badQuestion = new Question(jack, "");
		goodQuestion = new Question(jack, "");
		badAnswer = badQuestion.answer(jack, "");
		notQuiteAsBadAnswer = badQuestion.answer(jack, "");
		goodAnswer = badQuestion.answer(jack, "");
		bestAnswer = badQuestion.answer(jack, "");
	}

	public void shouldPreferMoreRecentQuestionEventhoughTheyMightBeWorse() {
		List<Question> questions = Database.questions().all();
		assertEquals(goodQuestion, questions.get(1));
		assertEquals(badQuestion, questions.get(0));
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
