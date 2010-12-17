package controllers;

import models.User;
import models.database.Database;

public class Security extends Secure.Security {

	static boolean authenticate(String username, String password) {
		User user = Database.users().get(username);
		return user != null && user.checkPW(password) && user.isConfirmed();
	}

	static void onDisconnected() {
		Application.index(0);
	}
}
