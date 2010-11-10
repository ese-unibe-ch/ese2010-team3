package models.SearchEngine;

import static models.helpers.SetOperations.difference;
import static models.helpers.SetOperations.intersection;

import java.util.HashSet;
import java.util.Set;

import models.Question;
import models.Tag;
import models.helpers.Filter;

public class SearchFilter implements Filter<Question, Double> {
	private final Set<String> queryFulltext;
	private final Set<Tag> queryTags;

	public SearchFilter(String query, Set<Tag> tags) {
		this.queryFulltext = getWords(query);
		this.queryTags = tags;
	}

	public Double visit(Question question) {
		double rating = rateTags(question) + rateText(question);
		// ignore questions that don't match at all, sort highest rating first
		return rating > 0 ? -rating : null;
	}

	private double rateTags(Question question) {
		Set<Tag> tags = new HashSet<Tag>(question.getTags());
		if (this.queryTags == null || this.queryTags.isEmpty()
				|| tags.isEmpty())
			return 0;

		// rate highest questions that share most of the tags and don't have
		// hardly any additional tags
		return Math.pow(intersection(tags, this.queryTags).size(), 2)
				/ this.queryTags.size() / tags.size();
	}

	private double rateText(Question question) {
		Set<String> words = getWords(question.content());
		if (this.queryFulltext == null)
			return 0;

		// words that aren't tags must appear in a question's content (AND
		// search)
		Set<String> mustHave = new HashSet<String>(this.queryFulltext);
		for (Tag tag : question.getTags()) {
			mustHave.remove(tag.getName());
		}
		if (difference(mustHave, words).size() != 0)
			return -1; // cancel out any value returned by rateTags [0;1]

		if (this.queryFulltext.isEmpty() || words.isEmpty())
			return 0;
		return 1.0 * intersection(words, this.queryFulltext).size()
				/ words.size();
	}

	private Set<String> getWords(String string) {
		if (string == null)
			return null;

		Set<String> words = new HashSet<String>();
		for (String word : string.split("\\W+")) {
			words.add(word);
		}
		words.remove(""); // remove splitting artifact
		return difference(words, StopWords.get());
	}
}
