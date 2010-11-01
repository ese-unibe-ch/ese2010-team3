package models;

/**
 * An interface for allowing classes to register themselves with observables and
 * being notified about changes therein.
 */
public interface IObserver {

	/**
	 * This method is called when an observable is updated.
	 * 
	 * @param o
	 *            The observable notifying this observer.
	 * @param arg
	 *            Information about the update.
	 */
	public void observe(IObservable o, Object arg);

}
