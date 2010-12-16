package tests;

import java.text.ParseException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import models.helpers.Mapper;
import models.helpers.SetOperations;
import models.helpers.Tools;

import org.junit.Test;

public class ToolsTest extends MockedUnitTest {

	@Test
	public void shouldConvertMarkdown() {
		assertEquals(Tools.markdownToHtml("*italic*"), "<p><em>italic</em></p>");
		assertEquals(Tools.markdownToHtml("**bold**"),
				"<p><strong>bold</strong></p>");
		assertEquals(Tools.markdownToHtml("<code>code</code>"),
				"<p><code>code</code></p>");
		assertEquals(Tools.markdownToHtml("<script>alert('XSS')</script>"), "");
		assertEquals(Tools.markdownToHtml("&lt;"), "<p>&lt;</p>");
		assertEquals(Tools.markdownToHtml("<"), "<p>&lt;</p>");
	}

	@Test
	public void shouldStripHtml() {
		assertEquals(Tools.htmlToText("<p><em>italic</em></p>"), "italic");
		assertEquals(Tools.htmlToText("<p><code>code</code></p>"), "code");
		assertEquals(Tools.htmlToText("<script>alert('XSS')</script>"), "");
		assertEquals(Tools.htmlToText("<p>&lt;</p>"), "<");
	}

	@Test
	public void shouldConvertDateToString() throws ParseException {
		long dec6 = 1291590000000L;
		assertEquals(Tools.stringToDate("06.12.2010").getTime(), dec6);
		assertEquals(Tools.stringToDate("12/06/2010").getTime(), dec6);
		assertEquals(Tools.stringToDate("2010-12-06").getTime(), dec6);
		assertNull(Tools.stringToDate("Dec 6 2010"));
		assertEquals(Tools.dateToString(new Date(dec6)), "06.12.2010");
		assertNull(Tools.dateToString(null));
	}

	@Test
	public void shouldKnowSetOperations() {
		Set<String> set1 = new HashSet();
		Set<String> set2 = new HashSet();
		Set<String> set3 = new HashSet();
		for (int i = 0; i < 3; i++)
			set1.add("item " + i);
		for (int i = 0; i < 5; i++)
			set2.add("item " + i);

		assertTrue(SetOperations.isSubset(set1, set2));
		assertFalse(SetOperations.isSubset(set2, set1));
		assertFalse(SetOperations.isSuperset(set1, set2));
		assertTrue(SetOperations.isSuperset(set2, set1));
		assertTrue(SetOperations.containsAny(set1, set2));
		assertTrue(SetOperations.containsAny(set2, set1));
		assertFalse(SetOperations.containsAny(set1, set3));
		assertFalse(SetOperations.containsAny(set3, set1));

		set3 = SetOperations.union(set1, set2);
		assertEquals(SetOperations.difference(set3, set2).size(), 0);
		set3 = SetOperations.symDifference(set1, set2);
		assertEquals(SetOperations.intersection(set3, set1).size(), 0);
		assertEquals(SetOperations.intersection(set3, set2).size(), 2);
		
		Integer a1[] = { 1, 2 };
		Integer a2[] = { 1, 2, 3 };
		Integer a3[] = { 1, 2, 4 };
		assertTrue(SetOperations.arrayEquals(a1, a1));
		assertFalse(SetOperations.arrayEquals(a1, a2));
		assertFalse(SetOperations.arrayEquals(a2, a3));
	}

	@Test
	public void shouldDigestPasswords() {
		// Source: http://en.wikipedia.org/wiki/Examples_of_SHA_digests
		assertEquals(Tools.encrypt(""),
				"da39a3ee5e6b4b0d3255bfef95601890afd80709");
		assertNotSame(Tools.encrypt("password"), Tools.encrypt("Password"));

		// Source: http://en.wikipedia.org/wiki/MD5#MD5_hashes
		assertEquals(Tools.digest("", "MD5"),
				"d41d8cd98f00b204e9800998ecf8427e");
		assertNull(Tools.digest("a", "Cobertura-Dummy-Algo"));
	}

	@Test
	public void shouldMakeCoberturaHappy() {
		new Mapper();
		new Tools();
		new SetOperations();
	}
}
