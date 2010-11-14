package models.database.importers;

/**
 * Action to be executed on an Element.
 * 
 * @author aaron
 * 
 */
public interface Action {
	public void call(Element e) throws SemanticError;
}
