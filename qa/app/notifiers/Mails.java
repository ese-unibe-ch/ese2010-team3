package notifiers;
 
import play.*;
import play.mvc.*;
import java.util.*;
import models.*;
import models.helpers.Tools;
 
public class Mails extends Mailer {
 
   public static void welcome(User user) {
      setSubject("Welcome %s", user.getName());
      addRecipient(user.getEmail());
      setFrom("ajopi <noreply@arcadeweb.ch>");
      String key = user.getConfirmKey();
      send(user, key);
   }
 
}