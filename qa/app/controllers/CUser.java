package controllers;

import java.io.File;
import java.text.ParseException;

import models.Answer;
import models.Notification;
import models.User;
import models.database.Database;
import models.database.importers.Importer;
import play.data.validation.Required;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.With;

@With(Secure.class)
public class CUser extends Controller {
	public static void deleteUser(String name) throws Throwable {
		User user = Database.get().users().get(name);
		if (hasPermissionToDelete(Session.get().currentUser(), user)) {
			user.delete();
			flash.success("User %s has been deleted.", name);
			Secure.logout();
			Application.index(0);
		}
		flash.error("You're not allowed to delete user %s!", name);
		if (!CUser.redirectToCallingPage()) {
			Application.index(0);
		}
	}

	public static void anonymizeUser(String name) throws Throwable {
		User user = Database.get().users().get(name);
		if (hasPermissionToDelete(Session.get().currentUser(), user)) {
			user.anonymize(true, false);
		}
		deleteUser(name);
	}

	private static boolean hasPermissionToDelete(User currentUser, User user) {
		return currentUser == user;
	}

	public static void saveProfile(String name, String email, String fullname,
			String birthday, String website, String profession,
			String employer, String biography, String oldPassword,
			String newPassword) throws ParseException {
		User user = Database.get().users().get(name);
		if (!Application.userCanEditProfile(user)) {
			flash.error("You're not allowed to edit %s's profile!", name);
			Application.showprofile(user.getName());
		}
		if (email != null) {
			user.setEmail(email);
		}
		if (fullname != null) {
			user.setFullname(fullname);
		}
		if (birthday != null) {
			user.setDateOfBirth(birthday);
		}
		if (website != null) {
			user.setWebsite(website);
		}
		if (profession != null) {
			user.setProfession(profession);
		}
		if (employer != null) {
			user.setEmployer(employer);
		}
		if (biography != null) {
			user.setBiography(biography);
		}
		if (!newPassword.isEmpty()) {
			if (user.checkPW(newPassword)) {
				user.setSHA1Password(newPassword);
			} else if (!user.checkPW(oldPassword)) {
				flash
						.error("To change your password enter your current one first, then enter the new password!");
			}
		}
		if (!oldPassword.isEmpty() && newPassword.isEmpty()) {
			flash.error("New password cannot be empty!");
		}

		flash.success("Thanks for keeping your profile up-to-date.");
		Application.showprofile(user.getName());
	}

	public static void followNotification(int id) {
		User user = Session.get().currentUser();
		Notification notification = user.getNotification(id);
		if (notification != null) {
			notification.unsetNew();
		}
		if (notification != null && notification.getAbout() instanceof Answer) {
			Application.question(((Answer) notification.getAbout())
					.getQuestion().id());
		} else if (!CUser.redirectToCallingPage()) {
			Application.notifications(0);
		}
	}

	public static void clearNewNotifications() {
		User user = Session.get().currentUser();
		for (Notification n : user.getNewNotifications()) {
			n.unsetNew();
		}
		flash.success("All notifications have been marked as read.");
		Application.notifications(0);
	}

	public static void deleteNotification(int id) {
		User user = Session.get().currentUser();
		Notification n = user.getNotification(id);
		if (n != null) {
			n.unregister();
			flash.success("You've got one notification less to care about.");
		}
		Application.notifications(0);
	}

	public static void blockUser(String username, String reason) {
		User user = Database.get().users().get(username);
		User mod = Session.get().currentUser();
		if (mod.isModerator() && mod != user) {
			if (reason.equals("")) {
				reason = "no reason given";
			}
			user.block(reason);
			flash.success("User %s has been blocked (%s).", username, reason);
		}
		Application.showprofile(user.getName());
	}

	public static void unblockUser(String username) {
		User user = Database.get().users().get(username);
		User mod = Session.get().currentUser();
		if (mod.isModerator() && mod != user) {
			user.unblock();
			flash.success("User %s has been unblocked.", username);
		}
		Application.showprofile(user.getName());
	}

	public static void loadXML(@Required File xml) {
		if (!Session.get().currentUser().isModerator()) {
			Application.index(0);
		}
		if (xml == null) {
			flash
					.error("Please select an XML database file before clicking <b>Import Database</b>");
			Application.admin();
		}

		try {
			Importer.importXML(xml);
			flash.success("XML file successfully loaded to the database.");
		} catch (Throwable e) {
			flash.error("Couldn't load xml file!", e.getMessage());
			e.printStackTrace();
			Application.admin();
		}
		if (xml != null) {
			xml.delete();
		}
		Application.index(0);
	}

	public static void clearDB() {
		if (!Session.get().currentUser().isModerator()) {
			flash.error("You're a naughty boy");
			Application.index(0);
		}
		Database.clearKeepAdmins();
		flash.success("Tabula rasa!");
		Application.admin();
	}

	static boolean redirectToCallingPage() {
		Http.Header referer = request.headers.get("referer");
		if (referer == null)
			return false;
		redirect(referer.value());
		return true;
	}

	/**
	 * Leads to the edit-view of the {@link User}'s profile
	 * 
	 * @param userName
	 *            the name of the {@link User} who owns the profile
	 */
	public static void editProfile(String userName) {
		User showUser = Database.get().users().get(userName);
		if (!Application.userCanEditProfile(showUser)) {
			Application.showprofile(userName);
		}
		render(showUser);
	}
}
