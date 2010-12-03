package models.database.importers;

import java.util.HashMap;
import java.util.Map;

/**
 * Definition of a (XML) syntax. Gives a fluent interface to create a
 * <ul>
 * <li>simple,
 * <li>non-recursing,
 * <li>unordered
 * </ul>
 * xml reader.<br/>
 * 
 * Refer to {@link XMLParser#getSyntax} for an example.
 * 
 * @author aaron
 * 
 */
public class Syntax {
	private final String name;
	private final Syntax parent;
	private final Map<String, Syntax> subsyntax;
	private Action callback;

	private Syntax(String name, Syntax parent) {
		this.name = name;
		this.parent = parent;
		this.subsyntax = new HashMap<String, Syntax>();
	}

	public String getName() {
		return this.name;
	}

	/**
	 * Tries to accept the tag. That means, it checks, whether such a tag is
	 * allowed and
	 * throws a <code>SemanticError()</code> if it's not. The fluent interface
	 * works with return values: <br/>
	 * <code>syntax.get("user").get("name")</code>
	 * 
	 * @param tag
	 * @return
	 * @throws SemanticError
	 */
	public Syntax get(String tag) throws SemanticError {
		Syntax syntax = this.subsyntax.get(tag);
		if (syntax == null)
			throw new SemanticError();
		return this.subsyntax.get(tag);
	}

	@Override
	public String toString() {
		return "S[" + this.name + "](" + this.subsyntax.toString() + ")";
	}

	public Syntax(String name) {
		this(name, null);
	}

	/**
	 * Defines a subsyntax. In here, there can be further subsyntaxes and/or
	 * reads nested.
	 * 
	 * @param tag
	 * @return
	 */
	public Syntax by(String tag) {
		Syntax sub = new Syntax(tag, this);
		this.subsyntax.put(tag, sub);
		return sub;
	}

	/**
	 * Defines a simple shallow field. There are no subfields and just a text
	 * read.
	 * 
	 * @param tag
	 * @return
	 */
	public Syntax read(String tag) {
		Syntax sub = new Syntax(tag, this);
		this.subsyntax.put(tag, sub);
		return this;
	}

	/**
	 * Ends the current subsyntax. It is not necessary to close all of them in
	 * the very end.
	 * 
	 * @return
	 */
	public Syntax end() {
		return this.parent;
	}

	/**
	 * Gives a Parser that implements this syntax.
	 * 
	 * @return
	 */
	public ElementParser done() {
		return new ElementParser(this);
	}

	/**
	 * returns the supersyntax of this one.
	 * 
	 * @return
	 */
	public Syntax getParent() {
		return this.parent;
	}

	/**
	 * Defines an Action to be executed, once this syntax (ie this tag) is
	 * closed.
	 * 
	 * @param action
	 * @return
	 */
	public Syntax call(Action action) {
		this.callback = action;
		return this;
	}

	/**
	 * 
	 * @return true iff an Action is to be executed on the end of this tag.
	 */
	public boolean hasCallback() {
		return this.callback != null;
	}

	/**
	 * Performs the callback, after the tag is closed, taking the produced
	 * Element as input.
	 * 
	 * @param elt
	 * @throws SemanticError
	 */
	public void callback(Element elt) throws SemanticError {
		if (hasCallback()) {
			this.callback.call(elt);
		}
	}
}
