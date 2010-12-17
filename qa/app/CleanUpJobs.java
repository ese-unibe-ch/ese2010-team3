import models.User;
import play.jobs.Every;
import play.jobs.Job;
import controllers.Database;

@Every("2h")
public class CleanUpJobs extends Job {

	@Override
	public void doJob() {
		for (User user : Database.users().all()) {
			if (!user.isConfirmed() && user.getConfirmationLimit() < 0) {
				user.delete();
			}
		}
	}

}
