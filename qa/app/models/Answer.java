package models;
import java.util.*;
/**
 * A {@link Entry} containing an answer to a {@link Question}
 * 
 * @author Simon Marti
 * @author Mirco Kocher
 *
 */
public class Answer extends Entry {

	private Question question;
	private IDTable<Comment> comments;
	private int id;
	
	/**
	 * Create an <code>Answer</code> to a {@link Question}.
	 * @param ic 
	 * @param owner the {@link User} who posted the <code>Answer</code>
	 * @param question the {@link Question} this <code>Answer</code> belongs to
	 * @param content the answer
	 */
	public Answer(int id, User owner, Question question, String content) {
		super(owner, content);
		this.question = question;
		this.comments = new IDTable<Comment>();
		this.id = id;
	}
	
	public String type() {
		return "Answer";
	}
	
	/**
	 * Unregisters all {@link Vote}s, {@link Comments} and itself.
	 */
	@Override
	public void unregister() {
		Iterator<Comment> itComment = this.comments.iterator();
		this.comments = new IDTable<Comment>();
		while(itComment.hasNext()) {
			itComment.next().unregister();
		}
		this.question.unregister(this);
		this.unregisterVotes();
		this.unregisterUser();
	}
	
	/**
	 * Get the {@link Question} belonging to the <code>Answer</code>.
	 * @return the {@link Question} this <code>Answer</code> belongs to
	 */
	public Question question() {
		return this.question;
	}
	
	/**
	 * Unregisters a deleted {@link Comment}.
	 * @param comment the {@link Comment} to unregister
	 */
	public void unregister(Comment comment) {
		this.comments.remove(comment.id());
	}
	
	/**
	 * Checks if a {@link Comment} belongs to a <code>Answer</code>
	 * @param comment the {@link Comment} to check
	 * @return true if the {@link Comment} belongs to the <code>Answer</code>
	 */
	public boolean hasComment(Comment comment) {
		return this.comments.contains(comment);
	}
	
	/**
	 * Get all {@link Comment}s to a <code>Answer</code>
	 * @return {@link Collection} of {@link Comments}
	 */
	public List<Comment> comments() {
		List<Comment> list = new ArrayList<Comment>();
		list.addAll(comments.list());
		Collections.sort(list, new EntryComperator());
		return list;
	}
	
	/**
	 * Get a specific {@link Comment} to a <code>Answer</code>
	 * @param id of the <code>Comment</code>
	 * @return {@link Comment} or null
	 */
	public Entry getComment(int id) {
		return this.comments.get(id);
	}

	public int id() {
		return this.id;
	}

}
