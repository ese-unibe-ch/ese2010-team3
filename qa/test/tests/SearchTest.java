package tests;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import models.Question;
import models.User;
import models.SearchEngine.SearchFilter;
import models.database.Database;
import models.helpers.Mapper;

import org.junit.Before;
import org.junit.Test;

public class SearchTest extends MockedUnitTest {

	private Question fulltextPositive;
	private Question fulltextNegative;
	private Question taggedNegative;
	private Question taggedPositive;

	@Before
	public void setUp() {
		Database.clear();
		User jack = new User("Jack","");
		User jill = new User("Jill","");
		fulltextPositive = new Question(jack,"This is relevant.");
		fulltextNegative = new Question(jill,"This is not.");
		taggedPositive   = new Question(jack,"This is about an important thing.");
		taggedNegative   = new Question(jack,"This is not about anything important.");
		taggedPositive.setTagString("relevant");
		taggedNegative.setTagString("plop");
		fulltextPositive.answer(jill, "My answer");
	}

	@Test
	public void shouldFindFulltext() {
		assertTrue(Database.questions().searchFor("relevant").contains(
				fulltextPositive));
	}

	@Test
	public void shouldntFindFulltextNegative() {
		assertFalse(Database.questions().searchFor("relevant").contains(
				fulltextNegative));
	}

	@Test
	public void shouldFindByTag() {
		assertTrue(Database.questions().searchFor("relevant")
				.contains(taggedPositive));
	}

	@Test
	public void shouldFindByTagOnly() {
		assertFalse(Database.questions().searchFor("tag:relevant")
				.contains(fulltextPositive));
		assertTrue(Database.questions().searchFor("tag:relevant")
				.contains(taggedPositive));
	}

	@Test
	public void shouldntFindByTagNegative() {
		assertFalse(Database.questions().searchFor("relevant")
				.contains(taggedNegative));
	}

	@Test
	public void shouldntSearchForStupidWords() {
		assertTrue(Database.questions().searchFor("is").isEmpty());
		assertTrue(Database.questions().searchFor("???").isEmpty());
	}
	
	@Test
	public void shouldSearchMixedWord() {
		assertTrue(Database.questions().searchFor("is relevant").contains(fulltextPositive));
		assertTrue(Database.questions().searchFor("is relevant").contains(taggedPositive));
		assertTrue(Database.questions().searchFor("??? relevant")
				.contains(taggedPositive));
	}

	@Test
	public void shouldBeANDSearch() {
		assertEquals(Database.questions().searchFor("relevant").size(), 2);
		List<Question> relevantImportant = Database.questions()
				.searchFor("relevant important");
		assertEquals(relevantImportant.size(), 1);
		assertTrue(relevantImportant.contains(taggedPositive));
		assertTrue(Database.questions().searchFor("relevant dummy")
				.isEmpty());
	}

	@Test
	public void shouldFindUsername() {
		List<Question> jills = Database.questions().searchFor("jill");
		assertEquals(jills.size(), 2);
		assertTrue(jills.contains(fulltextPositive));
		assertTrue(jills.contains(fulltextNegative));

		List<Question> jackImportant = Database.questions()
				.searchFor("jack important");
		assertEquals(jackImportant.size(), 2);
		assertTrue(jackImportant.contains(taggedPositive));
		assertTrue(jackImportant.contains(taggedNegative));

		List<Question> jackTagged = Database.questions()
				.searchFor("jack tag:plop");
		assertEquals(jackTagged.size(), 1);
		assertTrue(jackTagged.contains(taggedNegative));
	}

	@Test
	public void shouldNotSearchInTags() {
		List<Question> questions = Database.questions().all();
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
		Question question = new Question(null, "about tag 'relevant'");
		Set<String> terms = new HashSet();
		terms.add("tag:");
		List<Question> found = Database.questions().searchFor("tag:");
		assertEquals(1, found.size());
		assertTrue(found.contains(question));
	}
}
