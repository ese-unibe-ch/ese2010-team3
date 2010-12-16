package tests;

import java.util.List;

import models.Question;
import models.User;
import models.database.Database;
import models.helpers.Tools;

import org.junit.Before;
import org.junit.Test;

public class PaginationTest extends MockedUnitTest {

	private Question question1;
	private Question question3;
	User jack = new User("Jack", "jack");
	private int questionsPerPage;

	@Before
	public void setup() {
		Database.clear();
		questionsPerPage = 15;
		sysInfo.year(2010).month(9).day(3).hour(0).minute(0).second(0);
		question3 = new Question(jack, "Who?");
		sysInfo.year(2010).month(10).day(3).hour(0).minute(0).second(0);
		new Question(jack, "Where?");
		sysInfo.year(2010).month(10).day(3).hour(0).minute(0).second(5);
		question1 = new Question(jack, "Why?");
		// To make sure all other questions have a different timestamp
		sysInfo.year(2010).month(10).day(4).hour(0).minute(0).second(5);
	}

	@Test
	public void shouldDetermineMaxIndexZeroForThreeQuestions() {
		int maxIndex = Tools.determineMaximumIndex(Database.questions()
				.all(), questionsPerPage);

		assertEquals(0, maxIndex);
	}

	@Test
	public void shouldDetermineMaxIndexZeroForZeroQuestions() {
		int maxIndex = Tools.determineMaximumIndex(Database.questions()
				.all(), questionsPerPage);
		jack.delete();
		assertEquals(0, Database.questions().all().size());
		assertEquals(0, maxIndex);
	}

	@Test
	public void shouldDetermineMaxIndexTwoForThreePages() {
		for (int i = 0; i < 42; i++) {
			new Question(jack, "Could you repeat this please?");
		}
		int maxIndex = Tools.determineMaximumIndex(Database.questions()
				.all(), questionsPerPage);
		assertEquals(2, maxIndex);
	}

	@Test
	public void shouldDetermineMaxIndexThreeForFourPages() {
		for (int i = 0; i < 43; i++) {
			new Question(jack, "Could you repeat this please?");
		}
		int maxIndex = Tools.determineMaximumIndex(Database.questions()
				.all(), questionsPerPage);
		assertEquals(3, maxIndex);
	}

	@Test
	public void shouldBeOfSizeThree() {
		int index = 0;
		List<Question> questions = Tools.paginate(Database.questions()
				.all(), questionsPerPage, index);
		assertEquals(3, questions.size());
	}

	@Test
	public void shouldPaginateCorrectly() {
		for (int i = 0; i < 15; i++) {
			new Question(jack, "Could you repeat this please?");
		}
		int index = 0;
		List<Question> questions = Tools.paginate(Database.questions()
				.all(), questionsPerPage, index);
		assertEquals(15, questions.size());
		index = 1;
		questions = Tools.paginate(Database.questions().all(),
				questionsPerPage, index);
		assertEquals(3, questions.size());

	}

	@Test
	public void shouldPaginateCorrectlyForThreePages() {
		for (int i = 0; i < 42; i++) {
			new Question(jack, "Could you repeat this please?");
		}
		int index = 0;
		List<Question> questions = Tools.paginate(Database.questions()
				.all(), questionsPerPage, index);
		assertEquals(15, questions.size());
		index = 1;
		questions = Tools.paginate(Database.questions().all(),
				questionsPerPage, index);
		assertEquals(15, questions.size());
		index = 2;
		questions = Tools.paginate(Database.questions().all(),
				questionsPerPage, index);
		assertEquals(15, questions.size());
	}

	@Test
	public void shouldPaginateCorrectlyForZeroQuestions() {
		jack.delete();
		int index = 0;
		List<Question> questions = Tools.paginate(Database.questions()
				.all(), questionsPerPage, index);
		assertEquals(0, questions.size());
	}

	@Test
	public void shouldPaginateCorrectlyForOneQuestion() {
		question1.unregister();
		question3.unregister();
		int index = 0;
		List<Question> questions = Tools.paginate(Database.questions()
				.all(), questionsPerPage, index);
		assertEquals(1, questions.size());
	}

	@Test
	public void shouldPaginateCorrectlyForFifteenQuestions() {
		for (int i = 0; i < 12; i++) {
			new Question(jack, "Could you repeat this please?");
		}
		int index = 0;
		List<Question> questions = Tools.paginate(Database.questions()
				.all(), questionsPerPage, index);
		assertEquals(15, questions.size());
		index = 1;
		questions = Tools.paginate(Database.questions().all(),
				questionsPerPage, index);
		assertEquals(0, questions.size());
	}

	@Test
	public void shouldPaginateCorrectlyForSixteenQuestions() {
		for (int i = 0; i < 13; i++) {
			new Question(jack, "Could you repeat this please?");
		}
		int index = 0;
		List<Question> questions = Tools.paginate(Database.questions()
				.all(), questionsPerPage, index);
		assertEquals(15, questions.size());
		index = 1;
		questions = Tools.paginate(Database.questions().all(),
				questionsPerPage, index);
		assertEquals(1, questions.size());
	}

	@Test
	public void shouldYieldEmptyPagesAfterEnd() {
		List<Question> questions = Tools.paginate(Database.questions()
				.all(), questionsPerPage, 1);
		assertEquals(0, questions.size());
	}
}
