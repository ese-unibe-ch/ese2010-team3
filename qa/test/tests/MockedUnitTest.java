package tests;

import models.SysInfo;
import models.SystemInformation;
import models.database.Database;
import models.database.IDatabase;
import models.database.HotDatabase.HotDatabase;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import play.test.UnitTest;
import tests.mocks.SystemInformationMock;

public abstract class MockedUnitTest extends UnitTest {

	private static IDatabase origDB;
	private static SystemInformation savedSysInfo;
	protected static SystemInformationMock sysInfo;

	@BeforeClass
	public static void classSetUp() {
		origDB = Database.swapWith(new HotDatabase());
		sysInfo = new SystemInformationMock();
		savedSysInfo = SysInfo.mockWith(sysInfo);
	}

	@AfterClass
	public static void classTearDown() {
		SysInfo.mockWith(savedSysInfo);
		Database.swapWith(origDB);
	}
}
