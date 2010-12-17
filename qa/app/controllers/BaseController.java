package controllers;

import models.User;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.Http;

/**
 * Some basic methods that should be available to all controllers.
 */
public abstract class BaseController extends Controller {

	/**
	 * Makes the connected user available to all views as 'user'.
	 */
	@Before
	static void setConnectedUser() {
		if (Security.isConnected()) {
			User user = Database.users().get(Security.connected());
			renderArgs.put("user", user);
		}
	}

	/**
	 * Redirects to the calling page.
	 * 
	 * @return true, if a redirect header was found; false, if the caller should
	 *         call an explicit Controller action instead.
	 */
	protected static boolean redirectToCallingPage() {
		Http.Header referer = request.headers.get("referer");
		if (referer == null)
			return false;
		redirect(referer.value());
		return true;
	}

}
