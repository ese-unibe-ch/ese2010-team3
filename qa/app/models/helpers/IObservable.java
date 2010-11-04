package models.helpers;

/**
 * An interface for Objects that can be changed in a way that may interest other
 * Objects. These other objects can implement IObserver, register themselves
 * with an IObservable and will be notified about all changes until they
 * unregister.
 */
public interface IObservable {

	/**
	 * Adds an observer.
	 * 
	 * @param o
	 *            the observer to be added
	 */
	public void addObserver(IObserver o);

	/**
	 * Removes an observer.
	 * 
	 * @param o
	 *            the observer to be removed
	 */
	public void removeObserver(IObserver o);

	/**
	 * Checks for observer.
	 * 
	 * @param o
	 *            the observer to check for
	 * @return true, if the observer is currently observing this observable
	 */
	public boolean hasObserver(IObserver o);

	/**
	 * Notify observers.
	 * 
	 * @param arg
	 *            additional information to be passed on to all observers
	 */
	public void notifyObservers(Object arg);
}
