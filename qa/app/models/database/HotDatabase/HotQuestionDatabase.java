package models.database.HotDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import models.Answer;
import models.Question;
import models.Tag;
import models.User;
import models.SearchEngine.SearchFilter;
import models.database.Database;
import models.database.IQuestionDatabase;
import models.helpers.IDTable;
import models.helpers.Mapper;

public class HotQuestionDatabase implements IQuestionDatabase {

	private final IDTable<Question> questions = new IDTable();

	public List<Question> searchFor(String term) {
		Set<Tag> tags = new HashSet<Tag>();
		for (String s : term.split("\\W+")) {
			tags.add(Database.get().tags().get(s));
		}
		return Mapper.sort(this.questions, new SearchFilter(term, tags));
	}

	/**
	 * Get the <code>Question</code> with the given id.
	 * 
	 * @param id
	 * @return a <code>Question</code> or null if the given id doesn't exist.
	 */
	public Question get(int id) {
		return this.questions.get(id);
	}

	/**
	 * Get a <@link Collection} of all <code>Questions</code>.
	 * 
	 * @return all <code>Questions</code>
	 */
	public List<Question> all() {
		List<Question> list = new ArrayList<Question>(this.questions.values());
		Collections.sort(list);
		return list;
	}

	public Question add(User owner, String content) {
		return new Question(owner, content);
	}

	public void remove(int id) {
		this.questions.remove(id);
	}

	public int register(Question q) {
		return this.questions.add(q);
	}

	public int count() {
		return this.questions.size();
	}

	public int countBestRatedAnswers() {
		int count = 0;
		for (Question q : this.questions)
			if (q.hasBestAnswer()) {
				count++;
			}
		return count;
	}

	public int countAllAnswers() {
		int count = 0;
		for (Question q : this.questions) {
			count += q.countAnswers();
		}
		return count;
	}

	public int countHighRatedAnswers() {
		int count = 0;
		for (Question q : this.questions) {
			for (Answer a : q.answers()) {
				if (a.isHighRated()) {
					count += 1;
				}
			}
		}
		return count;
	}

	public List<Question> findSimilar(Question q) {
		List<Question> result = Mapper.sort(this.questions, new SearchFilter(
				null, new HashSet<Tag>(q.getTags())));
		result.remove(q); // don't find the question itself!
		return result;
	}

	public void clear() {
		this.questions.clear();
	}
}
