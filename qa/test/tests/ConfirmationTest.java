package tests;

import models.User;
import models.helpers.Tools;

import org.junit.Before;
import org.junit.Test;

public class ConfirmationTest extends MockedUnitTest {
	
	private User norbert;
	private User andrew;

	@Before
	public void setUp() {
		norbert = new User("Norbert");
		andrew = new User("Andrew");
	}
	
	@Test
	public void shouldGenerateKey(){
		String key = Tools.randomStringGenerator(23);
		String key2 = Tools.randomStringGenerator(33);
		assertNotNull(key);
		assertNotNull(key2);
		assertEquals(key.length(), 23);
		assertEquals(key2.length(), 33);
	}
	
	@Test
	public void shouldNotConfirmed(){
		assertFalse(norbert.isConfirmed());
		assertFalse(andrew.isConfirmed());
	}
	
	@Test
	public void shouldConfirmUser(){
		norbert.confirm();
		assertTrue(norbert.isConfirmed());
	}
	
	@Test
	public void shouldHaveSameKey(){
		String key = Tools.randomStringGenerator(35);
		norbert.setConfirmKey(key);
		assertEquals(key, norbert.getConfirmKey());
	}

}
