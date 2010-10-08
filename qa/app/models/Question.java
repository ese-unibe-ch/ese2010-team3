package models;
import java.util.*;

/**
 * A {@link Entry} containing a question as <code>content</code>, {@link Answer}s and {@link Comments}.  
 * 
 * @author Simon Marti
 * @author Mirco Kocher
 *
 */
public class Question extends Entry {

	private IDTable<Answer> answers;
	private IDTable<Comment> comments;
	private int id;
	
	private static IDTable<Question> questions = new IDTable();
	
	/**
	 * Create a Question.
	 * @param owner the {@link User} who posted the <code>Question</code>
	 * @param content the question
	 */
	public Question(User owner, String content) {
		super(owner, content);
		this.answers = new IDTable<Answer>();
		this.comments = new IDTable<Comment>();
		this.id = questions.add(this);
	}

	public String type() {
		return "Question";
	}
	
	/**
	 * Unregisters all {@link Answer}s, {@link Comment}s, {@link Vote}s and itself.
	 */
	@Override
	public void unregister() {
		Iterator<Answer> itAnswer = this.answers.iterator();
		Iterator<Comment> itComment = this.comments.iterator();
		this.answers = new IDTable<Answer>();
		this.comments = new IDTable<Comment>();
		while(itAnswer.hasNext()) {
			itAnswer.next().unregister();
		}
		while(itComment.hasNext()) {
			itComment.next().unregister();
		}
		questions.remove(this.id);
		this.unregisterVotes();
		this.unregisterUser();
	}
	
	/**
	 * Unregisters a deleted {@link Answer}.
	 * @param answer the {@link Answer} to unregister
	 */
	public void unregister(Answer answer) {
		this.answers.remove(answer.id());
	}
	
	
	@Override
	public void unregister(Comment comment) {
		this.comments.remove(comment.id());
	}

	/**
	 * Post a {@link Answer} to a <code>Question</code>
	 * @param user the {@link User} posting the {@link Answer}
	 * @param content the answer
	 * @return an {@link Answer}
	 */
	public Answer answer(User user, String content) {
		Answer answer = new Answer(this.answers.nextID(), user, this, content);
		this.answers.add(answer);
		return answer;
	}
	
	/**
	 * Post a {@link Comment} to a <code>Question</code>
	 * @param user the {@link User} posting the {@link Comment}
	 * @param content the comment
	 * @return an {@link Comment}
	 */
	public Comment comment(User user, String content) {
		Comment comment = new Comment(this.comments.nextID(), user, this, content);
		this.comments.add(comment);
		return comment;
	}
	
	/**
	 * Checks if a {@link Answer} belongs to a <code>Question</code>
	 * @param answer the {@link Answer} to check
	 * @return true if the {@link Answer} belongs to the <code>Question</code>
	 */
	public boolean hasAnswer(Answer answer) {
		return this.answers.contains(answer);
	}
	
	/**
	 * Checks if a {@link Comment} belongs to a <code>Question</code>
	 * @param comment the {@link Comment} to check
	 * @return true if the {@link Comment} belongs to the <code>Question</code>
	 */
	public boolean hasComment(Comment comment) {
		return this.comments.contains(comment);
	}
	
	/**
	 * Get the <code>id</code> of the <code>Question</code>.
	 * The <code>id</code> does never change.
	 * @return id of the <code>Question</code>
	 */
	public int id() {
		return this.id;
	}

	/**
	 * Get a <@link Collection} of all <code>Questions</code>.
	 * @return all <code>Questions</code>
	 */
	public static List<Question> questions() {
		List<Question> list = new ArrayList();
		list.addAll(questions.list());
		Collections.sort(list, new EntryComperator());
		return list;
	}
	
	/**
	 * Get the <code>Question</code> with the given id.
	 * @param id
	 * @return a <code>Question</code> or null if the given id doesn't exist.
	 */
	public static Question get(int id) {
		return questions.get(id);
	}

	/**
	 * Get all {@link Answer}s to a <code>Question</code>
	 * @return {@link Collection} of {@link Answers}
	 */
	public List<Answer> answers() {
		List<Answer> list = new ArrayList();
		list.addAll(answers.list());
		Collections.sort(list, new EntryComperator());
		return list;
	}
	
	/**
	 * Get all {@link Comment}s to a <code>Question</code>
	 * @return {@link Collection} of {@link Comments}
	 */
	public List<Comment> comments() {
		List<Comment> list = new ArrayList<Comment>();
		list.addAll(comments.list());
		Collections.sort(list, new EntryComperator());
		return list;
	}

	/**
	 * Get a specific {@link Answer} to a <code>Question</code>
	 * @param id of the <code>Answer</code>
	 * @return {@link Answer} or null
	 */
	public Answer getAnswer(int id) {
		return this.answers.get(id);
	}
	
	/**
	 * Get a specific {@link Comment} to a <code>Question</code>
	 * @param id of the <code>Comment</code>
	 * @return {@link Comment} or null
	 */
	public Comment getComment(int id) {
		return this.comments.get(id);
	}

}
