package models.database.importers;

import java.util.HashMap;
import java.util.Map;

public class Syntax {
	private String name;
	private Syntax parent;
	private Map<String, Syntax> subsyntax;
	private Action callback;

	private Syntax(String name, Syntax parent) {
		this.name = name;
		this.parent = parent;
		this.subsyntax = new HashMap<String, Syntax>();
	}

	public String getName() {
		return this.name;
	}

	public Syntax get(String tag) throws SemanticError {
		Syntax syntax = this.subsyntax.get(tag);
		if (syntax == null)
			throw new SemanticError();
		return this.subsyntax.get(tag);
	}

	public String toString() {
		return "S[" + this.name + "](" + this.subsyntax.toString() + ")";
	}

	public Syntax(String name) {
		this(name, null);
	}

	public Syntax by(String tag) {
		Syntax sub = new Syntax(tag, this);
		this.subsyntax.put(tag, sub);
		return sub;
	}

	public Syntax read(String tag) {
		Syntax sub = new Syntax(tag, this);
		this.subsyntax.put(tag, sub);
		return this;
	}

	public Syntax end() {
		return this.parent;
	}

	public ElementParser done() {
		return new ElementParser(this);
	}

	public Syntax getParent() {
		return this.parent;
	}

	public Syntax call(Action action) {
		this.callback = action;
		return this;
	}

	public boolean hasCallback() {
		return this.callback != null;
	}

	public void callback(Element elt) {
		if (hasCallback()) {
			this.callback.call(elt);
		}
	}
}
