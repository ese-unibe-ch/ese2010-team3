package notifiers;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import models.User;
import play.exceptions.MailException;
import play.mvc.Mailer;
 
public class Mails extends Mailer {

	public static boolean welcome(User user) throws MailException {
		setSubject("Welcome %s", user.getName());
		addRecipient(user.getEmail());
		setFrom("ajopi <noreply@arcadeweb.ch>");
		String key = user.getConfirmKey();
		Future<Boolean> isSent = send(user, key);

		// busy wait until the e-mail has been sent
		while (!isSent.isDone())
			;
		try {
			// check if sending has been successful
			return isSent.get();
		} catch (InterruptedException e) {
			return false;
		} catch (ExecutionException e) {
			return false;
		}
	}
}
