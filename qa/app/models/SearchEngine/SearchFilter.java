package models.SearchEngine;

import static models.helpers.SetOperations.difference;
import static models.helpers.SetOperations.intersection;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import models.Answer;
import models.Entry;
import models.Question;
import models.Tag;
import models.helpers.IFilter;
import models.helpers.Tools;

//TODO add javadoc
public class SearchFilter implements IFilter<Question, Double> {
	private final Set<String> queryFulltext;
	private final Set<Tag> queryTags;

	public SearchFilter(String query, Set<Tag> tags) {
		this.queryFulltext = getWords(query);
		this.queryTags = tags;
	}

	public Double visit(Question question) {
		Set<String> mustHave = new HashSet<String>(
				this.queryFulltext == null ? Collections.EMPTY_SET
						: this.queryFulltext);
		double tagRating = rateTags(question, mustHave);
		double textRating = rateText(question, mustHave);
		double answerRating = rateAnswers(question, mustHave);
		double rating = tagRating + textRating + answerRating;
		// words that aren't tags must appear in a question's content (AND
		// search)
		if (mustHave.size() != 0)
			return null;
		return rating > 0 ? -rating : null;
	}

	private double rateTags(Question question, Set<String> mustHave) {
		Set<Tag> tags = new HashSet<Tag>(question.getTags());
		if (this.queryTags == null || this.queryTags.isEmpty()
				|| tags.isEmpty())
			return 0;
		for (Tag tag : tags) {
			mustHave.remove(tag.getName());
		}

		// rate highest questions that share most of the tags and don't have
		// hardly any additional tags
		return Math.pow(intersection(tags, this.queryTags).size(), 2)
				/ this.queryTags.size() / tags.size();
	}

	private double rateText(Entry entry, Set<String> mustHave) {
		String content = entry.content();
		if (entry.owner() != null)
			content += " " + entry.owner().getName();
		Set<String> words = getWords(content);
		if (this.queryFulltext == null)
			return 0;
		if (this.queryFulltext.isEmpty() || words.isEmpty())
			return 0;
		mustHave.removeAll(words);
		return 1.0 * intersection(words, this.queryFulltext).size()
				/ words.size();
	}

	private double rateAnswers(Question question, Set<String> mustHave) {
		double rating = 0;
		for (Answer ans : question.answers()) {
			rating += rateText(ans, mustHave);
		}
		int answerCount = question.countAnswers();
		return rating / (answerCount == 0 ? 1 : answerCount);
	}

	private Set<String> getWords(String string) {
		if (string == null)
			return null;
		// this isn't ideally placed...
		string = Tools.htmlToText(string);

		Set<String> words = new HashSet<String>();
		for (String word : string.split("\\W+")) {
			words.add(word.toLowerCase());
		}
		words.remove(""); // remove splitting artifact
		return difference(words, StopWords.get());
	}
}
