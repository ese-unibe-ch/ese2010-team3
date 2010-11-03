package tests;

import java.util.List;

import models.Question;
import models.Tag;
import models.User;
import models.database.Database;
import models.helpers.SetOperations;

import org.junit.Before;
import org.junit.Test;

import play.test.UnitTest;

public class TagTest extends UnitTest {

	private Question question1;
	private Question question2;
	private User douglas;
	private String tagName;

	@Before
	public void setUp() {
		Database.clear();
		this.douglas = new User("Douglas", "douglas");
		this.question1 = new Question(this.douglas,
				"Why did the chicken cross the road?");
		this.question2 = new Question(this.douglas,
				"Is this question meaningless?");
		this.tagName = "tag";
	}

	@Test
	public void shouldHaveName() {
		Tag tag = new Tag(this.tagName);
		assertNotNull(tag.getName());
		assertEquals(tag.getName(), this.tagName);

		assertNull(Tag.get("space "));
		try {
			new Tag(null);
			assertTrue(false);
		} catch (IllegalArgumentException ex) {
			assertTrue(true);
		}
		try {
			new Tag("UpperCase");
			assertTrue(false);
		} catch (IllegalArgumentException ex) {
			assertTrue(true);
		}
		try {
			new Tag("012345678901234567890123456789012");
			assertTrue(false);
		} catch (IllegalArgumentException ex) {
			assertTrue(true);
		}
	}

	@Test
	public void shouldAssociateWithQuestions() {
		assertEquals(countTags(this.tagName), 0);

		assertEquals(this.question1.getTags().size(), 0);
		this.question1.setTagString(this.tagName);
		assertEquals(this.question1.getTags().size(), 1);
		assertEquals(countTags(this.tagName), 1);

		Tag tag1 = Tag.get(this.tagName);
		assertNotNull(tag1);
		assertTrue(this.question1.getTags().contains(tag1));
		assertTrue(tag1.getQuestions().contains(this.question1));
		assertFalse(this.question2.getTags().contains(tag1));
		assertFalse(tag1.getQuestions().contains(this.question2));

		this.question2.setTagString(this.tagName);
		assertEquals(countTags(this.tagName), 1);

		assertTrue(this.question1.getTags().contains(tag1));
		assertTrue(tag1.getQuestions().contains(this.question1));
		assertTrue(this.question2.getTags().contains(tag1));
		assertTrue(tag1.getQuestions().contains(this.question2));
		assertEquals(tag1.getQuestions().size(), 2);

		assertEquals(this.question1.getTags().size(), 1);
		assertEquals(this.question2.getTags().size(), 1);
		assertEquals(this.question1.getTags().get(0), this.question2.getTags()
				.get(0));

		this.question1.setTagString("");
		assertEquals(this.question1.getTags().size(), 0);
		assertFalse(this.question1.getTags().contains(tag1));
		assertFalse(tag1.getQuestions().contains(this.question1));
		assertTrue(this.question2.getTags().contains(tag1));
		assertTrue(tag1.getQuestions().contains(this.question2));
		assertEquals(countTags(this.tagName), 1);

		this.question2.setTagString("");
		assertEquals(this.question2.getTags().size(), 0);
		assertFalse(this.question2.getTags().contains(tag1));
		assertFalse(tag1.getQuestions().contains(this.question2));
		assertTrue(tag1.getQuestions().isEmpty());

		assertEquals(countTags(this.tagName), 0);
	}

	@Test
	public void shouldOrderAlphabetically() {
		Tag tagC = Tag.get("c" + this.tagName);
		Tag tagA = Tag.get("a" + this.tagName);
		Tag tagB = Tag.get("b" + this.tagName);

		this.question1.setTagString(tagC.getName() + " " + tagA.getName() + ","
				+ tagB.getName());
		assertEquals(this.question1.getTags().get(0), tagA);
		assertEquals(this.question1.getTags().get(1), tagB);
		assertEquals(this.question1.getTags().get(2), tagC);
		this.question1.setTagString(null);
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

		questionK.setTagString("J K Z");
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
		Database.clear();
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
		
		Question[] possibility1 = {questionB,questionF,questionC,questionD,questionE};
		Question[] possibility2 = {questionF,questionB,questionC,questionD,questionE};
		List<Question> similar = Database.get().questions().findSimilar(questionA);
		assertTrue(SetOperations.arrayEquals(possibility1,similar.toArray()) ||
				SetOperations.arrayEquals(possibility2,similar.toArray()));
	}


	private static int countTags(String name) {
		int count = 0;
		for (Tag tag : Tag.tags())
			if (tag.getName().equals(name))
				count++;
		return count;
	}
}
