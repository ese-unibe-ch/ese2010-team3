package models.database.HotDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import models.Answer;
import models.Question;
import models.Tag;
import models.User;
import models.SearchEngine.SearchFilter;
import models.database.Database;
import models.database.IQuestionDatabase;
import models.helpers.Mapper;

public class HotQuestionDatabase implements IQuestionDatabase {

	private final HashMap<Integer, Question> questions = new HashMap<Integer, Question>();

	/**
	 * Searches through all questions, answers and usernames for the given
	 * search terms.
	 * 
	 * @param term
	 *            the list of strings that must appear somewhere in a question
	 *            or its answers. Only letters and numbers are retained. In
	 *            order to search for questions having a specific tag, use the
	 *            "tag:<em>tagname</em>" syntax.
	 * @return a list of all the questions that match <em>all</em> the search
	 *         criteria.
	 * 
	 */
	public List<Question> searchFor(String term) {
		Set<String> terms = new HashSet();
		Set<Tag> tags = new HashSet<Tag>();
		for (String s : term.toLowerCase().split("\\s+")) {
			if (s.startsWith("tag:") && s.length() > 4) {
				// search for tag only
				terms.add(s);
				tags.add(Database.tags().get(s.substring(4)));
			} else {
				// search for this term anywhere, so ignore all non-alphanumeric
				// characters
				terms.addAll(Arrays.asList(s.split("\\W+")));
				tags.add(Database.tags().get(s));
			}
		}
		return Mapper.sort(this.questions.values(),
				new SearchFilter(terms, tags));
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
		this.questions.put(q.id(), q);
		return q.id();
	}

	public int count() {
		return this.questions.size();
	}

	public int countBestRatedAnswers() {
		int count = 0;
		for (Question q : this.questions.values())
			if (q.hasBestAnswer()) {
				count++;
			}
		return count;
	}

	public int countAllAnswers() {
		int count = 0;
		for (Question q : this.questions.values()) {
			count += q.countAnswers();
		}
		return count;
	}

	public int countHighRatedAnswers() {
		int count = 0;
		for (Question q : this.questions.values()) {
			for (Answer a : q.answers()) {
				if (a.isHighRated()) {
					count += 1;
				}
			}
		}
		return count;
	}

	public List<Question> findSimilar(Question q) {
		List<Question> result = Mapper.sort(this.questions.values(),
				new SearchFilter(null, new HashSet<Tag>(q.getTags())));
		result.remove(q); // don't find the question itself!
		return result;
	}

	/**
	 * Having given a best answer gives the equivalent of an additional
	 * BEST_ANSWER_BONUS votes.
	 */
	private final int BEST_ANSWER_BONUS = 5;

	/**
	 * Collects for all tags the vote counts for all the users that have
	 * answered a question labeled with that tag.
	 * 
	 * @return a statistics map allowing to either determine the experts for a
	 *         given tag or the tags this user is an expert for
	 */
	public Map<Tag, Map<User, Integer>> collectExpertiseStatistics() {
		Map<Tag, Map<User, Integer>> stats = new HashMap();
		// only check each question (and answer) once
		for (Question question : Database.questions().all()) {
			List<Tag> tags = question.getTags();
			// skip untagged questions
			if (tags.isEmpty())
				continue;
			for (Answer answer : question.answers()) {
				User user = answer.owner();
				// don't consider answers by the question's author and by
				// anonymous users
				if (user == question.owner() || user == null)
					continue;
				for (Tag tag : tags) {
					// get the statistics for a given tag (initialize them at
					// the first pass)
					Map<User, Integer> tagStats = stats.get(tag);
					if (tagStats == null) {
						tagStats = new HashMap();
						stats.put(tag, tagStats);
					}
					// update the vote count for this answer's owner
					Integer count = tagStats.get(user);
					if (count == null)
						count = 0;
					// a best answer count as 5 additional up-votes
					if (answer.isBestAnswer())
						count += BEST_ANSWER_BONUS;
					tagStats.put(user, count + answer.rating());
				}
			}
		}

		return stats;
	}

	public void clear() {
		this.questions.clear();
	}

	public List<Question> getWatchList(User user) {
		List<Question> watchList = new ArrayList();
		for (Question question : this.questions.values()) {
			if (question.hasObserver(user)) {
				watchList.add(question);
			}
		}
		return watchList;
	}
}
