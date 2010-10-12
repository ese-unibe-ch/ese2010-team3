import play.*;
import play.jobs.*;
import play.test.*;
 
import models.*;
 
@OnApplicationStart
public class Bootstrap extends Job {
 
    public void doJob() {
    	// User
        User jack = new User("Jack", "jack");
        User john = new User("John", "john");
        User bill = new User("Bill", "bill");
        User kate = new User("Kate", "kate");
        
        // Questions
        Question question = new Question(jack, "Why did the chicken cross the road?");
        question.answer(bill, "To get to the other side.");
        
        question = new Question(john, "What is the answer to life the universe and everything?");
        question.answer(kate, "42");
        question.answer(kate, "1337");
    }
 
}