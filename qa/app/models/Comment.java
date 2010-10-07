package models;

/**
 * 
 * @author Felix Langenegger
 * @author Tobias Brog (Review)
 *
 */

public class Comment extends Entry {
	
	private int id;
	private Question question;
	

	public Comment(int id, User owner, Question question, String content) {
		super(owner, content);
		this.question = question;
		this.id = id;
	}

	@Override
	public String type() {
		return "Comment";
	}
	
	public Question question() {
		return this.question;
	}
	
	public int id() {
		return this.id;
	}

}
