package controllers;

import models.User;

public interface ISession {

	public User currentUser();

}