package tests;
import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import models.*;
import play.test.*;
import tests.mocks.SystemInformationMock;


public class AnswerTest extends UnitTest {

	private User james;
	private Question question;
	private Answer answer;
	private Date questionDate;
	private Date answerDate;
	private ISystemInformation original;

	@Before
	public void setUp() {
		original = SystemInformation.get();
		SystemInformationMock sys = new SystemInformationMock();
		SystemInformation.mockWith(sys);
		this.james = new User("James", "jack");
		sys.year(2000).month(6).day(6).hour(12).minute(0).second(0);;
		questionDate = sys.now();
		sys.changeTo(questionDate);
		this.question = new Question(new User("Jack", "jack"), "Why did the chicken cross the road?");
		sys.minute(5);
		answerDate = sys.now();
		sys.changeTo(answerDate);
		this.answer = this.question.answer(james, "To get to the other side.");
	}
	
	@Test
	public void shouldCreateAnswer() {
		assertTrue(answer != null);
	}
	
	@Test
	public void shouldHaveCorrectContent() {
		assertEquals(answer.content(), "To get to the other side.");
	}
	
	@Test
	public void shouldHaveOwner() {
		assertEquals(this.answer.owner(), this.james);
	}
	
	@Test
	public void shouldHaveQuestion() {
		assertEquals(this.answer.question(), this.question);
	}
	
	@Test
	public void shouldHaveTimestamp() {
		assertEquals(answer.timestamp(),answerDate);
	}
	
	@Test
	public void shouldRegisterItself() {
		assertTrue(this.james.hasItem(this.answer));
		assertTrue(this.question.hasAnswer(this.answer));
	}
	
	@Test
	public void shouldFindAnswer() {
		assertEquals(this.answer,question.getAnswer(this.answer.id()));
	}
	
	@After
	public void tearDown() {
		SystemInformation.mockWith(original);
	}
	
}
