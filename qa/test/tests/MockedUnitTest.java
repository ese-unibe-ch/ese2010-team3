package tests;

import models.SysInfo;
import models.SystemInformation;
import models.database.IDatabase;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import play.test.UnitTest;
import tests.mocks.SystemInformationMock;
import controllers.Database;

public abstract class MockedUnitTest extends UnitTest {

	private static IDatabase origDB;
	private static SystemInformation savedSysInfo;
	protected static SystemInformationMock sysInfo;

	@BeforeClass
	public static void classSetUp() {
		// make sure that no test tries to access the global database
		origDB = Database.swapWith(null);
		sysInfo = new SystemInformationMock();
		savedSysInfo = SysInfo.mockWith(sysInfo);
	}

	@AfterClass
	public static void classTearDown() {
		SysInfo.mockWith(savedSysInfo);
		Database.swapWith(origDB);
	}
}
