package models.helpers;

import java.util.LinkedList;
import java.util.List;

/**
 * Iterates over an Iterable calling its <code>visit</code>, collecting the returned values. <code>null</code> however 
 * designates a value, that should not be added.
 * @author aaron
 *
 * @param <ReturnType> Output of <code>visit</code>.
 * @param <InputType> Input of <code>visit</code> and the type of the Iterator, the iterable returns.
 */
public abstract class Visitor<ReturnType,InputType> {
	protected abstract ReturnType visit(InputType i);
	
	public List<ReturnType> over(Iterable<InputType> collection) {
		List<ReturnType> results = new LinkedList<ReturnType>();
		for(InputType i : collection) {
			ReturnType result = visit(i);
			if (result != null){
				results.add(result);
			}
		}
		return results;
	}
}
