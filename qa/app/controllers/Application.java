package controllers;

import java.util.List;

import models.Answer;
import models.Comment;
import models.Question;
import models.User;
import play.data.validation.Required;
import play.mvc.Before;
import play.mvc.Controller;

public class Application extends Controller {

	@Before
	static void setConnectedUser() {
		if (Security.isConnected()) {
			User user = User.get(Security.connected());
			renderArgs.put("user", user);
		}
	}

    public static void index() {
    	List<Question> questions = Question.questions();
        render(questions);
    }
    
    public static void question(int id) {
    	Question question = Question.get(id);
    	if(question == null) {
    		render();
    	} else {
    		List<Answer> answers = question.answers();
    		render(question, answers);
    	}
    }
    
    public static void answerQuestion(int id) {
    	Question question = Question.get(id);
    	List<Question> questions = Question.questions();
    	List<Answer> answers = question.answers();
    	int count = question.answers().size();
    	render(questions, question, answers, count);
    }
    
    public static void commentQuestion(int id) {
    	Question question = Question.get(id);
    	List<Question> questions = Question.questions();
    	List<Comment> comments = question.comments();
    	int count = question.comments().size();
    	render(questions, question, comments, count);
    }
    
    public static void commentAnswer(int questionId, int answerId) {
    	Question question = Question.get(questionId);
    	Answer answer = question.getAnswer(answerId);
    	List<Comment> comments = answer.comments();
    	render(answer, comments, question);
    }

    public static void deleteuser(User user){
    	render();
    }

	public static void register() {
		render();
	}

	public static void signup(@Required String username, String password,
			String email) {
		User user = User.register(username, password);
		if (email != null && email.matches("\\S+@\\w+\\.\\w+"))
			user.setEmail(email);
		// Mark user as connected
		session.put("username", username);
		index();
	}

	public static void showprofile(User user) {
		render();
	}

	public static void editprofile(User user) {
		render();
	}

	public static void saveProfile(String name, String email,
			String fullname, String age, String website, String profession,
			String employer, String biography) {
		User user = User.get(name);
		user.setEmail(email);
		user.setFullname(fullname);
		user.setAge(age);
		user.setWebsite(website);
		user.setProfession(profession);
		user.setEmployer(employer);
		user.setBiography(biography);
		showprofile(user);
	}
}
