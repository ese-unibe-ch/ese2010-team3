import models.User;
import models.database.Database;
import play.jobs.Every;
import play.jobs.Job;


@Every("2h")
public class CleanUpJobs extends Job{
	
	public void UnconfirmedUser(){
		for(User user:Database.get().users().all())
			if(!user.isConfirmed())
				user.delete();
	}

}
