package models.helpers;

/**
 * Interface for visitors for objects (of type T) to be filtered or sorted
 * according to a value (of type V) calculated for these objects by
 * <code>visit</code>.
 * 
 * A <code>Filter</code> is supposed to return a <code>Comparable</code> value
 * for objects to be sorted and <code>null</code> for objects to be filtered
 * while sorting.
 * 
 * Use Mapper.filter and Mapper.sort for filtering resp. sorting
 * <code>Iterable</code>s.
 * 
 * @param <T>
 *            the object type
 * @param <V>
 *            the filter/sort value type
 */
public interface Filter<T, V> {

	/**
	 * Visit an object, returning either a sort key or a (non-)null value for
	 * filtering.
	 * 
	 * @param object
	 *            the object to be processed
	 * @return a sort key or (non-)null filter value
	 */
	public V visit(T object);
}
