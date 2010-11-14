package controllers;

import models.User;
import models.database.Database;

public class Security extends Secure.Security {

	static boolean authenticate(String username, String password) {
		boolean userexists = false;
		boolean correctpassword = false;
		for (User user : Database.get().users().all()) {
			if (user.getName().toLowerCase().equals(username.toLowerCase())) {
				userexists = true;
				correctpassword = Database.get().users().get(user.getName())
						.checkPW(password);
				break;
			}
		}
		return userexists && correctpassword;
	}

	static void onDisconnected() {
		Application.index();
	}
}
