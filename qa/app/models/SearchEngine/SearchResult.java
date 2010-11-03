package models.SearchEngine;

import static models.helpers.SetOperations.difference;
import static models.helpers.SetOperations.intersection;

import java.util.HashSet;
import java.util.Set;

import models.Question;
import models.Tag;
import models.helpers.Mapper;
import models.helpers.Pair;

public class SearchResult extends Mapper<Pair<Integer, Question>, Question> {
	private final Set<String> queryFulltext;
	private final Set<Tag> queryTags;

	public SearchResult(String query, Set<Tag> tags) {
		queryFulltext = filterWords(getWords(query));
		queryTags = tags;
	}

	@Override
	protected Pair<Integer, Question> visit(Question question) {
		Integer tagRating = rateTags(question), textRating = rateText(question);
		Integer rating = combine(tagRating, textRating);
		if (rating != 0)
			return new Pair<Integer, Question>(rating, question);
		else
			return null;
	}

	private Set<String> getWords(String string) {
		Set<String> words = new HashSet<String>();
		for (String word : string.replaceAll("\\W", " ").split("\\s+")) {
			words.add(word);
		}
		return words;
	}

	private Integer combine(Integer tagRating, Integer textRating) {
		return tagRating + textRating;
	}

	private Integer rateTags(Question question) {
		Set<Tag> tags = new HashSet<Tag>(question.getTags());
		return 100 * intersection(tags, queryTags).size()
				/ notZero(queryTags.size());
	}

	private Integer notZero(Integer x) {
		return x < 1 ? 1 : x;
	}

	private Integer rateText(Question question) {
		Set<String> words = filterWords(getWords(question.content()));
		return 100 * intersection(words, queryFulltext).size()
				/ notZero(queryFulltext.size());
	}

	private Set<String> filterWords(Set<String> words) {
		return difference(words, StopWords.get());
	}

}
