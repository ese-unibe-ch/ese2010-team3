package models.helpers;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

/**
 * {@link http://www.java2s.com/Code/Java/Collections-Data-Structure/
 * Setoperationsunionintersectiondifferencesymmetricdifferenceissubsetissuperset
 * .htm}
 * 
 */
public class SetOperations {
	public static <T> Set<T> union(Set<T> setA, Set<T> setB) {
		Set<T> tmp = new TreeSet<T>(setA);
		tmp.addAll(setB);
		return tmp;
	}

	public static <T> Set<T> intersection(Set<T> setA, Set<T> setB) {
		Set<T> tmp = new TreeSet<T>();
		for (T x : setA)
			if (setB.contains(x)) {
				tmp.add(x);
			}
		return tmp;
	}

	public static <T> Set<T> difference(Set<T> setA, Set<T> setB) {
		Set<T> tmp = new TreeSet<T>(setA);
		tmp.removeAll(setB);
		return tmp;
	}

	public static <T> Set<T> symDifference(Set<T> setA, Set<T> setB) {
		Set<T> tmpA;
		Set<T> tmpB;

		tmpA = union(setA, setB);
		tmpB = intersection(setA, setB);
		return difference(tmpA, tmpB);
	}

	public static <T> boolean isSubset(Set<T> setA, Set<T> setB) {
		return setB.containsAll(setA);
	}

	public static <T> boolean isSuperset(Set<T> setA, Set<T> setB) {
		return setA.containsAll(setB);
	}

	public static <T> boolean arrayEquals(T[] a, T[] b) {
		if (a.length != b.length)
			return false;
		for (int i = 0; i < a.length; i++) {
			if (!a[i].equals(b[i]))
				return false;
		}
		return true;
	}

	public static <T> boolean containsAny(Collection<T> c1, Collection<T> c2) {
		for (T t : c1) {
			if (c2.contains(t))
				return true;
		}
		return false;
	}
}
