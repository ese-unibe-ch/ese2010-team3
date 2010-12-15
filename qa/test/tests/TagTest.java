package tests;

import java.util.List;

import models.Question;
import models.Tag;
import models.User;
import models.database.Database;
import models.database.ITagDatabase;
import models.helpers.SetOperations;

import org.junit.Before;
import org.junit.Test;

import play.test.UnitTest;

public class TagTest extends UnitTest {

	private ITagDatabase tagDB;
	private Question question1;
	private Question question2;
	private User douglas;
	private String tagName;

	@Before
	public void setUp() {
		Database.clear();
		tagDB = Database.tags();
		douglas = new User("Douglas", "douglas");
		question1 = new Question(douglas, "Why did the chicken cross the road?");
		question2 = new Question(douglas, "Is this question meaningless?");
		tagName = "tag";
	}

	@Test
	public void shouldHaveName() {
		Tag tag = new Tag(tagName);
		assertNotNull(tag.getName());
		assertEquals(tag.getName(), tagName);

		assertNull(tagDB.get("space "));
		boolean hasThrown = false;
		try {
			new Tag(null);
		} catch (IllegalArgumentException ex) {
			hasThrown = true;
		}
		assertTrue(hasThrown);

		hasThrown = false;
		try {
			new Tag("UpperCase");
		} catch (IllegalArgumentException ex) {
			hasThrown = true;
		}
		assertTrue(hasThrown);

		hasThrown = false;
		try {
			new Tag("012345678901234567890123456789012");
		} catch (IllegalArgumentException ex) {
			hasThrown = true;
		}
		assertTrue(hasThrown);
	}

	@Test
	public void shouldAssociateWithQuestions() {
		assertEquals(countTags(tagName), 0);

		assertEquals(question1.getTags().size(), 0);
		question1.setTagString(tagName);
		assertEquals(question1.getTags().size(), 1);
		assertEquals(countTags(tagName), 1);

		Tag tag1 = tagDB.get(tagName);
		assertNotNull(tag1);
		assertTrue(question1.getTags().contains(tag1));
		assertTrue(tag1.getQuestions().contains(question1));
		assertFalse(question2.getTags().contains(tag1));
		assertFalse(tag1.getQuestions().contains(question2));

		question2.setTagString(tagName);
		assertEquals(countTags(tagName), 1);

		assertTrue(question1.getTags().contains(tag1));
		assertTrue(tag1.getQuestions().contains(question1));
		assertTrue(question2.getTags().contains(tag1));
		assertTrue(tag1.getQuestions().contains(question2));
		assertEquals(tag1.getQuestions().size(), 2);

		assertEquals(question1.getTags().size(), 1);
		assertEquals(question2.getTags().size(), 1);
		assertEquals(question1.getTags().get(0), question2.getTags().get(0));

		question1.setTagString("");
		assertEquals(question1.getTags().size(), 0);
		assertFalse(question1.getTags().contains(tag1));
		assertFalse(tag1.getQuestions().contains(question1));
		assertTrue(question2.getTags().contains(tag1));
		assertTrue(tag1.getQuestions().contains(question2));
		assertEquals(countTags(tagName), 1);

		question2.setTagString("");
		assertEquals(question2.getTags().size(), 0);
		assertFalse(question2.getTags().contains(tag1));
		assertFalse(tag1.getQuestions().contains(question2));
		assertTrue(tag1.getQuestions().isEmpty());

		assertEquals(countTags(tagName), 0);
	}

	@Test
	public void shouldSetTagString() {
		question1.setTagString("012345678901234567890123456789012");
		assertEquals(question1.getTags().get(0).getName(),
				"01234567890123456789012345678901");
		question1.setTagString("012345678901234567890123456789012");
		assertEquals(question1.getTags().size(), 1);
	}

	@Test
	public void shouldOrderAlphabetically() {
		Tag tagC = tagDB.get("c" + tagName);
		Tag tagA = tagDB.get("a" + tagName);
		Tag tagB = tagDB.get("b" + tagName);

		question1.setTagString(tagC.getName() + " " + tagA.getName() + ","
				+ tagB.getName());
		assertEquals(question1.getTags().get(0), tagA);
		assertEquals(question1.getTags().get(1), tagB);
		assertEquals(question1.getTags().get(2), tagC);
		question1.setTagString(null);
	}

	@Test
	public void shouldNotListQuestionWithZeroTags() {
		User A = new User("A", "a");
		User B = new User("B", "b");
		User C = new User("C", "c");
		User D = new User("D", "d");
		Question questionK = new Question(A, "K?");
		Question questionL = new Question(B, "L?");
		Question questionM = new Question(C, "M?");
		Question questionN = new Question(D, "N?");
		Question questionO = new Question(D, "O?");

		questionK.setTagString(" J K Z");
		questionL.setTagString(" ");
		questionM.setTagString(" ");
		questionN.setTagString("");
		questionO.setTagString("");

		List<Question> similarK = questionK.getSimilarQuestions();
		List<Question> similarL = questionL.getSimilarQuestions();
		List<Question> similarM = questionM.getSimilarQuestions();
		List<Question> similarN = questionN.getSimilarQuestions();
		List<Question> similarO = questionO.getSimilarQuestions();

		assertTrue(similarK.isEmpty());
		assertTrue(similarL.isEmpty());
		assertTrue(similarM.isEmpty());
		assertTrue(similarN.isEmpty());
		assertTrue(similarO.isEmpty());
	}

	@Test
	public void shouldListCorrectOrderOfSimilarQuestions() {
		User A = new User("A", "a");
		User B = new User("B", "b");
		User C = new User("C", "c");
		User D = new User("D", "d");
		Question questionA = new Question(A, "A?");
		Question questionB = new Question(B, "B?");
		Question questionC = new Question(C, "C?");
		Question questionD = new Question(D, "D?");
		Question questionE = new Question(D, "E?");
		Question questionF = new Question(A, "F?");

		questionA.setTagString("A B C D");
		questionB.setTagString("A B C D");
		questionC.setTagString("A B C");
		questionD.setTagString("A B");
		questionE.setTagString("A");
		// To check if duplicate values are allowed
		questionF.setTagString("A B C D");

		Question[] possibility1 = { questionB, questionF, questionC, questionD,
				questionE };
		Question[] possibility2 = { questionF, questionB, questionC, questionD,
				questionE };
		List<Question> similar = Database.questions().findSimilar(
				questionA);
		assertEquals(similar.size(), 5);
		assertTrue(SetOperations.arrayEquals(possibility1, similar.toArray())
				|| SetOperations.arrayEquals(possibility2, similar.toArray()));
	}

	@Test
	public void shouldIgnoreDuplicates() {
		question1.setTagString("double double double");
		assertEquals(countTags("double"), 1);
		assertEquals(question1.getTags().size(), 1);
		assertEquals(question1.getTags().get(0), tagDB.get("double"));
	}

	private int countTags(String name) {
		int count = 0;
		for (Tag tag : tagDB.all())
			if (tag.getName().equals(name))
				count++;
		return count;
	}
}
