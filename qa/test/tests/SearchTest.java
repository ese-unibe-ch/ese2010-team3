package tests;

import static org.junit.Assert.*;

import models.Question;
import models.User;

import org.junit.Before;
import org.junit.Test;

import play.test.UnitTest;

public class SearchTest extends UnitTest {

	private Question fulltextPositive;
	private Question fulltextNegative;
	private Question taggedNegative;
	private Question taggedPositive;

	@Before
	public void setUp() throws Exception {
		User jack = new User("Jack","");
		User jill = new User("Jill","");
		fulltextPositive = new Question(jack,"This is relevant.");
		fulltextNegative = new Question(jill,"This is not.");
		taggedPositive   = new Question(jack,"This is about an important thing.");
		taggedNegative   = new Question(jack,"This is not about anything important.");
		taggedPositive.setTagString("relevant");
		taggedNegative.setTagString("plop");
	}

	@Test
	public void shouldFindFulltext() {
		assertTrue(Question.searchFor("relevant").contains(fulltextPositive));
	}
	
	@Test
	public void shouldntFindFulltextNegative() {
		assertFalse(Question.searchFor("relevant").contains(fulltextNegative));
	}
	
	@Test
	public void shouldFindByTag() {
		assertTrue(Question.searchFor("relevant").contains(fulltextPositive));
	}
	
	@Test
	public void shouldntFindByTagNegative() {
		assertFalse(Question.searchFor("relevant").contains(fulltextNegative));
	}
	
	@Test
	public void shouldntSearchForStupidWords() {
		assertTrue(Question.searchFor("is").isEmpty());
	}
	
	@Test
	public void shouldSearchMixedWord() {
		assertTrue(Question.searchFor("is relevant").contains(fulltextPositive));
		assertTrue(Question.searchFor("is relevant").contains(taggedPositive));
	}
}
