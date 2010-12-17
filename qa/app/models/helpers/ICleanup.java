package models.helpers;

/**
 * Interface for an object aggregating other objects, so that these child
 * objects can tell the aggregating parent to clean up any state associated with
 * the child.
 * 
 * @param <T>
 *            the type of the child object
 */
public interface ICleanup<T> {

	/**
	 * Tells an aggregating object to clean up any state associated with the
	 * passed in object.
	 * 
	 * @param object
	 *            the object to clean up after
	 */
	public void cleanUp(T object);
}
