package tests.mocks;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;
import static java.util.Calendar.*;

import models.ISystemInformation;

public class SystemInformationMock implements ISystemInformation {
	private Calendar date;
	
	public SystemInformationMock() {
		date = Calendar.getInstance();
	}
	
	public SystemInformationMock changeTo(Date d) {
		date.setTime(d);
		return this;
	}
	
	public SystemInformationMock year(int y) {
		date.set(YEAR,y);
		return this;
	}
	
	public SystemInformationMock month(int m) {
		date.set(MONTH,m);
		return this;
	}
	
	public SystemInformationMock day(int m) {
		date.set(Calendar.DAY_OF_MONTH,m);
		return this;
	}
	
	public SystemInformationMock hour(int m) {
		date.set(HOUR,m);
		return this;
	}
	
	public SystemInformationMock minute(int m) {
		date.set(MINUTE,m);
		return this;
	}
	
	public SystemInformationMock second(int m) {
		date.set(SECOND,m);
		return this;
	}

	public Date now() {
		return date.getTime();
	}

}
