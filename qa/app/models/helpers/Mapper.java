package models.helpers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A helper class for sorting/filtering <code>Iterable</code>s.
 */
public class Mapper {
	/**
	 * Return a <code>Map</code>'s key sorted in order of the corresponding
	 * values (which must be Comparable).
	 * 
	 * Code adapted from a forum post retrieved 2010-11-01 from
	 * http://www.programmersheaven.com/download/49349/download.aspx
	 * 
	 * @param map
	 *            the map whose keys are to be sorted
	 * @return the sorted list of keys
	 */
	private static <T> List<T> sortByValue(Map<T, Comparable> map) {
		List<Map.Entry> list = new ArrayList(map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry>() {
			public int compare(Map.Entry e1, Map.Entry e2) {
				return ((Comparable) e1.getValue()).compareTo(e2.getValue());
			}
		});
		List<T> result = new ArrayList();
		for (Map.Entry<T, Comparable> entry : list) {
			result.add(entry.getKey());
		}
		return result;
	}

	/**
	 * Sort an <code>Iterable</code> according to a sorting <code>Filter</code>.
	 * 
	 * @param iterable
	 *            the iterable whose values are to be sorted.
	 * @param filter
	 *            the sorting filter to be used for generating sort keys
	 * @return the sorted list
	 */
	public static <T> List<T> sort(Iterable<T> iterable,
			IFilter<T, ? extends Comparable> filter) {
		Map<T, Comparable> map = new HashMap();
		for (T object : iterable) {
			Comparable value = filter.visit(object);
			if (value != null) {
				map.put(object, value);
			}
		}
		return sortByValue(map);
	}

	/**
	 * Filter objects out of an <code>Iterable</code>.
	 * 
	 * @param iterable
	 *            the iterable to filter through
	 * @param filter
	 *            the filter to be used for deciding which objects to keep
	 * @return the filtered list
	 */
	public static <T> List<T> filter(Iterable<T> iterable,
			IFilter<T, Boolean> filter) {
		List<T> result = new ArrayList();
		for (T object : iterable) {
			if (filter.visit(object)) {
				result.add(object);
			}
		}
		return result;
	}
}
