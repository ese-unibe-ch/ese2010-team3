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
	public static List sortByValue(Map map) {
		List<Map.Entry> list = new ArrayList(map.entrySet());
		Collections.sort(list, new Comparator() {
			public int compare(Object o1, Object o2) {
				return ((Comparable) ((Map.Entry) (o1)).getValue())
						.compareTo(((Map.Entry) (o2)).getValue());
			}
		});
		List result = new ArrayList();
		for (Map.Entry entry : list)
			result.add(entry.getKey());
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
	public static List sort(Iterable iterable, Filter filter) {
		Map map = new HashMap();
		for (Object object : iterable) {
			Object value = filter.visit(object);
			if (value != null)
				map.put(object, value);
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
	 *            (return <code>null</code> or <code>false</code> to drop an
	 *            object)
	 * @return the filtered list
	 */
	public static List filter(Iterable iterable, Filter filter) {
		List result = new ArrayList();
		for (Object object : iterable) {
			Object value = filter.visit(object);
			if (value != null
					&& !(object instanceof Boolean && object.equals(false)))
				result.add(object);
		}
		return result;
	}
}
