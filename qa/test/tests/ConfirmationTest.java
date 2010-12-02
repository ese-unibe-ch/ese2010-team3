package tests;

import static org.junit.Assert.*;
import models.ISystemInformation;
import models.Question;
import models.SystemInformation;
import models.User;
import models.database.Database;
import models.helpers.Tools;
import net.sf.oval.constraint.AssertFalse;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import play.test.UnitTest;
import tests.mocks.SystemInformationMock;

public class ConfirmationTest extends UnitTest {
	
	private ISystemInformation savedSysInfo;
	private User norbert;
	private User andrew;

	@Before
	public void setUp() {
		savedSysInfo = SystemInformation.get();
		norbert = new User("Norbert", "norbert");
		andrew = new User("Andrew", "andrew");
	}
	
	@After
	public void tearDown() {
		SystemInformation.mockWith(savedSysInfo);
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
