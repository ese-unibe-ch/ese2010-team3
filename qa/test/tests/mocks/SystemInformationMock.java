package tests.mocks;

import java.util.Calendar;
import java.util.Date;

import models.SystemInformation;

public class SystemInformationMock extends SystemInformation {
	private final Calendar date;

	public SystemInformationMock() {
		date = Calendar.getInstance();
	}

	public SystemInformationMock changeTo(Date d) {
		date.setTime(d);
		return this;
	}

	public SystemInformationMock year(int y) {
		date.set(Calendar.YEAR, y);
		return this;
	}

	public SystemInformationMock month(int m) {
		date.set(Calendar.MONTH, m);
		return this;
	}

	public SystemInformationMock day(int m) {
		date.set(Calendar.DAY_OF_MONTH, m);
		return this;
	}

	public SystemInformationMock hour(int m) {
		date.set(Calendar.HOUR, m);
		return this;
	}

	public SystemInformationMock minute(int m) {
		date.set(Calendar.MINUTE, m);
		return this;
	}

	public SystemInformationMock second(int m) {
		date.set(Calendar.SECOND, m);
		return this;
	}

	@Override
	public Date now() {
		return date.getTime();
	}

}
