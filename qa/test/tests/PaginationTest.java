package tests;

import java.util.List;

import models.ISystemInformation;
import models.Question;
import models.SystemInformation;
import models.User;
import models.database.Database;
import models.helpers.Tools;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import play.test.UnitTest;
import tests.mocks.SystemInformationMock;

public class PaginationTest extends UnitTest {

	private Question question1;
	private Question question2;
	private Question question3;
	User jack = new User("Jack", "jack");
	private int questionsPerPage;
	private ISystemInformation savedSysInfo;

	@Before
	public void setup() {
		savedSysInfo = SystemInformation.get();
		SystemInformationMock sys = new SystemInformationMock();
		SystemInformation.mockWith(sys);
		questionsPerPage = 15;
		sys.year(2010).month(9).day(3).hour(0).minute(0).second(0);
		question3 = new Question(jack, "Who?");
		sys.year(2010).month(10).day(3).hour(0).minute(0).second(0);
		question2 = new Question(jack, "Where?");
		sys.year(2010).month(10).day(3).hour(0).minute(0).second(5);
		question1 = new Question(jack, "Why?");
		// To make sure all other questions have a different timestamp
		sys.year(2010).month(10).day(4).hour(0).minute(0).second(5);
	}

	@After
	public void tearDown() {
		Database.clear();
	}

	@Test
	public void shouldBeOfSizeThree() {
		int index = 0;
		List<Question> questions = Tools.paginate(Database.get().questions()
				.all(), questionsPerPage, index);
		assertEquals(3, questions.size());
	}

	@Test
	public void shouldBeLatestAnswerFirst() {
		int index = 0;
		List<Question> questions = Tools.paginate(Database.get().questions()
				.all(), questionsPerPage, index);
		for (Question q : questions) {
			System.out.println(q.content());
		}
		assertEquals(question1, questions.get(0));
	}

	@Test
	public void shouldPaginateCorrectly() {
		for (int i = 0; i < 15; i++) {
			@SuppressWarnings("unused")
			Question question4 = new Question(jack,
					"Could you repeat this please?");
		}
		int index = 0;
		List<Question> questions = Tools.paginate(Database.get().questions()
				.all(), questionsPerPage, index);
		assertEquals(15, questions.size());
		index = 1;
		questions = Tools.paginate(Database.get().questions().all(),
				questionsPerPage, index);
		assertEquals(3, questions.size());
		assertEquals(question1, questions.get(0));
		assertEquals(question2, questions.get(1));
		assertEquals(question3, questions.get(2));
	}

	@Test
	public void shouldPaginateCorrectlyForThreePages() {
		for (int i = 0; i < 42; i++) {
			@SuppressWarnings("unused")
			Question question4 = new Question(jack,
					"Could you repeat this please?");
		}
		int index = 0;
		List<Question> questions = Tools.paginate(Database.get().questions()
				.all(), questionsPerPage, index);
		assertEquals(15, questions.size());
		index = 1;
		questions = Tools.paginate(Database.get().questions().all(),
				questionsPerPage, index);
		assertEquals(15, questions.size());
		index = 2;
		questions = Tools.paginate(Database.get().questions().all(),
				questionsPerPage, index);
		assertEquals(15, questions.size());
		assertEquals(question1, questions.get(12));
		assertEquals(question2, questions.get(13));
		assertEquals(question3, questions.get(14));
	}
}
