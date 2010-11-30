package tests;

import models.helpers.Tools;

import org.junit.Test;

import play.test.UnitTest;

public class ToolsTest extends UnitTest {

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

}
