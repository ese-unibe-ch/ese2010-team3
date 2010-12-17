package tests;

import java.text.ParseException;

import models.Answer;
import models.Question;
import models.SysInfo;
import models.Tag;
import models.User;
import models.database.IQuestionDatabase;
import models.database.IUserDatabase;
import models.database.HotDatabase.HotQuestionDatabase;
import models.database.HotDatabase.HotTagDatabase;
import models.database.HotDatabase.HotUserDatabase;
import models.helpers.ICleanup;
import models.helpers.Tools;

import org.junit.Before;
import org.junit.Test;

public class UserTest extends MockedUnitTest {

	private IQuestionDatabase questionDB;

	@Before
	public void setUp() {
		this.questionDB = new HotQuestionDatabase(new HotTagDatabase());
	}

	@Test
	public void shouldCreateUser() {
		User user = new User("Jack");
		assertTrue(user != null);
	}

	@Test
	public void shouldBeCalledJack() {
		User user = new User("Jack");
		assertEquals(user.getName(), "Jack");
	}

	@Test
	public void checkUsernameAvailable() {
		IUserDatabase userDB = new HotUserDatabase();
		assertTrue(userDB.isAvailable("JaneSmith"));
		userDB.register("JaneSmith", "janesmith", "jane@smith.com");
		assertFalse(userDB.isAvailable("JaneSmith"));
		assertFalse(userDB.isAvailable("janesmith"));
		assertFalse(userDB.isAvailable("jAnEsMiTh"));
	}

	@Test
	public void shouldCheckeMailValidation() {
		assertTrue(Tools.checkEmail("john@gmx.com"));
		assertTrue(Tools.checkEmail("john.smith@students.unibe.ch"));
		assertFalse(Tools.checkEmail("john@gmx.c"));
		assertFalse(Tools.checkEmail("john@info.museum"));
		assertFalse(Tools.checkEmail("john@...com"));
	}

	@Test
	public void checkMailAssertion() {
		User user = new User("Bill");
		user.setEmail("bill@aol.com");
		assertEquals(user.getEmail(), "bill@aol.com");
	}

	@Test
	public void checkPassw() {
		User user = new User("Bill", "bill", "bill@example.net", null);
		assertTrue(user.checkPW("bill"));
		assertEquals(Tools.encrypt("bill"), user.getSHA1Password());
		user.setSHA1Password("bill2");
		assertFalse(user.checkPW("bill"));
	}

	@Test
	public void shouldEditProfileCorrectly() throws ParseException {
		sysInfo.year(2010).month(12).day(3);

		User user = new User("Jack");
		assertEquals(user.getAge(), 0);
		assertNull(user.getBiographyHTML());

		user.setDateOfBirth("14.9.1987");
		user.setBiography("I lived");
		user.setEmail("test@test.tt");
		user.setEmployer("TestInc");
		user.setFullname("Test Tester");
		user.setProfession("tester");
		user.setWebsite("http://www.test.ch");

		assertEquals(user.getAge(), 23);
		assertEquals(user.getDateOfBirth(), "14.09.1987");
		assertEquals(user.getBiography(), "I lived");
		assertEquals(user.getBiographyHTML(), "<p>I lived</p>");
		assertEquals(user.getEmail(), "test@test.tt");
		assertEquals(user.getEmployer(), "TestInc");
		assertEquals(user.getFullname(), "Test Tester");
		assertEquals(user.getProfession(), "tester");
		assertEquals(user.getWebsite(), "http://www.test.ch");
	}

	@Test
	public void checkForSpammer() {
		User user = new User("Spammer");
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
		User user = new User("TheSupported");
		User user2 = new User("Cheater");
		assertFalse(user.isBlocked());
		assertFalse(user2.isBlocked());
		assertFalse(user2.isMaybeCheater());
		assertEquals(user.getStatusMessage(), "");
		assertEquals(user2.getStatusMessage(), "");
		for (int i = 0; i < 5; i++) {
			new Question(user, "This is my " + i + ". question").voteUp(user2);
		}
		SysInfo.setTestMode(false);
		assertTrue(user2.isMaybeCheater());
		assertTrue(user2.isCheating());
		assertTrue(user2.isBlocked());
		SysInfo.setTestMode(true);
		assertFalse(user2.isCheating());
		SysInfo.setTestMode(false);
		assertTrue(user2.isCheating());
		assertEquals(user2.getStatusMessage(), "User voted up somebody");
		assertFalse(user.isMaybeCheater());
		assertFalse(user.isCheating());
		assertFalse(user.isBlocked());
		assertEquals(user.getStatusMessage(), "");
	}
	
