package tests;

import models.SysInfo;
import models.SystemInformation;
import models.User;
import models.helpers.Tools;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import play.test.UnitTest;
import tests.mocks.SystemInformationMock;

public class ConfirmationTest extends UnitTest {
	
	private SystemInformation sys;
	private SystemInformation savedSysInfo;
	private User norbert;
	private User andrew;

	@Before
	public void setUp() {
		sys = new SystemInformationMock();
		savedSysInfo = SysInfo.mockWith(sys);
		norbert = new User("Norbert", "norbert");
		andrew = new User("Andrew", "andrew");
	}
	
	@After
	public void tearDown() {
		SysInfo.mockWith(savedSysInfo);
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
