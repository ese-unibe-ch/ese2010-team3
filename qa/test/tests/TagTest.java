package tests;

import java.util.List;

import models.Question;
import models.Tag;
import models.User;
import models.database.IQuestionDatabase;
import models.database.ITagDatabase;
import models.database.HotDatabase.HotQuestionDatabase;
import models.database.HotDatabase.HotTagDatabase;
import models.helpers.SetOperations;

import org.junit.Before;
import org.junit.Test;

public class TagTest extends MockedUnitTest {

	private ITagDatabase tagDB;
	private Question question1;
	private Question question2;
	private User douglas;
	private String tagName;

	@Before
	public void setUp() {
		tagDB = new HotTagDatabase();
		douglas = new User("Douglas");
		question1 = new Question(douglas,
				"Why did the chicken cross the road?", tagDB, null);
		question2 = new Question(douglas, "Is this question meaningless?",
				tagDB, null);
		tagName = "tag";
	}

	@Test
	public void shouldHaveName() {
		Tag tag = new Tag(tagName, null);
		assertNotNull(tag.getName());
		assertEquals(tag.getName(), tagName);
		assertNull(tagDB.get("space "));

		String invalidNames[] = { null, "UpperCase",
				"012345678901234567890123456789012" };
		for (String name : invalidNames) {
			boolean hasThrown = false;
			try {
				new Tag(name, null);
			} catch (IllegalArgumentException ex) {
				hasThrown = true;
			}
			assertTrue("Should throw for name " + name, hasThrown);
		}
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
		IQuestionDatabase questionDB = new HotQuestionDatabase(this.tagDB);
		User A = new User("A");
		User B = new User("B");
		User C = new User("C");
		User D = new User("D");
		Question questionK = questionDB.add(A, "K?");
		Question questionL = questionDB.add(B, "L?");
		Question questionM = questionDB.add(C, "M?");
		Question questionN = questionDB.add(D, "N?");
		Question questionO = questionDB.add(D, "O?");

		questionK.setTagString(" J K Z");
		questionL.setTagString(" ");
		questionM.setTagString(" ");
		questionN.setTagString("");
		questionO.setTagString("");

		List<Question> similarK = questionDB.findSimilar(questionK);
		List<Question> similarL = questionDB.findSimilar(questionL);
		List<Question> similarM = questionDB.findSimilar(questionM);
		List<Question> similarN = questionDB.findSimilar(questionN);
		List<Question> similarO = questionDB.findSimilar(questionO);

		assertTrue(similarK.isEmpty());
		assertTrue(similarL.isEmpty());
		assertTrue(similarM.isEmpty());
		assertTrue(similarN.isEmpty());
		assertTrue(similarO.isEmpty());
	}

	@Test
	public void shouldListCorrectOrderOfSimilarQuestions() {
		IQuestionDatabase questionDB = new HotQuestionDatabase(this.tagDB);
		User A = new User("A");
		User B = new User("B");
		User C = new User("C");
		User D = new User("D");
		Question questionA = questionDB.add(A, "A?");
		Question questionB = questionDB.add(B, "B?");
		Question questionC = questionDB.add(C, "C?");
		Question questionD = questionDB.add(D, "D?");
		Question questionE = questionDB.add(D, "E?");
		Question questionF = questionDB.add(A, "F?");

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
		List<Question> similar = questionDB.findSimilar(questionA);
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

	@Test
	public void shouldSuggestTags() {
		question1.setTagString("tag1 tag2 nag3");
		List<String> tagNames = tagDB.suggestTagNames("ta");
		assertEquals(2, tagNames.size());
		assertEquals("tag1", tagNames.get(0));
		assertEquals("tag2", tagNames.get(1));

		tagNames = tagDB.suggestTagNames("TA");
		assertEquals(2, tagNames.size());
		assertEquals("tag1", tagNames.get(0));
		assertEquals("tag2", tagNames.get(1));

		tagNames = tagDB.suggestTagNames("Na");
		assertEquals(1, tagNames.size());
		assertEquals("nag3", tagNames.get(0));

		tagNames = tagDB.suggestTagNames("tag1");
		assertEquals(1, tagNames.size());
		assertEquals("tag1", tagNames.get(0));

		tagNames = tagDB.suggestTagNames(null);
		assertEquals(3, tagNames.size());
		assertEquals("nag3", tagNames.get(0));
		assertEquals("tag1", tagNames.get(1));
		assertEquals("tag2", tagNames.get(2));
	}

	@Test
	public void shouldAllowStandaloneTags() {
		Tag tag = new Tag(tagName, null);
		tag.register(question1);
		assertEquals(1, tag.getQuestions().size());
		tag.unregister(question1);
		assertEquals(0, tag.getQuestions().size());
	}

	private int countTags(String name) {
		int count = 0;
		for (Tag tag : tagDB.all())
			if (tag.getName().equals(name))
				count++;
		return count;
	}
}
