package models.database.HotDatabase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import models.Answer;
import models.IDTable;
import models.Question;
import models.Tag;
import models.User;
import models.SearchEngine.SearchResult;
import models.database.IQuestionDatabase;
import models.helpers.Pair;
import models.helpers.Mapper;

import static models.helpers.SetOperations.*;

public class HotQuestionDatabase implements IQuestionDatabase {

	private  IDTable<Question> questions = new IDTable();

	private static final Comparator byRating = new Comparator() {
		public int compare(Object arg0, Object arg1) {
			Pair<Integer,Question> 	x = (Pair<Integer, Question>) arg0, 
									y = (Pair<Integer, Question>) arg1;
			return y.left.compareTo(x.left);
		}
	};
	private static final Mapper onlyQuestion = new Mapper<Question,Pair<Integer,Question>>() {
		@Override
		protected Question visit(Pair<Integer, Question> i) {
			return i.right;
		}
	};

	public  List<Question> searchFor(String term) {
		Set<Tag> tags = new HashSet<Tag>();
		for (String s : term.split("\\s+")) {
			tags.add(Tag.get(s));
		}
		SearchResult search = new SearchResult(term,tags);
		List<Pair<Integer,Question>> results = search.over(questions);
		Collections.sort(results, byRating);
		return onlyQuestion.over(results);
	}

	/**
	 * Get the <code>Question</code> with the given id.
	 * 
	 * @param id
	 * @return a <code>Question</code> or null if the given id doesn't exist.
	 */
	public Question get(int id) {
		return questions.get(id);
	}

	/**
	 * Get a <@link Collection} of all <code>Questions</code>.
	 * 
	 * @return all <code>Questions</code>
	 */
	public List<Question> all() {
		List<Question> list = new ArrayList<Question>(questions.values());
		Collections.sort(list);
		return list;
	}

	public Question add(User owner, String content) {
		return new Question(owner, content);
	}

	public void remove(int id) {
		questions.remove(id);
	}

	public int register(Question q) {
		return questions.add(q);
	}

	public int count() {
		return questions.size();
	}

	public Set<Answer> getBestRatedAnswers() {
		HashSet<Answer> answers = new HashSet<Answer>();
		for (Question q : questions) {
			if (q.hasBestAnswer()) {
				answers.add(q.getBestAnswer());
			}
		}
		return answers;
	}

	public int countBestRatedAnswers() {
		int count = 0;
		for (Question q : questions) {
			if (q.hasBestAnswer()) {
				count++;
			}
		}
		return count;
	}

	public int countAllAnswers() {
		int count = 0;
		for (Question q: questions) {
			count += q.countAnswers();
		}
		return count;
	}

	public int countHighRatedAnswers() {
		int count = 0;
		for (Question q: questions) {
			for (Answer a: q.answers()) {
				if (a.isHighRated())
					count += 1;
			}
		}
		return count;
	}
	
	private List<Question> withMatchingTag(Question q, Iterable<Question> list) {
		List<Question> result = new LinkedList<Question>();
		for (Question question : list) {
			if ( containsAny(question.getTags(),q.getTags()) &&
					q!=question) {
				result.add(question);
			}
		}
		return result;
	}

	public List<Question> findSimilar(Question q) {
		Set<Tag> tags = new HashSet(q.getTags());
		SearchResult search = new SearchResult(q.content(),tags);
		List<Pair<Integer, Question>> result = search.over(questions);
		Collections.sort(result,byRating);
		
		return withMatchingTag(q,onlyQuestion.over(result));
	}

	public void clear() {
		questions.clear();
	}
}
