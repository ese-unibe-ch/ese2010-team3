import java.io.File;
import java.util.GregorianCalendar;

import models.Question;
import models.SystemInformation;
import models.TimeTracker;
import models.User;
import models.database.Database;
import models.database.importers.Importer;
import play.jobs.Job;
import play.jobs.OnApplicationStart;

@OnApplicationStart
public class Bootstrap extends Job {

	@Override
	public void doJob() {

		/**
		 * Be Careful!
		 * 
		 * The cheating detection, preventions and consequences are NOT
		 * activated if this option is set true
		 */
		SystemInformation.get().setTestMode(true);

		// User
		User jack = Database.get().users().register("Jack", "jack",
				"jack@example.com");
		User john = Database.get().users().register("John", "john",
				"john@example.com");
		User bill = Database.get().users().register("Bill", "bill",
				"bill@example.com");
		User kate = Database.get().users().register("Kate", "kate",
				"kate@example.com");
		User xss = Database.get().users().register(
				"<script>alert('XSS')</script>", "xss", "");

		for (User user : Database.get().users().all()) {
			user.confirm();
		}

		jack.setFullname("Jack Daniel");
		jack.setWebsite("http://www.example.org/#jackd");
		jack.setProfession("Brewer");
		jack.setBiography("Oh well, ...");
		jack.setModerator(true);

		// Questions

		Question question = Database.get().questions().add(jack,
				"Why did the chicken cross the road?");
		question.answer(bill, "To get to the other side.");

		question = Database.get().questions().add(john,
				"What is the answer to life the universe and everything?");

		question.answer(kate, "42");
		question.answer(kate, "1337");

		// Comments
		question.comment(jack, "What a strange question");
		question.comment(xss, "Don't ask &mdash; it's dangerous!");

		// Tags
		question.setTagString("numb3rs");

		// TimeTracker
		GregorianCalendar mock = new GregorianCalendar();
		mock.set(2010, 10, 1, 0, 0);
		TimeTracker.getTimeTracker().injectMockedStartTime(mock.getTime());

		// try to import some more questions, answers, etc.
		try {
			Importer.importXML(new File("qa/conf/fixtures/QA.xml"));
		} catch (Exception e) {
			// handle all exceptions the same way (all failures aren't fatal)
			e.printStackTrace();
		}
	}
}
