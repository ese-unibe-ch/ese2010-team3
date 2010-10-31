package tests.mocks;

import models.User;
import controllers.ISession;

public class SessionMock implements ISession {
	private User user;

	public User currentUser() {
		return user;
	}

	public void loginAs(User _user) {
		user = _user;
	}

}
