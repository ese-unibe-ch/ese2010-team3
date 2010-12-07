package notifiers;
 
import play.*;
import play.exceptions.MailException;
import play.mvc.*;
import java.util.*;
import java.util.concurrent.Future;

import models.*;
import models.helpers.Tools;
 
public class Mails extends Mailer {
 
   public static void welcome(User user) throws MailException {
	   setSubject("Welcome %s", user.getName());
	   addRecipient(user.getEmail());
	   setFrom("ajopi <noreply@arcadeweb.ch>");
	   String key = user.getConfirmKey();
	   Future<Boolean> isSent = send(user, key);
	   if(isSent.isDone()){
		   throw new MailException("Error");
	   }
   }
 
}