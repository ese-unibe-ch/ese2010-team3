package tests;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import models.Question;
import models.SearchFilter;
import models.User;
import models.database.IQuestionDatabase;
import models.database.HotDatabase.HotQuestionDatabase;
import models.database.HotDatabase.HotTagDatabase;
import models.helpers.Mapper;

import org.junit.Before;
import org.junit.Test;

public class SearchTest extends MockedUnitTest {

	private Question fulltextPositive;
	private Question fulltextNegative;
	private Question taggedNegative;
	private Question taggedPositive;
	private IQuestionDatabase questionDB;

	@Before
	public void setUp() {
		this.questionDB = new HotQuestionDatabase(new HotTagDatabase());
		User jack = new User("Jack");
		User jill = new User("Jill");
		fulltextPositive = this.questionDB.add(jack, "This is relevant.");
		fulltextNegative = this.questionDB.add(jill, "This is not.");
		taggedPositive = this.questionDB.add(jack,
				"This is about an important thing.");
		taggedNegative = this.questionDB.add(jack,
				"This is not about anything important.");
		taggedPositive.setTagString("relevant");
		taggedNegative.setTagString("plop");
		fulltextPositive.answer(jill, "My answer");
	}

	@Test
	public void shouldFindFulltext() {
		assertTrue(this.questionDB.searchFor("relevant").contains(
				fulltextPositive));
	}

	@Test
	public void shouldntFindFulltextNegative() {
		assertFalse(this.questionDB.searchFor("relevant").contains(
				fulltextNegative));
	}

	@Test
	public void shouldFindByTag() {
		assertTrue(this.questionDB.searchFor("relevant")
				.contains(taggedPositive));
	}

	@Test
	public void shouldFindByTagOnly() {
		assertFalse(this.questionDB.searchFor("tag:relevant")
				.contains(fulltextPositive));
		assertTrue(this.questionDB.searchFor("tag:relevant")
				.contains(taggedPositive));
	}

	@Test
	public void shouldntFindByTagNegative() {
		assertFalse(this.questionDB.searchFor("relevant")
				.contains(taggedNegative));
	}

	@Test
	public void shouldntSearchForStupidWords() {
		assertTrue(this.questionDB.searchFor("is").isEmpty());
		assertTrue(this.questionDB.searchFor("???").isEmpty());
	}
	
	@Test
	public void shouldSearchMixedWord() {
		assertTrue(this.questionDB.searchFor("is relevant").contains(
				fulltextPositive));
		assertTrue(this.questionDB.searchFor("is relevant").contains(
				taggedPositive));
		assertTrue(this.questionDB.searchFor("??? relevant")
				.contains(taggedPositive));
	}

	@Test
	public void shouldBeANDSearch() {
		assertEquals(this.questionDB.searchFor("relevant").size(), 2);
		List<Question> relevantImportant = this.questionDB
				.searchFor("relevant important");
		assertEquals(relevantImportant.size(), 1);
		assertTrue(relevantImportant.contains(taggedPositive));
		assertTrue(this.questionDB.searchFor("relevant dummy").isEmpty());
	}

	@Test
	public void shouldFindUsername() {
		List<Question> jills = this.questionDB.searchFor("jill");
		assertEquals(jills.size(), 2);
		assertTrue(jills.contains(fulltextPositive));
		assertTrue(jills.contains(fulltextNegative));

		List<Question> jackImportant = this.questionDB
				.searchFor("jack important");
		assertEquals(jackImportant.size(), 2);
		assertTrue(jackImportant.contains(taggedPositive));
		assertTrue(jackImportant.contains(taggedNegative));

		List<Question> jackTagged = this.questionDB.searchFor("jack tag:plop");
		assertEquals(jackTagged.size(), 1);
		assertTrue(jackTagged.contains(taggedNegative));
	}

	@Test
	public void shouldNotSearchInTags() {
		List<Question> questions = this.questionDB.all();
		assertNotSame(questions.size(), 0);
		Set<String> terms = new HashSet();
		terms.add("relevant");
		List<Question> tagLess = Mapper.sort(questions, new SearchFilter(terms,
				null));
		assertEquals(tagLess.size(), 1);
		assertTrue(tagLess.contains(fulltextPositive));
	}

	@Test
	public void shouldHandleNullQuestion() {
		Question question = new Question(null, null);
		Set<String> terms = new HashSet();
		terms.add("relevant");
		assertNull(new SearchFilter(terms, null).visit(question));
	}

	@Test
	public void shouldHandleInvalidSyntax() {
		Question question = this.questionDB.add(null, "about tag 'relevant'");
		Set<String> terms = new HashSet();
		terms.add("tag:");
		List<Question> found = this.questionDB.searchFor("tag:");
		assertEquals(1, found.size());
		assertTrue(found.contains(question));
	}
}
