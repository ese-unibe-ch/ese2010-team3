package models;

import java.util.Date;

public class SystemInformation implements ISystemInformation {
	private static ISystemInformation instance = new SystemInformation();

	public static void mockWith(ISystemInformation mock) {
		instance = mock;
	}

	public static ISystemInformation get() {
		return instance;
	}

	public Date now() {
		return new Date();
	}

}
