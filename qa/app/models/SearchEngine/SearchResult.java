package models.SearchEngine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import models.Question;
import models.Tag;
import models.helpers.Pair;
import models.helpers.Visitor;

import static models.helpers.SetOperations.*;

public class SearchResult extends Visitor<Pair<Integer,Question>,Question> {
	private final Set<String> queryWords;
	public SearchResult(String query) {
		queryWords = filterWords(getWords(query));
		
	}
	
	@Override
	protected Pair<Integer, Question> visit(Question question) {
		Integer tagRating = rateTags(question), 
				textRating = rateText(question);
		Integer rating = combine(tagRating,textRating);
		if (rating != 0) {
			return new Pair<Integer,Question>(rating,question);
		}
		else {
			return null;
		}
	}
	
	private Set<String> getWords(String string) {
		Set<String> words = new HashSet<String>();
		for ( String word : string.replaceAll("\\W"," ").split("\\s+") ) {
			words.add(word);
		}
		return words;
	}


	private Integer combine(Integer tagRating, Integer textRating) {
		return tagRating+textRating;
	}

	private Integer rateTags(Question question) {
		Set<String> tags = new HashSet<String>();
		for (Tag tag : question.getTags()) {
			tags.add(tag.getName());
		}
		return 100*intersection(tags,queryWords).size()/notZero(queryWords.size());
	}
	
	private Integer notZero(Integer x){
		return x<1? 1 : x;
	}

	private Integer rateText(Question question) {
		Set<String> words = filterWords(getWords(question.content()));
		return 100*intersection(words,queryWords).size()/notZero(queryWords.size());
	}

	private Set<String> filterWords(Set<String> words) {
		return difference(words,StopWords.get());
	}

}
