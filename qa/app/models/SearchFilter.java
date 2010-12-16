package models;

import static models.helpers.SetOperations.difference;
import static models.helpers.SetOperations.intersection;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import models.helpers.IFilter;
import models.helpers.StopWords;

/**
 * SearchFilter can be used as a Filter-Visitor that classifies a list of
 * questions of how well the question's content, its answers' contents, its tags
 * and the name of their owners fit given search terms. Questions not matching
 * <em>all</em> of the search terms are given a value of <code>null</code> so
 * that they can be filtered out.
 **/
public class SearchFilter implements IFilter<Question, Double> {

	/**
	 * A set of terms to search for. All these terms must be found (AND search).
	 * Set to <code>null</code> for filtering out questions with partially
	 * overlapping tag sets.
	 */
	private final Set<String> queryFulltext;

	/**
	 * A set of tags to match. The more tags overlap, the higher a question will
	 * be rated.
	 */
	private final Set<Tag> queryTags;

	/**
	 * Instantiates a new search filter.
	 * 
	 * @param query
	 *            all the search terms a question must match (resp. its answers
	 *            or their owner's usernames). In order to force a term to only
	 *            match tags, prepend it with "tag:". If <code>query</code> is
	 *            <code>null</code>, all questions matching at least one of the
	 *            tags will be kept, ordered by the tag matching ratio.
	 * 
	 *            Pass in <code>null</code> in order to find all questions with
	 *            at least partially overlapping tag sets.
	 * @param tags
	 *            a list of tags. The more of these a question matches, the
	 *            higher it's rated.
	 */
	public SearchFilter(Set<String> query, Set<Tag> tags) {
		this.queryFulltext = query != null ? difference(query, StopWords.get())
				: null;
		this.queryTags = tags;
	}

	/**
	 * Rates a question as to how well it matches the given search terms (if at
	 * all) and as to how well its tags overlap with the set of given tags.
	 * 
	 * @param question
	 *            the question to rate
	 * @return a value between -1 (complete match) and 0 (failed to match),
	 *         where 0 is replaced with <code>null</code> in order to allow
	 *         filtering out non-matching questions
	 */
	public Double visit(Question question) {
		Set<String> mustHave = new HashSet<String>(
				this.queryFulltext == null ? Collections.EMPTY_SET
						: this.queryFulltext);
		double tagRating = rateTags(question, mustHave);
		double textRating = rateText(question, mustHave);
		double answerRating = rateAnswers(question, mustHave);
		double rating = tagRating + textRating + answerRating;
		// all search terms must appear at least once (AND search)
		if (mustHave.size() != 0)
			return null;
		// best matching questions should appear first in an ascending sort
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
			// search terms prepended with "tag:" won't match any content
			// and are thus guaranteed to only match tags (and maybe very odd
			// usernames)
			mustHave.remove("tag:" + tag.getName());
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
		String content = entry.getContentText();
		if (entry.owner() != null)
			content += " " + entry.owner().getName();
		if (this.queryFulltext == null)
			return 0;
		Set<String> words = getWords(content);
		if (words.isEmpty())
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
		Set<String> words = new HashSet<String>();
		for (String word : string.split("\\W+")) {
			words.add(word.toLowerCase());
		}
		words.remove(""); // remove splitting artifact
		return difference(words, StopWords.get());
	}
}
