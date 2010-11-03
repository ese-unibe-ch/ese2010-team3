package models.helpers;

import java.util.Comparator;
import java.util.Map;

public class MapComparator<T> implements Comparator<T> {

	private Map<T, ? extends Comparable> map;

	public MapComparator(Map<T, ? extends Comparable> mapping) {
		map = mapping;
	}

	public int compare(T arg0, T arg1) {
		return map.get(arg0).compareTo(map.get(arg1));
	}

}
