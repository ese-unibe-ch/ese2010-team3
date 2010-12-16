package tests;

import models.Question;
import models.User;

import org.junit.Test;

import play.mvc.Http.Response;
import play.test.FunctionalTest;

public class ApplicationTest extends FunctionalTest {

	@Test
	public void testThatIndexPageLoads() {
		Response response = GET("/");
		assertIsOk(response);
		assertContentType("text/html", response);
		assertCharset("utf-8", response);
	}

	@Test
	public void testThatQuestionPageLoads() {
		Question question = new Question(new User("Jack"), "what's up?");
		Response response = GET("/question/" + question.id());
		assertIsOk(response);
		assertContentType("text/html", response);
		assertCharset("utf-8", response);
	}

}