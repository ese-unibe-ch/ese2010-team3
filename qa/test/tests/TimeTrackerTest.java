package tests;

import models.TimeTracker;

import org.junit.Before;
import org.junit.Test;

public class TimeTrackerTest extends MockedUnitTest {

	private TimeTracker t;

	@Before
	public void setUp() throws Exception {
		sysInfo.year(2010).month(9).day(1).hour(0).minute(0).second(0);
		t = TimeTracker.getTimeTracker();
		t.injectMockedStartTime(sysInfo.now());
		sysInfo.year(2010).month(12).day(1).hour(0).minute(0).second(0);

	}

	@Test
	public void shouldCalculateNinetyTwoDays() {
		sysInfo.year(2010).month(12).day(1).hour(0).minute(0).second(0);
		int days = t.getDays();
		assertEquals(92, days);
	}

	@Test
	public void shouldCalculateOneDay() {
		sysInfo.year(2010).month(9).day(2).hour(0).minute(0).second(0);
		int days = t.getDays();
		assertEquals(1, days);
	}

	@Test
	public void shouldCalculateZeroDays() {
		sysInfo.year(2010).month(9).day(1).hour(0).minute(0).second(0);
		int days = t.getDays();
		assertEquals(0, days);
	}

	@Test
	public void shouldCalculateOneWeek() {
		sysInfo.year(2010).month(9).day(2).hour(0).minute(0).second(0);
		int weeks = t.getWeeks();
		assertEquals(1, weeks);
	}

	@Test
	public void shouldCalculateZeroWeeks() {
		sysInfo.year(2010).month(9).day(1).hour(0).minute(0).second(0);
		int weeks = t.getWeeks();
		assertEquals(0, weeks);
	}

	@Test
	public void shouldCalculateFourteenWeeks() {
		int weeks = t.getWeeks();
		assertEquals(14, weeks);
	}

	@Test
	public void shouldCalculateThreeMonths() {
		int months = t.getMonths();
		assertEquals(3, months);
	}

	@Test
	public void shouldCalculateOneMonth() {
		sysInfo.year(2010).month(10).day(1).hour(0).minute(0).second(0);
		int months = t.getMonths();
		assertEquals(1, months);
	}

	@Test
	public void shouldCalculateZeroMonths() {
		sysInfo.year(2010).month(9).day(1).hour(0).minute(0).second(0);
		int months = t.getMonths();
		assertEquals(0, months);
	}
}
