package tests;

import java.util.ArrayList;
import java.util.List;

import models.Question;
import models.User;
import models.database.IQuestionDatabase;
import models.database.HotDatabase.HotQuestionDatabase;
import models.helpers.Tools;

import org.junit.Before;
import org.junit.Test;

public class PaginationTest extends MockedUnitTest {

	private Question question1;
	private Question question3;
	private final User jack = new User("Jack");
	private final int questionsPerPage = 15;
	private IQuestionDatabase questionDB;

	@Before
	public void setup() {
		this.questionDB = new HotQuestionDatabase(null);
		sysInfo.year(2010).month(9).day(3).hour(0).minute(0).second(0);
		question3 = this.questionDB.add(jack, "Who?");
		sysInfo.year(2010).month(10).day(3).hour(0).minute(0).second(0);
		questionDB.add(jack, "Where?");
		sysInfo.year(2010).month(10).day(3).hour(0).minute(0).second(5);
		question1 = this.questionDB.add(jack, "Why?");
		// To make sure all other questions have a different timestamp
		sysInfo.year(2010).month(10).day(4).hour(0).minute(0).second(5);
	}

	@Test
	public void shouldDetermineMaxIndexZeroForThreeQuestions() {
		List<Question> questions = produceQuestionList(3);
		assertEquals(3, questions.size());
		int maxIndex = Tools.determineMaximumIndex(questions, questionsPerPage);
		assertEquals(0, maxIndex);
	}

	@Test
	public void shouldDetermineMaxIndexZeroForZeroQuestions() {
		int maxIndex = Tools.determineMaximumIndex(this.questionDB.all(),
				questionsPerPage);
		jack.delete();
		assertEquals(0, this.questionDB.all().size());
		assertEquals(0, maxIndex);
	}

	@Test
	public void shouldDetermineMaxIndexTwoForThreePages() {
		int maxIndex = Tools.determineMaximumIndex(
				this.produceQuestionList(42), questionsPerPage);
		assertEquals(2, maxIndex);
	}

	@Test
	public void shouldDetermineMaxIndexThreeForFourPages() {
		int maxIndex = Tools.determineMaximumIndex(
				this.produceQuestionList(46), questionsPerPage);
		assertEquals(3, maxIndex);
	}

	@Test
	public void shouldBeOfSizeThree() {
		List<Question> questions = Tools.paginate(this.produceQuestionList(3),
				questionsPerPage, 0);
		assertEquals(3, questions.size());
	}

	@Test
	public void shouldPaginateCorrectly() {
		List<Question> questionList = this.produceQuestionList(18);
		List<Question> questions = Tools.paginate(questionList,
				questionsPerPage, 0);
		assertEquals(15, questions.size());
		questions = Tools.paginate(questionList, questionsPerPage, 1);
		assertEquals(3, questions.size());

	}

	@Test
	public void shouldPaginateCorrectlyForThreePages() {
		List<Question> questionList = this.produceQuestionList(45);
		List<Question> questions = Tools.paginate(questionList,
				questionsPerPage, 0);
		assertEquals(15, questions.size());
		questions = Tools.paginate(questionList, questionsPerPage, 1);
		assertEquals(15, questions.size());
		questions = Tools.paginate(questionList, questionsPerPage, 2);
		assertEquals(15, questions.size());
	}

	@Test
	public void shouldPaginateCorrectlyForZeroQuestions() {
		jack.delete();
		int index = 0;
		List<Question> questions = Tools.paginate(this.questionDB.all(),
				questionsPerPage, index);
		assertEquals(0, questions.size());
	}

	@Test
	public void shouldPaginateCorrectlyForOneQuestion() {
		question1.delete();
		question3.delete();
		int index = 0;
		List<Question> questions = Tools.paginate(this.questionDB.all(),
				questionsPerPage, index);
		assertEquals(1, questions.size());
	}

	@Test
	public void shouldPaginateCorrectlyForFifteenQuestions() {
		List<Question> questionList = this.produceQuestionList(15);
		List<Question> questions = Tools.paginate(questionList,
				questionsPerPage, 0);
		assertEquals(15, questions.size());
		questions = Tools.paginate(questionList, questionsPerPage, 1);
		assertEquals(0, questions.size());
	}

	@Test
	public void shouldPaginateCorrectlyForSixteenQuestions() {
		List<Question> questionList = this.produceQuestionList(16);
		List<Question> questions = Tools.paginate(questionList,
				questionsPerPage, 0);
		assertEquals(15, questions.size());
		questions = Tools.paginate(questionList, questionsPerPage, 1);
		assertEquals(1, questions.size());
	}

	@Test
	public void shouldYieldEmptyPagesAfterEnd() {
		List<Question> questions = Tools.paginate(this.questionDB.all(),
				questionsPerPage, 1);
		assertEquals(0, questions.size());
	}

	private List<Question> produceQuestionList(int size) {
		List<Question> questions = new ArrayList<Question>();
		for (int i = 0; i < size; i++) {
			questions.add(new Question(this.jack, "Question " + i));
		}
		return questions;
	}
}
