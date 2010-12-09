package notifiers;

import models.User;
import play.exceptions.MailException;
import play.mvc.Mailer;
 
public class Mails extends Mailer {

	public static boolean welcome(User user) throws MailException {
		setSubject("Welcome %s", user.getName());
		addRecipient(user.getEmail());
		setFrom("ajopi <noreply@arcadeweb.ch>");
		String key = user.getConfirmKey();
		return sendAndWait(user, key);
	}
}
