package controllers;

import models.*;

public class Security extends Secure.Security {

	static boolean authenticate(String username, String password) {
		return User.get(username) != null && User.get(username).checkPW(password);
	}

	static void onDisconnected() {
		Application.index();
	}
}