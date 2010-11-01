package models.database.HotDatabase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import models.IDTable;
import models.Question;
import models.User;
import models.SearchEngine.SearchResult;
import models.database.IQuestionDatabase;
import models.helpers.Pair;
import models.helpers.Visitor;

public class HotQuestionDatabase implements IQuestionDatabase {

	public  IDTable<Question> questions = new IDTable();

	private static final Comparator byRating = new Comparator() {
		public int compare(Object arg0, Object arg1) {
			Pair<Integer,Question> 	x = (Pair<Integer, Question>) arg0, 
									y = (Pair<Integer, Question>) arg1;
			return y.left.compareTo(x.left);
		}
	};
	private static final Visitor onlyQuestion = new Visitor<Question,Pair<Integer,Question>>() {

		@Override
		protected Question visit(Pair<Integer, Question> i) {
			return i.right;
		}
	};

	public  List<Question> searchFor(String term) {
		SearchResult search = new SearchResult(term);
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

}
