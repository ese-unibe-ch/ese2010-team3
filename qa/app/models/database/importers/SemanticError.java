package models.database.importers;

/**
 * A SemanticError is thrown when an XML file either doesn't match the expected
 * {@link Syntax} or its contents can't be converted into valid data structures
 * due to invalid values (such as ID references not matching any object's ID,
 * etc.).
 */
public class SemanticError extends IllegalArgumentException {

	/**
	 * Instantiates a new semantic error.
	 */
	public SemanticError() {
		super();
	}

	/**
	 * Instantiates a new semantic error.
	 * 
	 * @param string
	 *            a message to be associated with this error
	 */
	public SemanticError(String string) {
		super(string);
	}
}
