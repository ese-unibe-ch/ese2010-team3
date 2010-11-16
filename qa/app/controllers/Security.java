package controllers;

import models.database.Database;

public class Security extends Secure.Security {

	static boolean authenticate(String username, String password) {
		return Database.get().users().get(username) != null
				&& Database.get().users().get(username).checkPW(password);
	}

	static void onDisconnected() {
		Application.index(0);
	}
}
