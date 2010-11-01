package models.database;


/**
 * Database accessor. Can be swapped if necessary.  
 *
 */
public class Database{
	private static IDatabase instance = new HotDatabase();
	
	/** Gain access to the database.
	 * 
	 * @return the database that is currently responsible.
	 */
	public IDatabase get() {
		return instance;
	}
	
	/**
	 * Exchanges the Database. Useful for mocking or hot replacement to other engine.
	 * @param d The fully functional database to take responsibility.
	 */
	public void swapWith(IDatabase d) {
		instance = d;
	}
}
