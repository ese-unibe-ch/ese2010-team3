import java.util.Calendar;
import java.util.GregorianCalendar;

import models.Question;
import models.TimeTracker;
import models.User;
import play.jobs.Job;
import play.jobs.OnApplicationStart;

@OnApplicationStart
public class Bootstrap extends Job {

	@Override
	public void doJob() {
		// User
		User jack = User.register("Jack", "jack");
		User john = User.register("John", "john");
		User bill = User.register("Bill", "bill");
		User kate = User.register("Kate", "kate");

		// Questions
		Question question = Question.register(jack,
				"Why did the chicken cross the road?");
		question.answer(bill, "To get to the other side.");

		question = Question.register(john,
				"What is the answer to life the universe and everything?");
		question.answer(kate, "42");
		question.answer(kate, "1337");

		// Comments
		question.comment(jack, "What a strange question");

		// TimeTracker
		GregorianCalendar g = new GregorianCalendar(2010, Calendar.OCTOBER, 25);
		TimeTracker.setRealTimeTracker(g);
		
		
	}
}