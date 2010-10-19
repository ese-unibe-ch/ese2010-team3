package models;

import java.util.Comparator;

/**
 * Compares two <code>Entrys</code> by their rating.
 */
public class EntryComperator implements Comparator {

	public int compare(Object o1, Object o2) {
		if ((o1 instanceof Entry) && (o2 instanceof Entry)){
			Entry entry1 = (Entry) o1;
			Entry entry2 = (Entry) o2;
			if ((entry1 instanceof Answer) && (entry2 instanceof Answer)){
				Answer answer1 = (Answer) entry1;
				Answer answer2 = (Answer) entry2;
				if (answer1.isBestAnswer())
					return -1;
				else if (answer2.isBestAnswer())
					return 1;
			}
			return entry2.rating() - entry1.rating();
		}
		return 0;
	}

}
