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

/**
 * SearchFilter can be used as a Filter-Visitor that classifies a list of
 * questions of how well the question's content, its answers' contents, its tags
 * and the name of their owners fit given search terms. Questions not matching
 * *all* of the search terms are given a value of 0 so that they can be filtered
 * out.
 **/
public class SearchFilter implements IFilter<Question, Double> {

	/** A set of terms to search for. */
	private final Set<String> queryFulltext;

	/** A set of tags to search for. */
	private final Set<Tag> queryTags;

	/**
	 * Instantiates a new search filter.
	 * 
	 * @param query
	 *            all the search terms as a single string
	 * @param tags
	 *            the tags a question must have, unless their names also appear
	 *            in the questions, etc. content
	 */
	public SearchFilter(String query, Set<Tag> tags) {
		this.queryFulltext = getWords(query);
		this.queryTags = tags;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see models.helpers.IFilter#visit(java.lang.Object)
	 */
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

	/**
	 * Rates how well the tags of a question match the searched for tags
	 * yielding values between 0 (no tags match) to 1 (the tags are identical).
	 * 
	 * @param question
	 *            the question to rate
	 * @param mustHave
	 *            the list of search terms that have to occur in some part of a
	 *            question (else the overall rating is 0)
	 * @return the matching ratio of the question's tags with the searched for
	 *         tags
	 */
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

	/**
	 * Rates how well the content of an entry (and its owner's name) matches the
	 * searched for terms. This is a ratio between 0 (no term appears) and 1
	 * (the content consists exclusively of the searched for terms).
	 * 
	 * @param entry
	 *            the entry whose content is to be rated
	 * @param mustHave
	 *            the list of search terms that have to occur in some part of a
	 *            question (else the overall rating is 0)
	 * @return the matching ratio of the entry's content with the searched for
	 *         terms
	 */
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

	/**
	 * Rate how well a question's answers matches the searched for terms.
	 * 
	 * @param question
	 *            the question whose answers are to be classified
	 * @param mustHave
	 *            the list of search terms that have to occur in some part of a
	 *            question (else the overall rating is 0)
	 * @return the matching ratio of the question's answers with the searched
	 *         for terms
	 */
	private double rateAnswers(Question question, Set<String> mustHave) {
		double rating = 0;
		for (Answer ans : question.answers()) {
			rating += rateText(ans, mustHave);
		}
		int answerCount = question.countAnswers();
		return rating / (answerCount == 0 ? 1 : answerCount);
	}

	/**
	 * Splits the content of a question, answer or the search terms up into a
	 * set of words not containing HTML tags nor words occurring very often in
	 * the English language (StopWords).
	 * 
	 * @param string
	 *            an (HTML-)string to split up and clean
	 * @return a set of words for easier rating through intersections
	 */
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