	@Test
	public void shouldAllowVotingOften() {
		User voter = new User("Voter");
		User user1 = new User("User1");
		User user2 = new User("User2");

		for (int i = 0; i < 5; i++) {
			new Question(user1, "Q1-" + i).voteUp(voter);
			new Question(user2, "Q2-" + i).voteUp(voter);
		}

		assertFalse(voter.isMaybeCheater());
		new Question(user1, "Q1-last").voteUp(voter);
		assertTrue(voter.isMaybeCheater());
	}

	@Test
	public void shouldNotBeAbleToEditForeignPosts() {
		User user1 = new User("Jack");
		User user2 = new User("John");
		User user3 = new User("Geronimo");
		user1.setModerator(true, null);
		Question q = new Question(user2, "Can you edit this post?");
		/* moderator should be able to edit the question */
		assertTrue(user1.canEdit(q));
		/* owner should be able to edit the question */
		assertTrue(user2.canEdit(q));
		/* blocked owner should not be able to edit the question */
		user2.block("for testing");
		assertFalse(user2.canEdit(q));
		/* user that is neither a moderator nor the owner of
		   the question should NOT be able to edit the question */
		assertFalse(user3.canEdit(q));
	}

	@Test
	public void shouldHaveOneQuestion() {
		User user = new User("Jack");
		Question q = new Question(user, "Why?");
		assertEquals(1, user.getQuestions().size());
		q.delete();
	}

	@Test
	public void shouldHaveNoQuestion() {
		User user = new User("Jack");
		Question q = new Question(user, "Why?");
		q.delete();
		assertEquals(0, user.getQuestions().size());
	}

	@Test
	public void shouldHaveOneAnswer() {
		User user = new User("Jack");
		Question q = new Question(user, "Why?");
		q.answer(user, "Because");
		assertEquals(1, user.getAnswers().size());
	}

	@Test
	public void shouldHaveNoAnswer() {
		User user = new User("Jack");
		Question q = new Question(user, "Why?");
		q.answer(user, "Because");
		q.answers().get(0).delete();
		assertEquals(0, user.getAnswers().size());
	}

	@Test
	public void shouldHaveOneBestAnswer() {
		User user = new User("Jack");
		Question q = new Question(user, "Why?");
		q.answer(user, "Because");
		q.setBestAnswer(q.answers().get(0));
		assertEquals(1, user.bestAnswers().size());
	}

	@Test
	public void shouldHaveNoBestAnswer() {
		User user = new User("Jack");
		Question q = new Question(user, "Why?");
		q.answer(user, "Because");
		q.setBestAnswer(q.answers().get(0));
		q.answers().get(0).delete();
		assertEquals(0, user.bestAnswers().size());
	}

	@Test
	public void testModerator() {
		User user = new User("Jack");
		assertFalse(user.isModerator());
		user.setModerator(true, null);
		assertTrue(user.isModerator());
	}

	@Test
	public void testBlock() {
		User user = new User("Jack");
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
	public void shouldHaveRecentEntries() {
		sysInfo.year(2000).month(6).day(6).hour(12).minute(0).second(0);

		User user = new User("Jack");
		assertEquals(0, user.getRecentQuestions().size());
		assertEquals(0, user.getRecentAnswers().size());
		assertEquals(0, user.getRecentComments().size());
		Question question = new Question(user, "Question");
		Answer answer = question.answer(user, "Answer");
		question.comment(user, "Comment");
		assertEquals(1, user.getRecentQuestions().size());
		assertEquals(1, user.getRecentAnswers().size());
		assertEquals(1, user.getRecentComments().size());

		for (int i = 0; i < 4; i++) {
			sysInfo.second(i);
			question.answer(user, "Answer " + i);
		}
		assertEquals(3, user.getRecentAnswers().size());
		assertFalse(user.getRecentAnswers().contains(answer));
	}

	@Test
	public void shouldHaveOneHighRatedAnswer() {
		User user = new User("Jack");
		Question q = this.questionDB.add(user, "Why?");
		q.answer(user, "Because");

		assertEquals(0, user.highRatedAnswers().size());
		assertTrue(this.questionDB.countHighRatedAnswers() == 0);

		User A = new User("A");
		User B = new User("B");
		User C = new User("C");
		User D = new User("D");
		User E = new User("E");

		q.answers().get(0).voteUp(A);
		q.answers().get(0).voteUp(B);
		q.answers().get(0).voteUp(C);
		q.answers().get(0).voteUp(D);
		q.answers().get(0).voteUp(E);

		assertEquals(1, user.highRatedAnswers().size());
		assertTrue(this.questionDB.countHighRatedAnswers() > 0);

		A.delete();
		B.delete();
		C.delete();
		D.delete();
		E.delete();

		assertEquals(0, user.highRatedAnswers().size());
	}

	@Test
	public void shouldSuggestQuestion() {
		User user3 = new User("User3");
		User user4 = new User("User4");
		User user5 = new User("User5");
		Question m = this.questionDB.add(user3, "Why?");
		Question n = this.questionDB.add(user4, "Where?");

		m.setTagString("demo");
		n.setTagString("demo demo2");
		m.answer(user3, "Because");
		m.answer(user4, "No idea");
		n.answer(user5, "Therefore");

		assertEquals(1, this.questionDB.suggestQuestions(user5).size());
		assertEquals(m, this.questionDB.suggestQuestions(user5).get(0));

		n.answer(user5, "and then some");
		assertEquals(1, this.questionDB.suggestQuestions(user5).size());
		assertEquals(m, this.questionDB.suggestQuestions(user5).get(0));
	}

	@Test
	public void shouldSuggestThreeQuestions() {
		User user3 = new User("User3");
		User user4 = new User("User4");
		User user5 = new User("User5");
		Question m = this.questionDB.add(user3, "Why?");
		Question n = this.questionDB.add(user4, "Where?");
		Question o = this.questionDB.add(user3, "Who?");
		Question p = this.questionDB.add(user4, "How old?");

		m.setTagString("demo");
		n.setTagString("demo demo2");
		o.setTagString("demo demo3 demo4");
		p.setTagString("demo demo3 demo4 demo5");
		m.answer(user3, "Because");
		m.answer(user4, "No idea");
		n.answer(user5, "Therefore");

		assertEquals(3, this.questionDB.suggestQuestions(user5).size());
		assertEquals(m, this.questionDB.suggestQuestions(user5).get(0));
	}

	@Test
	public void shouldSuggestSixQuestionsMax() {
		User user3 = new User("User3");
		User user5 = new User("User5");
		for (int i = 0; i < 10; i++) {
			Question q = this.questionDB.add(user3, "Hard question " + i);
			q.setTagString("demo");
		}

		Question q = this.questionDB.add(user3, "Simple question");
		q.setTagString("demo");
		q.answer(user5, "Simple!");

		assertEquals(6, this.questionDB.suggestQuestions(user5).size());
	}

	@Test
	public void shouldNotSuggestSameQuestionTwice() {
		User user5 = new User("User5");
		Question q = this.questionDB.add(null, "suggest me!");
		q.setTagString("demo");
		Question r = this.questionDB.add(null, "answer me!");
		r.setTagString("demo");
		r.answer(user5, "ok");
		Question s = this.questionDB.add(null, "answer me, too!");
		s.setTagString("demo");
		s.answer(user5, "ok");

		assertEquals(1, this.questionDB.suggestQuestions(user5).size());
		assertEquals(q, this.questionDB.suggestQuestions(user5).get(0));
	}

	@Test
	public void shouldNotSuggestOldQuestions() {
		sysInfo.year(2000).month(6).day(6).hour(12).minute(0).second(0);

		User user5 = new User("User5");
		Question q = this.questionDB.add(null, "suggest me!");
		q.setTagString("demo");

		Question r = this.questionDB.add(null, "answer me!");
		r.setTagString("demo");
		r.answer(user5, "ok");

		assertEquals(1, this.questionDB.suggestQuestions(user5).size());
		sysInfo.year(2001);
		assertEquals(0, this.questionDB.suggestQuestions(user5).size());
	}

	@Test
	public void shouldSuggestQuestionsFromBestAnswersFirst() {
		User user3 = new User("User3");
		User user4 = new User("User4");
		User user5 = new User("User5");
		Question m = this.questionDB.add(user3, "Why?");
		Question n = this.questionDB.add(user4, "Where?");
		Question o = this.questionDB.add(user3, "Who?");
		Question p = this.questionDB.add(user4, "How old?");

		m.setTagString("demo");
		n.setTagString("demo demo2");
		o.setTagString("demo9 demo8");
		p.setTagString("demo9 demo8");
		m.answer(user3, "Because");
		m.answer(user4, "No idea");
		n.answer(user5, "Therefore");
		o.answer(user5, "No");
		o.setBestAnswer(user5.getAnswers().get(1));
		assertEquals(2, this.questionDB.suggestQuestions(user5).size());
		assertEquals(p, this.questionDB.suggestQuestions(user5).get(0));
		assertEquals(m, this.questionDB.suggestQuestions(user5).get(1));

	}

	@Test
	public void shouldSuggestQuestionsSortedByRatingOfAnswers() {
		User user3 = new User("User3");
		User user4 = new User("User4");
		User user5 = new User("User5");
		Question m = this.questionDB.add(user3, "Why?");
		Question n = this.questionDB.add(user4, "Where?");
		Question o = this.questionDB.add(user3, "Who?");
		Question p = this.questionDB.add(user4, "How old?");
		Question q = this.questionDB.add(user3, "So?");
		Question r = this.questionDB.add(user4, "For ho long?");

		m.setTagString("demo");
		n.setTagString("demo demo2");
		o.setTagString("demo9 demo8");
		p.setTagString("demo9 demo8");
		q.setTagString("demo demo2 demo10");
		r.setTagString("tag");
		m.answer(user3, "Because");
		m.answer(user4, "No idea");
		n.answer(user5, "Therefore");
		o.answer(user5, "No");
		user5.getAnswers().get(1).voteUp(user3);
		user5.getAnswers().get(1).voteUp(user4);

		assertEquals(3, this.questionDB.suggestQuestions(user5).size());
		assertEquals(q, this.questionDB.suggestQuestions(user5).get(0));
		assertEquals(m, this.questionDB.suggestQuestions(user5).get(1));
		assertEquals(p, this.questionDB.suggestQuestions(user5).get(2));

	}

	@Test
	public void shouldNotSuggestQuestionsFromBadAnswers() {
		User user6 = new User("User6");
		User user7 = new User("User7");
		User user8 = new User("User8");
		Question m = this.questionDB.add(user6, "Why?");
		Question n = this.questionDB.add(user7, "Where?");
		Question o = this.questionDB.add(user6, "Who?");
		Question p = this.questionDB.add(user7, "How old?");
		m.answer(user6, "Because");
		m.answer(user7, "No idea");
		p.answer(user8, "Therefore");
		user8.getAnswers().get(0).voteDown(user6);

		m.setTagString("demo");
		n.setTagString("demo demo2");
		o.setTagString("demo demo3 demo4");
		p.setTagString("demo demo3 demo4 demo5");

		assertEquals(0, this.questionDB.suggestQuestions(user8).size());
	}

	@Test
	public void shouldNotSuggestOwnQuestions() {
		User user = new User("Jack");
		User user2 = new User("John");
		Question q = this.questionDB.add(user, "Why?");
		Question f = this.questionDB.add(user2, "Where?");
		q.setTagString("demo");
		f.setTagString("demo");
		q.answer(user2, "Because");
		assertEquals(0, this.questionDB.suggestQuestions(user2).size());

	}

	@Test
	public void shouldNotSuggestQuestionsWithBestAnswer() {
		User james = new User("James");
		User john = new User("John");
		User kate = new User("Kate");
		Question k = this.questionDB.add(james, "Why?");
		Question l = this.questionDB.add(john, "Where?");

		k.setTagString("demo");
		l.setTagString("demo");
		k.answer(james, "Because");
		k.setBestAnswer(k.answer(john, "No idea"));
		l.answer(kate, "Therefore");
		assertEquals(0, this.questionDB.suggestQuestions(kate).size());
	}

	@Test
	public void shouldNotSuggestQuestionsWithManyAnswers() {
		User user3 = new User("User3");
		User user5 = new User("User5");

		Question p = this.questionDB.add(user3, "Hard question");
		p.setTagString("demo");
		Question q = this.questionDB.add(user3, "Simple question");
		q.setTagString("demo");

		q.answer(user5, "Simple!");
		assertEquals(1, this.questionDB.suggestQuestions(user5).size());

		for (int i = 0; i < 9; i++) {
			p.answer(null, "anonymous genious!");
		}
		assertEquals(1, this.questionDB.suggestQuestions(user5).size());
		p.answer(null, "yet another anonymous genious!");
		assertEquals(0, this.questionDB.suggestQuestions(user5).size());
	}

	@Test
	public void shouldHaveDebugFriendly_toString() {
		User james = new User("James");
		Question question = new Question(james, "Why?");
		Answer answer = question.answer(james, "No idea");
		Tag tag = new Tag("tag", null);
		assertEquals(james.toString(), "U[James]");
		assertEquals(question.toString(), "Question(Why?)");
		assertEquals(answer.toString(), "Answer(No idea)");
		assertEquals(tag.toString(), "Tag(tag)");
	}

	@Test
	public void testPostAndSearchDelay() {
		sysInfo.year(2010).month(1).day(1).hour(1).minute(1).second(0);
		User james = new User("James");
		assertTrue(james.canPost());
		assertTrue(james.canSearchFor("search 1"));

		james.setLastPostTime(sysInfo.now());
		assertFalse(james.canPost());
		sysInfo.second(29);
		assertFalse(james.canPost());
		sysInfo.second(30);
		assertTrue(james.canPost());

		assertTrue(james.canPost());
		james.block("You are blocked for making a good Test coverage");
		assertFalse(james.canPost());
		james.unblock();
		assertTrue(james.canPost());

		sysInfo.year(2010).month(1).day(1).hour(1).minute(1).second(0);
		james.setLastSearch("search 1", sysInfo.now());
		assertFalse(james.canSearchFor("search 2"));
		sysInfo.second(14);
		assertFalse(james.canSearchFor("search 2"));
		sysInfo.second(15);
		assertTrue(james.canSearchFor("search 2"));
	}

	@Test
	public void testTimeToSearchAndPost() {
		sysInfo.year(2010).month(1).day(1).hour(1).minute(1).second(0);
		User james = new User("James");

		james.setLastSearch("search 1", sysInfo.now());
		assertEquals(james.timeToSearch(), 15);
		sysInfo.second(14);
		assertEquals(james.timeToSearch(), 1);
		assertFalse(james.canSearchFor("search 2"));
		assertTrue(james.canSearchFor("search 1"));
		sysInfo.second(15);
		assertEquals(james.timeToSearch(), 0);
		assertTrue(james.canSearchFor("search 2"));
		assertTrue(james.canSearchFor("search 1"));
		sysInfo.year(2010).month(1).day(1).hour(1).minute(1).second(0);
		james.setLastPostTime(sysInfo.now());
		assertEquals(james.timeToPost(), 30);
		sysInfo.second(29);
		assertEquals(james.timeToPost(), 1);
		assertFalse(james.canPost());
		sysInfo.second(30);
		assertEquals(james.timeToPost(), 0);
		assertTrue(james.canPost());
	}

	@Test
	public void testTestMode() {
		sysInfo.year(2010).month(1).day(1).hour(1).minute(1).second(0);
		User james = new User("James");
		sysInfo.setTestMode(false);
		assertFalse(sysInfo.isInTestMode());
		james.setLastPostTime(sysInfo.now());
		james.setLastSearch("search 1", sysInfo.now());
		assertFalse(james.canSearchFor("search 2"));
		assertFalse(james.canPost());
		sysInfo.setTestMode(false);
		assertEquals(sysInfo.isInTestMode(), false);
		sysInfo.setTestMode(true);
		assertEquals(sysInfo.isInTestMode(), true);
		assertTrue(sysInfo.isInTestMode());
		sysInfo.isInTestMode();
		james.setLastPostTime(sysInfo.now());
		james.setLastSearch("search 3", sysInfo.now());
		assertTrue(james.canSearchFor("search 4"));
		assertTrue(james.canPost());
	}

	@Test
	public void shouldNotWatchAnything() {
		User user = new User("Jack");
		assertEquals(0, this.questionDB.getWatchList(user).size());
	}

	@Test
	public void shouldMakeCoberturaHappy() {
		// this one's tricky, as Cobertura complains in an obscure way about the
		// bridge function User.cleanUp(java.lang.Object) not being called; see
		// http://sourceforge.net/tracker/?func=detail&atid=720015&aid=2015158&group_id=130558
		ICleanup user = new User("Jimmy");
		user.cleanUp(new Question(null, "question"));
	}
}
